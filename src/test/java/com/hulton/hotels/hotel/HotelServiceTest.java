package com.hulton.hotels.hotel;

import static com.hulton.hotels.support.Entities.hotel;
import static com.hulton.hotels.support.Entities.room;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

	private static final LocalDate CHECKIN = LocalDate.of(2026, 11, 15);

	@Mock
	private HotelRepository hotels;

	@Mock
	private RoomRepository rooms;

	@Mock
	private BreakfastRepository breakfasts;

	@Mock
	private ServiceOfferingRepository services;

	@InjectMocks
	private HotelService service;

	@Test
	void popularRanksByBookingsThenRatingWithUnratedLast() {
		HotelCard quiet = card(1, "Toronto", 1L, 3.0);
		HotelCard busy = card(2, "Miami Beach", 5L, 1.0);
		HotelCard busyBetter = card(3, "New York", 5L, 2.5);
		HotelCard busyUnrated = card(4, "San Francisco", 5L, null);
		given(this.hotels.findCards()).willReturn(List.of(quiet, busy, busyBetter, busyUnrated));

		assertThat(this.service.popular(3)).containsExactly(busyBetter, busy, busyUnrated);
	}

	@Test
	void searchGroupsFreeRoomsByHotelAndAppliesThePriceLimitAfterDiscounts() {
		HotelSearch search = search(2);
		search.setMaxPrice(new BigDecimal("150"));
		AvailableRoom discounted = new AvailableRoom(room(1, 101, "standard", "180.00", 2), 20);
		AvailableRoom tooExpensive = new AvailableRoom(room(1, 102, "standard", "180.00", 2), null);
		AvailableRoom cheap = new AvailableRoom(room(2, 101, "standard", "129.00", 2), null);
		given(this.rooms.findAvailable(null, null, null, 1, CHECKIN, CHECKIN.plusDays(2)))
			.willReturn(List.of(discounted, tooExpensive, cheap));
		HotelCard first = card(1, "New York", 3L, 2.0);
		HotelCard second = card(2, "Toronto", 1L, 3.0);
		HotelCard noRooms = card(3, "Miami Beach", 9L, 3.0);
		given(this.hotels.findCards()).willReturn(List.of(second, first, noRooms));

		List<HotelResult> results = this.service.search(search);

		assertThat(results).extracting(HotelResult::card).containsExactly(first, second);
		assertThat(results.get(0).rooms()).containsExactly(discounted);
		assertThat(results.get(0).lowestRate()).isEqualByComparingTo("144.00");
		assertThat(results.get(0).bestDiscount()).isEqualTo(20);
	}

	@Test
	void bookedRoomsShowTheNextStayOfTheSameLengthEachRoomIsFreeFor() {
		Room suite401 = room(3, 401, "suite", "442.90", 5);
		Room suite402 = room(3, 402, "suite", "442.90", 5);
		Room suite403 = room(3, 403, "suite", "442.90", 5);
		given(this.rooms.findMatching(3, null, "suite", 1)).willReturn(List.of(suite401, suite402, suite403));
		given(this.rooms.findStaysEndingAfter(3, CHECKIN)).willReturn(List.of(
				new BookedStay(3, 402, CHECKIN.minusDays(2), CHECKIN.plusDays(4)),
				new BookedStay(3, 401, CHECKIN.minusDays(1), CHECKIN.plusDays(3))));
		HotelSearch search = search(2);
		search.setRoomtype("suite");

		List<NextAvailable> booked = this.service.bookedRooms(3, search);

		assertThat(booked).extracting((next) -> next.room().getRoomNo()).containsExactly(401, 402);
		assertThat(booked.get(0).checkin()).isEqualTo(CHECKIN.plusDays(3));
		assertThat(booked.get(0).checkout()).isEqualTo(CHECKIN.plusDays(5));
		assertThat(booked.get(1).checkin()).isEqualTo(CHECKIN.plusDays(4));
	}

	@Test
	void soldOutListsOnlyHotelsWithoutFreeRoomsWithTheirSoonestStay() {
		Room booked = room(1, 101, "standard", "129.00", 2);
		Room free = room(2, 101, "standard", "129.00", 2);
		given(this.rooms.findMatching(null, null, null, 1)).willReturn(List.of(booked, free));
		given(this.rooms.findStaysEndingAfter(isNull(), eq(CHECKIN)))
			.willReturn(List.of(new BookedStay(1, 101, CHECKIN, CHECKIN.plusDays(6))));
		HotelCard soldOut = card(1, "New York", 3L, 2.0);
		HotelCard available = card(2, "Toronto", 1L, 3.0);
		given(this.hotels.findCards()).willReturn(List.of(soldOut, available));
		HotelResult availableResult = new HotelResult(available, List.of(new AvailableRoom(free, null)));

		List<SoldOutHotel> result = this.service.soldOut(search(2), List.of(availableResult));

		assertThat(result).singleElement().satisfies((hotel) -> {
			assertThat(hotel.card()).isEqualTo(soldOut);
			assertThat(hotel.checkin()).isEqualTo(CHECKIN.plusDays(6));
			assertThat(hotel.checkout()).isEqualTo(CHECKIN.plusDays(8));
		});
	}

	@Test
	void availableRoomChecksTheGuestsFitAndPicksTheRequestedRoom() {
		AvailableRoom wanted = new AvailableRoom(room(1, 102, "double", "169.00", 4), null);
		given(this.rooms.findAvailable(eq(1), isNull(), isNull(), anyInt(), any(), any()))
			.willReturn(List.of(new AvailableRoom(room(1, 101, "double", "169.00", 4), null), wanted));
		HotelSearch search = search(2);
		search.setGuests(3);

		assertThat(this.service.availableRoom(1, 102, search)).contains(wanted);
		assertThat(this.service.availableRoom(1, 999, search)).isEmpty();
	}

	@Nested
	class FirstFreeCheckin {

		@Test
		void isTheRequestedDateWithoutReservations() {
			assertThat(HotelService.firstFreeCheckin(List.of(), CHECKIN, 2)).isEqualTo(CHECKIN);
		}

		@Test
		void isWhenAnOverlappingStayEnds() {
			List<BookedStay> stays = List.of(stay(-1, 3));
			assertThat(HotelService.firstFreeCheckin(stays, CHECKIN, 2)).isEqualTo(CHECKIN.plusDays(3));
		}

		@Test
		void allowsCheckingOutTheDayTheNextGuestChecksIn() {
			List<BookedStay> stays = List.of(stay(2, 5));
			assertThat(HotelService.firstFreeCheckin(stays, CHECKIN, 2)).isEqualTo(CHECKIN);
		}

		@Test
		void skipsGapsTooShortForTheStay() {
			List<BookedStay> stays = List.of(stay(0, 3), stay(4, 6), stay(9, 10));
			assertThat(HotelService.firstFreeCheckin(stays, CHECKIN, 1)).isEqualTo(CHECKIN.plusDays(3));
			assertThat(HotelService.firstFreeCheckin(stays, CHECKIN, 3)).isEqualTo(CHECKIN.plusDays(6));
			assertThat(HotelService.firstFreeCheckin(stays, CHECKIN, 4)).isEqualTo(CHECKIN.plusDays(10));
		}

		@Test
		void handlesStaysThatOverlapEachOther() {
			List<BookedStay> stays = List.of(stay(0, 5), stay(1, 3));
			assertThat(HotelService.firstFreeCheckin(stays, CHECKIN, 1)).isEqualTo(CHECKIN.plusDays(5));
		}

		private static BookedStay stay(int inOffset, int outOffset) {
			return new BookedStay(1, 101, CHECKIN.plusDays(inOffset), CHECKIN.plusDays(outOffset));
		}

	}

	private static HotelSearch search(int nights) {
		HotelSearch search = new HotelSearch();
		search.setCheckin(CHECKIN);
		search.setCheckout(CHECKIN.plusDays(nights));
		return search;
	}

	private static HotelCard card(int id, String city, Long bookings, Double rating) {
		return new HotelCard(hotel(id, city), new BigDecimal("129.00"), rating, 0L, bookings);
	}

}
