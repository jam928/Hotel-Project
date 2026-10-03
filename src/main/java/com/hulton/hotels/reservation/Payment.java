package com.hulton.hotels.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;

/** A reservation the customer paid for, as shown in their payment history. */
public record Payment(Integer invoiceNo, LocalDate paidOn, String city, Integer roomNo, LocalDate inDate,
		LocalDate outDate, BigDecimal amount, String cardType, String cardNumber) {

	public String hotelName() {
		return "Hulton " + this.city;
	}

	/** e.g. "Visa •••• 4242". */
	public String cardLabel() {
		return CardType.labelOf(this.cardType) + " •••• " + CreditCard.lastFour(this.cardNumber);
	}

}
