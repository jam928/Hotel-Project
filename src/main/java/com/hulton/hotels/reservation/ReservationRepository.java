package com.hulton.hotels.reservation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

	@Query("""
			select new com.hulton.hotels.reservation.ReservationSummary(
			  r.invoiceNo, r.roomNo, room.description, r.hotelId, r.inDate, r.outDate)
			from Reservation r join Room room on room.roomNo = r.roomNo and room.hotelId = r.hotelId
			where r.customerId = :customerId
			order by r.inDate desc
			""")
	List<ReservationSummary> findSummariesByCustomerId(Integer customerId);

	/** The customer's reservations with what they paid and the card used, latest payment first. */
	@Query("""
			select new com.hulton.hotels.reservation.Payment(
			  r.invoiceNo, r.reservedOn, h.city, r.roomNo, r.inDate, r.outDate, r.totalAmount, c.type, c.number)
			from Reservation r
			join Hotel h on h.id = r.hotelId
			join CreditCard c on c.number = r.cardNumber
			where r.customerId = :customerId
			order by r.reservedOn desc, r.invoiceNo desc
			""")
	List<Payment> findPaymentsByCustomerId(Integer customerId);

	@Modifying
	@Query("delete from Reservation r where r.invoiceNo = :invoiceNo and r.customerId = :customerId")
	int deleteByInvoiceNoAndCustomerId(Integer invoiceNo, Integer customerId);

}
