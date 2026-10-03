package com.hulton.hotels.hotel;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {

	/** Every hotel with the figures for its card (see {@link HotelCard}). */
	@Query(CARD_QUERY + " order by h.city")
	List<HotelCard> findCards();

	@Query(CARD_QUERY + " where h.id = :hotelId")
	Optional<HotelCard> findCard(Integer hotelId);

	String CARD_QUERY = """
			select new com.hulton.hotels.hotel.HotelCard(h,
			  (select min(r.price) from Room r where r.hotelId = h.id),
			  (select avg(rv.rating) from Review rv where rv.hotelId = h.id),
			  (select count(rv) from Review rv where rv.hotelId = h.id),
			  (select count(res) from Reservation res where res.hotelId = h.id))
			from Hotel h
			""";

	@Query("""
			select new com.hulton.hotels.hotel.HotelReview(
			  rv.rating, rv.textComment, c.name, rv.roomNo, rv.breakfastType, rv.serviceType)
			from Review rv join Customer c on c.id = rv.customerId
			where rv.hotelId = :hotelId
			order by rv.id desc
			""")
	List<HotelReview> findReviews(Integer hotelId, Limit limit);

	/**
	 * For each hotel, the type of the highest-rated room reserved in the given period.
	 * Native because it needs a per-hotel {@code LIMIT 1} subquery.
	 */
	@Query(nativeQuery = true, value = """
			SELECT hotel.HotelID AS hotelId, hotel.Street AS street,
			  (SELECT room.Roomtype
			   FROM room
			   INNER JOIN review ON room.Room_no = review.Room_no AND room.HotelID = review.HotelID
			   INNER JOIN reservation ON room.Room_no = reservation.Room_no AND room.HotelID = reservation.HotelID
			   WHERE room.HotelID = hotel.HotelID
			     AND reservation.InDate >= :beginDate
			     AND reservation.OutDate <= :endDate
			   ORDER BY review.Rating DESC LIMIT 1) AS roomtype
			FROM hotel
			""")
	List<HighestRatedRoom> findHighestRatedRoomTypes(LocalDate beginDate, LocalDate endDate);

	interface HighestRatedRoom {

		Integer getHotelId();

		String getStreet();

		String getRoomtype();

	}

}
