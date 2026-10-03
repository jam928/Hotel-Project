package com.hulton.hotels.hotel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * The filters on the search and hotel pages, bound from the query string. Every
 * field is optional; {@link #normalize} fills in sensible dates.
 */
@Data
public class HotelSearch {

	/** City, or blank for every city. */
	private String destination;

	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate checkin;

	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate checkout;

	private int guests = 1;

	/** Room type, or blank for any. */
	private String roomtype;

	/** Highest price per night after discounts, or {@code null} for no limit. */
	private BigDecimal maxPrice;

	/**
	 * Replaces missing or impossible values: check-in defaults to tomorrow and may
	 * not be in the past, and the stay is at least one night.
	 */
	public void normalize(LocalDate today) {
		if (this.checkin == null || this.checkin.isBefore(today)) {
			this.checkin = today.plusDays(1);
		}
		if (this.checkout == null || !this.checkout.isAfter(this.checkin)) {
			this.checkout = this.checkin.plusDays(1);
		}
		this.guests = Math.clamp(this.guests, 1, 6);
		this.destination = blankToNull(this.destination);
		this.roomtype = blankToNull(this.roomtype);
	}

	public long nights() {
		return ChronoUnit.DAYS.between(this.checkin, this.checkout);
	}

	private static String blankToNull(String value) {
		return (value == null || value.isBlank()) ? null : value;
	}

}
