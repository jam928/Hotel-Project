package com.hulton.hotels.reservation;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

/** The card networks accepted, with the code stored in creditcard.Type. */
public enum CardType {

	VISA("VISA", "Visa", "4\\d{12}(\\d{3})?"),
	MASTERCARD("MSTR", "Mastercard", "(5[1-5]\\d{2}|2(2[2-9]\\d|[3-6]\\d{2}|7[01]\\d|720))\\d{12}"),
	AMEX("AMEX", "American Express", "3[47]\\d{13}"),
	DISCOVER("DISC", "Discover", "(6011|65\\d{2}|64[4-9]\\d)\\d{12}");

	private final String code;

	private final String label;

	private final Pattern number;

	CardType(String code, String label, String number) {
		this.code = code;
		this.label = label;
		this.number = Pattern.compile(number);
	}

	public String code() {
		return this.code;
	}

	public String label() {
		return this.label;
	}

	/** The label for a creditcard.Type code, or the code itself if it is not one we know. */
	public static String labelOf(String code) {
		return Arrays.stream(values())
			.filter((type) -> type.code.equals(code))
			.findFirst()
			.map(CardType::label)
			.orElse(code);
	}

	/** American Express prints a 4-digit code on the front; the others a 3-digit one on the back. */
	public int securityCodeLength() {
		return (this == AMEX) ? 4 : 3;
	}

	/** The network of a card number (digits only), if it is one we accept and passes the Luhn check. */
	public static Optional<CardType> of(String digits) {
		if (!passesLuhn(digits)) {
			return Optional.empty();
		}
		return Arrays.stream(values()).filter((type) -> type.number.matcher(digits).matches()).findFirst();
	}

	private static boolean passesLuhn(String digits) {
		int sum = 0;
		for (int i = 0; i < digits.length(); i++) {
			int digit = digits.charAt(digits.length() - 1 - i) - '0';
			if (i % 2 == 1) {
				digit *= 2;
				if (digit > 9) {
					digit -= 9;
				}
			}
			sum += digit;
		}
		return !digits.isEmpty() && sum % 10 == 0;
	}

}
