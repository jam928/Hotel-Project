package com.hulton.hotels.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

class PaymentFormTest {

	private static final YearMonth NOW = YearMonth.of(2026, 9);

	@Test
	void acceptsAValidCardWithSpacesInTheNumber() {
		PaymentForm form = form("4242 4242 4242 4242", "12/29", "123");
		Errors errors = validate(form);
		assertThat(errors.hasErrors()).isFalse();
		assertThat(form.digits()).isEqualTo("4242424242424242");
		assertThat(form.cardType()).contains(CardType.VISA);
		assertThat(form.expiryMonth()).isEqualTo(YearMonth.of(2029, 12));
	}

	@Test
	void acceptsACardExpiringThisMonth() {
		assertThat(validate(form("4242424242424242", "09/26", "123")).hasErrors()).isFalse();
	}

	@Test
	void rejectsAnExpiredCard() {
		Errors errors = validate(form("4242424242424242", "08/26", "123"));
		assertThat(errors.getFieldError("expiry").getCode()).isEqualTo("card.expired");
	}

	@Test
	void rejectsANumberThatFailsTheLuhnCheck() {
		Errors errors = validate(form("4242424242424241", "12/29", "123"));
		assertThat(errors.getFieldError("cardNumber").getCode()).isEqualTo("card.invalid");
	}

	@Test
	void requiresFourDigitSecurityCodeForAmex() {
		Errors errors = validate(form("378282246310005", "12/29", "123"));
		assertThat(errors.getFieldError("securityCode").getDefaultMessage()).contains("4 digits");
	}

	@Test
	void requiresThreeDigitSecurityCodeForVisa() {
		Errors errors = validate(form("4242424242424242", "12/29", "1234"));
		assertThat(errors.getFieldError("securityCode").getDefaultMessage()).contains("3 digits");
	}

	@Test
	void parsesExpiryWithSpacesAroundTheSlash() {
		assertThat(form("4242424242424242", "03 / 28", "123").expiryMonth()).isEqualTo(YearMonth.of(2028, 3));
	}

	private static PaymentForm form(String number, String expiry, String securityCode) {
		PaymentForm form = new PaymentForm();
		form.setCardholderName("Jane Doe");
		form.setCardNumber(number);
		form.setExpiry(expiry);
		form.setSecurityCode(securityCode);
		form.setBillingAddress("1 Main St");
		return form;
	}

	private static Errors validate(PaymentForm form) {
		Errors errors = new BeanPropertyBindingResult(form, "form");
		form.validate(errors, NOW);
		return errors;
	}

}
