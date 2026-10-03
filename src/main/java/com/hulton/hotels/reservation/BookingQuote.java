package com.hulton.hotels.reservation;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * A priced stay waiting for payment, kept in the session between the hotel page and
 * the payment page. {@code breakfastType} and {@code serviceType} (with their
 * prices) are {@code null} when not chosen.
 */
public record BookingQuote(int hotelId, String hotelName, String hotelLocation, int roomNo, String roomDescription,
		String roomtype, String roomImageKey, LocalDate checkin, LocalDate checkout, int guests, BigDecimal roomPrice, int discount,
		BigDecimal nightlyRate, String breakfastType, BigDecimal breakfastPrice, String serviceType,
		BigDecimal serviceCost) implements Serializable {

	public long nights() {
		return ChronoUnit.DAYS.between(this.checkin, this.checkout);
	}

	public BigDecimal roomTotal() {
		return this.nightlyRate.multiply(BigDecimal.valueOf(nights()));
	}

	/** What the discount saves over the whole stay. */
	public BigDecimal savings() {
		return this.roomPrice.subtract(this.nightlyRate).multiply(BigDecimal.valueOf(nights()));
	}

	/** Breakfast is charged per night. */
	public BigDecimal breakfastTotal() {
		return (this.breakfastPrice != null) ? this.breakfastPrice.multiply(BigDecimal.valueOf(nights()))
				: BigDecimal.ZERO;
	}

	/** A service is charged once per stay. */
	public BigDecimal serviceTotal() {
		return (this.serviceCost != null) ? this.serviceCost : BigDecimal.ZERO;
	}

	public BigDecimal total() {
		return roomTotal().add(breakfastTotal()).add(serviceTotal());
	}

	/** The request this quote was made for, to price it again at payment. */
	public BookingRequest toRequest() {
		return new BookingRequest(this.hotelId, this.roomNo, this.checkin, this.checkout, this.guests,
				this.breakfastType, this.serviceType);
	}

}
