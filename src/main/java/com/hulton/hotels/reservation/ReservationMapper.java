package com.hulton.hotels.reservation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(imports = java.time.LocalDate.class)
public interface ReservationMapper {

	@Mapping(target = "invoiceNo", ignore = true)
	@Mapping(target = "reservedOn", expression = "java(LocalDate.now())")
	@Mapping(target = "totalAmount", expression = "java(quote.total())")
	@Mapping(target = "inDate", source = "quote.checkin")
	@Mapping(target = "outDate", source = "quote.checkout")
	@Mapping(target = "numberOfDays", expression = "java((int) quote.nights())")
	Reservation toReservation(BookingQuote quote, Integer customerId, String cardNumber);

}
