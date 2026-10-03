package com.hulton.hotels.hotel;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A room that is free for the requested stay, with the best discount on offer
 * during it ({@code null} when there is none).
 */
public record AvailableRoom(Room room, Integer discount) {

	/** The discount in percent, 0 when there is none. */
	public int discountPercent() {
		return (this.discount != null) ? this.discount : 0;
	}

	/** The price per night after the discount. */
	public BigDecimal nightlyRate() {
		return this.room.getPrice()
			.multiply(BigDecimal.valueOf(100 - discountPercent()))
			.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
	}

}
