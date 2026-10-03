package com.hulton.hotels.hotel;

import java.math.BigDecimal;

/**
 * A hotel with the figures shown on its card: the cheapest room, its review
 * rating (1–3, {@code null} without reviews) and how often it has been booked.
 */
public record HotelCard(Hotel hotel, BigDecimal fromPrice, Double rating, Long reviewCount, Long bookingCount) {

	/** The rating as a word, matching the labels on the review form. */
	public String ratingLabel() {
		if (this.rating == null) {
			return "New";
		}
		return (this.rating >= 2.5) ? "Great" : (this.rating >= 1.5) ? "Good" : "Poor";
	}

}
