package com.hulton.hotels.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class CardTypeTest {

	@ParameterizedTest
	@CsvSource({ "4242424242424242, VISA", "4111111111111111, VISA", "4222222222222, VISA",
			"5555555555554444, MASTERCARD", "2223003122003222, MASTERCARD", "378282246310005, AMEX",
			"371449635398431, AMEX", "6011111111111117, DISCOVER", "6500000000000002, DISCOVER" })
	void recognizesTestCardsOfEachNetwork(String number, CardType expected) {
		assertThat(CardType.of(number)).contains(expected);
	}

	@ParameterizedTest
	@ValueSource(strings = { "4242424242424241", "5555555555554443", "1234567812345670", "3530111333300000", "" })
	void rejectsNumbersThatFailLuhnOrBelongToNoAcceptedNetwork(String number) {
		assertThat(CardType.of(number)).isEmpty();
	}

	@ParameterizedTest
	@CsvSource({ "AMEX, 4", "VISA, 3", "MASTERCARD, 3", "DISCOVER, 3" })
	void securityCodeLengthDependsOnTheNetwork(CardType type, int length) {
		assertThat(type.securityCodeLength()).isEqualTo(length);
	}

}
