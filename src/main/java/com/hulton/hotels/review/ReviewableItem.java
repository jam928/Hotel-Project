package com.hulton.hotels.review;

import java.time.LocalDate;

/**
 * Something the customer reserved and can review. {@code roomtype} is only set for
 * rooms.
 */
public record ReviewableItem(String item, LocalDate reservedOn, Integer hotelId, String roomtype) {

	public ReviewableItem(String item, LocalDate reservedOn, Integer hotelId) {
		this(item, reservedOn, hotelId, null);
	}

}
