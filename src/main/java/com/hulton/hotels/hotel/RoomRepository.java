package com.hulton.hotels.hotel;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoomRepository extends JpaRepository<Room, RoomId> {

	/**
	 * Rooms that match the filters and have no reservation overlapping the stay, each
	 * with the best discount on offer during it. A stay that ends on the day another
	 * begins does not overlap it. {@code null} filters match every room.
	 */
	@Query("""
			select new com.hulton.hotels.hotel.AvailableRoom(r,
			  (select max(o.discount) from OfferRoom o
			   where o.hotelId = r.hotelId and o.roomNo = r.roomNo
			     and o.startDate < :checkout and o.endDate >= :checkin))
			from Room r join Hotel h on h.id = r.hotelId
			where (:hotelId is null or r.hotelId = :hotelId)
			  and (:city is null or h.city = :city)
			  and (:roomtype is null or r.roomtype = :roomtype)
			  and r.capacity >= :guests
			  and not exists (
			    select 1 from Reservation res
			    where res.hotelId = r.hotelId and res.roomNo = r.roomNo
			      and res.inDate < :checkout and res.outDate > :checkin)
			order by r.hotelId, r.price, r.roomNo
			""")
	List<AvailableRoom> findAvailable(Integer hotelId, String city, String roomtype, int guests, LocalDate checkin,
			LocalDate checkout);

	/** Rooms that match the filters, whether or not they are free. {@code null} filters match every room. */
	@Query("""
			select r from Room r join Hotel h on h.id = r.hotelId
			where (:hotelId is null or r.hotelId = :hotelId)
			  and (:city is null or h.city = :city)
			  and (:roomtype is null or r.roomtype = :roomtype)
			  and r.capacity >= :guests
			order by r.hotelId, r.price, r.roomNo
			""")
	List<Room> findMatching(Integer hotelId, String city, String roomtype, int guests);

	/** Reservations still running on or after {@code from}, earliest check-in first. */
	@Query("""
			select new com.hulton.hotels.hotel.BookedStay(res.hotelId, res.roomNo, res.inDate, res.outDate)
			from Reservation res
			where (:hotelId is null or res.hotelId = :hotelId) and res.outDate > :from
			order by res.inDate
			""")
	List<BookedStay> findStaysEndingAfter(Integer hotelId, LocalDate from);

}
