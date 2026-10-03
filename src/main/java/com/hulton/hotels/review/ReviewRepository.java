package com.hulton.hotels.review;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

	List<Review> findByRoomNoAndHotelIdAndCustomerId(Integer roomNo, Integer hotelId, Integer customerId);

	List<Review> findByBreakfastTypeAndHotelIdAndCustomerId(String breakfastType, Integer hotelId,
			Integer customerId);

	List<Review> findByServiceTypeAndHotelIdAndCustomerId(String serviceType, Integer hotelId, Integer customerId);

	@Query("""
			select new com.hulton.hotels.review.ReviewableItem(cast(r.roomNo as String), r.reservedOn, r.hotelId, room.roomtype)
			from Reservation r join Room room on room.hotelId = r.hotelId and room.roomNo = r.roomNo
			where r.customerId = :customerId
			""")
	List<ReviewableItem> findReservedRooms(Integer customerId);

	@Query("""
			select new com.hulton.hotels.review.ReviewableItem(r.breakfastType, r.reservedOn, r.hotelId)
			from Reservation r join Breakfast b on b.type = r.breakfastType and b.hotelId = r.hotelId
			where r.customerId = :customerId
			""")
	List<ReviewableItem> findReservedBreakfasts(Integer customerId);

	@Query("""
			select new com.hulton.hotels.review.ReviewableItem(r.serviceType, r.reservedOn, r.hotelId)
			from Reservation r join ServiceOffering s on s.type = r.serviceType and s.hotelId = r.hotelId
			where r.customerId = :customerId
			""")
	List<ReviewableItem> findReservedServices(Integer customerId);

	/** Breakfast types of reviews tied to a stay that falls entirely within the period. */
	@Query("""
			select rv.breakfastType from Review rv
			where exists (
			  select 1 from Reservation r
			  where r.inDate between :beginDate and :endDate
			    and r.outDate between :beginDate and :endDate
			    and r.hotelId = rv.hotelId and r.roomNo = rv.roomNo)
			""")
	List<String> findReviewedBreakfastTypes(LocalDate beginDate, LocalDate endDate);

	/** Service types of reviews tied to a stay that falls entirely within the period. */
	@Query("""
			select rv.serviceType from Review rv
			where exists (
			  select 1 from Reservation r
			  where r.inDate between :beginDate and :endDate
			    and r.outDate between :beginDate and :endDate
			    and r.hotelId = rv.hotelId and r.roomNo = rv.roomNo)
			""")
	List<String> findReviewedServiceTypes(LocalDate beginDate, LocalDate endDate);

}
