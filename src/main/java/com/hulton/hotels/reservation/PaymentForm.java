package com.hulton.hotels.reservation;

import java.time.YearMonth;
import java.util.Optional;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.Errors;

/** The card details entered on the payment page. */
@Data
public class PaymentForm {

	@NotBlank
	@Size(max = 32)
	private String cardholderName;

	/** May contain spaces or dashes between groups of digits. */
	@NotBlank
	@Pattern(regexp = "[\\d -]{12,23}", message = "must be 12 to 16 digits")
	private String cardNumber;

	@NotBlank
	@Pattern(regexp = "(0[1-9]|1[0-2]) ?/ ?\\d{2}", message = "must look like MM/YY")
	private String expiry;

	@NotBlank
	@Pattern(regexp = "\\d{3,4}", message = "must be 3 or 4 digits")
	private String securityCode;

	@NotBlank
	@Size(max = 50)
	private String billingAddress;

	public String digits() {
		return this.cardNumber.replaceAll("[ -]", "");
	}

	public Optional<CardType> cardType() {
		return CardType.of(digits());
	}

	/** Only valid once the annotated constraints pass. */
	public YearMonth expiryMonth() {
		String[] parts = this.expiry.replace(" ", "").split("/");
		return YearMonth.of(2000 + Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
	}

	/** Checks that need more than one field or today's date, after the annotated constraints. */
	public void validate(Errors errors, YearMonth currentMonth) {
		if (!errors.hasFieldErrors("cardNumber")) {
			Optional<CardType> type = cardType();
			if (type.isEmpty()) {
				errors.rejectValue("cardNumber", "card.invalid",
						"is not a valid Visa, Mastercard, American Express or Discover number");
			}
			else if (!errors.hasFieldErrors("securityCode")
					&& this.securityCode.length() != type.get().securityCodeLength()) {
				errors.rejectValue("securityCode", "card.securityCode",
						"must be " + type.get().securityCodeLength() + " digits for " + type.get().label());
			}
		}
		if (!errors.hasFieldErrors("expiry") && expiryMonth().isBefore(currentMonth)) {
			errors.rejectValue("expiry", "card.expired", "this card has expired");
		}
	}

}
