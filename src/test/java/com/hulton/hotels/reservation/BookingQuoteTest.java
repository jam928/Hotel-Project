package com.hulton.hotels.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class BookingQuoteTest {

	private static final LocalDate CHECKIN = LocalDate.of(2026, 10, 3);

	@Test
	void chargesRoomAndBreakfastPerNightAndServiceOnce() {
		BookingQuote quote = quote(20, "American", "24.00", "Spa", "110.00");
		assertThat(quote.nights()).isEqualTo(3);
		assertThat(quote.roomTotal()).isEqualByComparingTo("340.56");
		assertThat(quote.savings()).isEqualByComparingTo("85.14");
		assertThat(quote.breakfastTotal()).isEqualByComparingTo("72.00");
		assertThat(quote.serviceTotal()).isEqualByComparingTo("110.00");
		assertThat(quote.total()).isEqualByComparingTo("522.56");
	}

	@Test
	void extrasCostNothingWhenNotChosen() {
		BookingQuote quote = quote(0, null, null, null, null);
		assertThat(quote.breakfastTotal()).isZero();
		assertThat(quote.serviceTotal()).isZero();
		assertThat(quote.total()).isEqualByComparingTo(quote.roomTotal());
	}

	@Test
	void convertsBackToTheRequestItWasMadeFor() {
		BookingRequest request = quote(20, "American", "24.00", null, null).toRequest();
		assertThat(request).isEqualTo(new BookingRequest(3, 101, CHECKIN, CHECKIN.plusDays(3), 2, "American", null));
	}

	private static BookingQuote quote(int discount, String breakfast, String breakfastPrice, String service,
			String serviceCost) {
		BigDecimal price = new BigDecimal("141.90");
		BigDecimal rate = (discount == 20) ? new BigDecimal("113.52") : price;
		return new BookingQuote(3, "Hulton Miami Beach", "Miami Beach, FL", 101, "Queen bed", "standard",
				"rooms/standard.jpg", CHECKIN, CHECKIN.plusDays(3), 2, price, discount, rate, breakfast,
				(breakfastPrice != null) ? new BigDecimal(breakfastPrice) : null, service,
				(serviceCost != null) ? new BigDecimal(serviceCost) : null);
	}

}
