package com.hulton.hotels.hotel;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Browsing hotels and finding rooms available for a stay.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelService {

	/** The values of the room.Roomtype enum, cheapest first. */
	public static final List<String> ROOM_TYPES = List.of("standard", "double", "deluxe", "suite");

	/** Most booked first; ties go to the better rated hotel. */
	private static final Comparator<HotelCard> POPULARITY = Comparator
		.comparing(HotelCard::bookingCount, Comparator.reverseOrder())
		.thenComparing(HotelCard::rating, Comparator.nullsLast(Comparator.reverseOrder()));

	private final HotelRepository hotels;

	private final RoomRepository rooms;

	private final BreakfastRepository breakfasts;

	private final ServiceOfferingRepository services;

	public List<HotelCard> popular(int limit) {
		return this.hotels.findCards().stream().sorted(POPULARITY).limit(limit).toList();
	}

	public Optional<HotelCard> card(int hotelId) {
		return this.hotels.findCard(hotelId);
	}

	/** Cities with a hotel, keyed by city with "City, State" labels, in alphabetical order. */
	public Map<String, String> destinations() {
		return this.hotels.findAll()
			.stream()
			.sorted(Comparator.comparing(Hotel::getCity))
			.collect(Collectors.toMap(Hotel::getCity, Hotel::getLocation, (first, second) -> first,
					LinkedHashMap::new));
	}

	/**
	 * Hotels with at least one room that matches the search, most popular first.
	 * The search must be {@linkplain HotelSearch#normalize normalized}.
	 */
	public List<HotelResult> search(HotelSearch search) {
		Map<Integer, List<AvailableRoom>> roomsByHotel = findAvailable(null, search).stream()
			.collect(Collectors.groupingBy((available) -> available.room().getHotelId()));
		return this.hotels.findCards()
			.stream()
			.filter((card) -> roomsByHotel.containsKey(card.hotel().getId()))
			.sorted(POPULARITY)
			.map((card) -> new HotelResult(card, roomsByHotel.get(card.hotel().getId())))
			.toList();
	}

	/**
	 * Hotels with rooms that match the search but none free for its dates, each with the
	 * earliest stay of the same length one of those rooms is free for, soonest first.
	 */
	public List<SoldOutHotel> soldOut(HotelSearch search, List<HotelResult> results) {
		Map<Integer, NextAvailable> soonestByHotel = findNextAvailable(null, search).stream()
			.collect(Collectors.toMap((next) -> next.room().getHotelId(), (next) -> next,
					(a, b) -> a.checkin().isAfter(b.checkin()) ? b : a));
		results.forEach((result) -> soonestByHotel.remove(result.card().hotel().getId()));
		return this.hotels.findCards()
			.stream()
			.filter((card) -> soonestByHotel.containsKey(card.hotel().getId()))
			.map((card) -> {
				NextAvailable next = soonestByHotel.get(card.hotel().getId());
				return new SoldOutHotel(card, next.checkin(), next.checkout());
			})
			.sorted(Comparator.comparing(SoldOutHotel::checkin))
			.toList();
	}

	/**
	 * The hotel's rooms that match the search but are booked for its dates, with the
	 * first stay of the same length each is free for, soonest first.
	 */
	public List<NextAvailable> bookedRooms(int hotelId, HotelSearch search) {
		return findNextAvailable(hotelId, search).stream()
			.sorted(Comparator.comparing(NextAvailable::checkin)
				.thenComparing((next) -> next.room().getPrice())
				.thenComparing((next) -> next.room().getRoomNo()))
			.toList();
	}

	/** Rooms of the hotel that match the search, cheapest first. */
	public List<AvailableRoom> availableRooms(int hotelId, HotelSearch search) {
		return findAvailable(hotelId, search);
	}

	/** The room if it is free for the whole stay and sleeps the guests. */
	public Optional<AvailableRoom> availableRoom(int hotelId, int roomNo, HotelSearch search) {
		return this.rooms
			.findAvailable(hotelId, null, null, search.getGuests(), search.getCheckin(), search.getCheckout())
			.stream()
			.filter((available) -> available.room().getRoomNo() == roomNo)
			.findFirst();
	}

	public List<Breakfast> breakfasts(int hotelId) {
		return this.breakfasts.findByHotelIdOrderByPrice(hotelId);
	}

	public List<ServiceOffering> services(int hotelId) {
		return this.services.findByHotelIdOrderByCost(hotelId);
	}

	public Optional<Breakfast> breakfast(int hotelId, String type) {
		return this.breakfasts.findById(new BreakfastId(type, hotelId));
	}

	public Optional<ServiceOffering> service(int hotelId, String type) {
		return this.services.findById(new ServiceOfferingId(type, hotelId));
	}

	public List<HotelReview> recentReviews(int hotelId, int limit) {
		return this.hotels.findReviews(hotelId, Limit.of(limit));
	}

	/**
	 * For each room matching the search that is booked for its dates, the first stay of
	 * the same number of nights, starting after the searched check-in, that fits between
	 * its reservations. The price limit is checked against the undiscounted price, as
	 * discounts depend on the dates.
	 */
	private List<NextAvailable> findNextAvailable(Integer hotelId, HotelSearch search) {
		String city = (hotelId != null) ? null : search.getDestination();
		Map<RoomId, List<BookedStay>> staysByRoom = this.rooms.findStaysEndingAfter(hotelId, search.getCheckin())
			.stream()
			.collect(Collectors.groupingBy((stay) -> new RoomId(stay.roomNo(), stay.hotelId())));
		return this.rooms.findMatching(hotelId, city, search.getRoomtype(), search.getGuests())
			.stream()
			.filter((room) -> search.getMaxPrice() == null || room.getPrice().compareTo(search.getMaxPrice()) <= 0)
			.map((room) -> {
				List<BookedStay> stays = staysByRoom.getOrDefault(new RoomId(room.getRoomNo(), room.getHotelId()),
						List.of());
				LocalDate checkin = firstFreeCheckin(stays, search.getCheckin(), search.nights());
				return new NextAvailable(room, checkin, checkin.plusDays(search.nights()));
			})
			.filter((next) -> next.checkin().isAfter(search.getCheckin()))
			.toList();
	}

	/**
	 * The earliest check-in on or after {@code from} for which a stay of {@code nights}
	 * ends before the next reservation begins. {@code stays} must be in check-in order.
	 */
	static LocalDate firstFreeCheckin(List<BookedStay> stays, LocalDate from, long nights) {
		LocalDate checkin = from;
		for (BookedStay stay : stays) {
			if (!stay.inDate().isBefore(checkin.plusDays(nights))) {
				break;
			}
			if (stay.outDate().isAfter(checkin)) {
				checkin = stay.outDate();
			}
		}
		return checkin;
	}

	private List<AvailableRoom> findAvailable(Integer hotelId, HotelSearch search) {
		return this.rooms
			.findAvailable(hotelId, (hotelId != null) ? null : search.getDestination(), search.getRoomtype(),
					search.getGuests(), search.getCheckin(), search.getCheckout())
			.stream()
			.filter((available) -> search.getMaxPrice() == null
					|| available.nightlyRate().compareTo(search.getMaxPrice()) <= 0)
			.toList();
	}

}
