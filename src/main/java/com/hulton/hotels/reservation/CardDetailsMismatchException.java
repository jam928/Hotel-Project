package com.hulton.hotels.reservation;

/** A card with this number is on file, but with a different expiry date or security code. */
public class CardDetailsMismatchException extends RuntimeException {

	public CardDetailsMismatchException() {
		super("The card details do not match the card on file");
	}

}
