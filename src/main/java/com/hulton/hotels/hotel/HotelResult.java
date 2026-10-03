package com.hulton.hotels.hotel;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/** A hotel in the search results with its rooms that match the search. */
public record HotelResult(HotelCard card, List<AvailableRoom> rooms) {

	public BigDecimal lowestRate() {
		return this.rooms.stream().map(AvailableRoom::nightlyRate).min(Comparator.naturalOrder()).orElseThrow();
	}

	public int bestDiscount() {
		return this.rooms.stream().mapToInt(AvailableRoom::discountPercent).max().orElse(0);
	}

}
