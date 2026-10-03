package com.hulton.hotels.reservation;

/** The room was booked by someone else, or the chosen extras no longer exist. */
public class RoomUnavailableException extends RuntimeException {

	public RoomUnavailableException() {
		super("The room is no longer available for those dates");
	}

}
