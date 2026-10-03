package com.hulton.hotels.hotel;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class HotelSearchTest {

	private static final LocalDate TODAY = LocalDate.of(2026, 9, 30);

	@Test
	void defaultsToOneNightFromTomorrow() {
		HotelSearch search = new HotelSearch();
		search.normalize(TODAY);
		assertThat(search.getCheckin()).isEqualTo(TODAY.plusDays(1));
		assertThat(search.getCheckout()).isEqualTo(TODAY.plusDays(2));
		assertThat(search.nights()).isEqualTo(1);
	}

	@Test
	void movesACheckinInThePastToTomorrow() {
		HotelSearch search = new HotelSearch();
		search.setCheckin(TODAY.minusDays(3));
		search.setCheckout(TODAY.plusDays(5));
		search.normalize(TODAY);
		assertThat(search.getCheckin()).isEqualTo(TODAY.plusDays(1));
		assertThat(search.getCheckout()).isEqualTo(TODAY.plusDays(5));
	}

	@Test
	void makesTheStayAtLeastOneNight() {
		HotelSearch search = new HotelSearch();
		search.setCheckin(TODAY.plusDays(4));
		search.setCheckout(TODAY.plusDays(2));
		search.normalize(TODAY);
		assertThat(search.getCheckout()).isEqualTo(TODAY.plusDays(5));
	}

	@Test
	void clampsGuestsAndTreatsBlankFiltersAsNone() {
		HotelSearch search = new HotelSearch();
		search.setGuests(40);
		search.setDestination(" ");
		search.setRoomtype("");
		search.normalize(TODAY);
		assertThat(search.getGuests()).isEqualTo(6);
		assertThat(search.getDestination()).isNull();
		assertThat(search.getRoomtype()).isNull();
	}

}
