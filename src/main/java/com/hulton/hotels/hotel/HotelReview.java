package com.hulton.hotels.hotel;

/**
 * A review as shown on a hotel's page. Exactly one of {@code roomNo},
 * {@code breakfastType} and {@code serviceType} is set.
 */
public record HotelReview(Integer rating, String comment, String customerName, Integer roomNo, String breakfastType,
		String serviceType) {

	/** Only the first name is shown publicly. */
	public String reviewer() {
		return this.customerName.split(" ", 2)[0];
	}

	public String subject() {
		if (this.roomNo != null) {
			return "Room " + this.roomNo;
		}
		return (this.breakfastType != null) ? this.breakfastType + " breakfast" : this.serviceType;
	}

}
