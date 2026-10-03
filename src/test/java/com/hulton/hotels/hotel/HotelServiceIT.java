package com.hulton.hotels.hotel;

import static com.hulton.hotels.support.Bookings.FUTURE;
import static com.hulton.hotels.support.Bookings.book;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import com.hulton.hotels.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@IntegrationTest
class HotelServiceIT {

	@Autowired
	private HotelService hotels;

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void cardsSummarizeTheSampleData() {
		HotelCard newYork = this.hotels.card(1).orElseThrow();
		assertThat(newYork.hotel().getName()).isEqualTo("Hulton New York");
		assertThat(newYork.fromPrice()).isEqualByComparingTo("180.60");
		assertThat(newYork.reviewCount()).isEqualTo(8);
		assertThat(newYork.bookingCount()).isEqualTo(3);
		assertThat(newYork.rating()).isEqualTo(2.0);
	}

	@Test
	void popularHotelsAreRankedByBookingsThenRating() {
		// Every sample hotel has 3 bookings, so the rating decides.
		assertThat(this.hotels.popular(4)).extracting((card) -> card.hotel().getCity())
			.containsExactly("San Francisco", "Miami Beach", "Toronto", "New York");

		// One more booking puts Toronto first.
		book(this.jdbc, 4, 101, FUTURE, FUTURE.plusDays(1));
		assertThat(this.hotels.popular(1)).extracting((card) -> card.hotel().getCity()).containsExactly("Toronto");
	}

	@Test
	void destinationsAreCitiesInAlphabeticalOrder() {
		assertThat(this.hotels.destinations()).containsExactly(
				Map.entry("Miami Beach", "Miami Beach, FL"), Map.entry("New York", "New York, NY"),
				Map.entry("San Francisco", "San Francisco, CA"), Map.entry("Toronto", "Toronto, ON"));
	}

	@Test
	void aHotelWithAllMatchingRoomsBookedIsSoldOutWithItsNextDates() {
		book(this.jdbc, 3, 401, FUTURE.minusDays(1), FUTURE.plusDays(3));
		book(this.jdbc, 3, 402, FUTURE.minusDays(2), FUTURE.plusDays(4));
		HotelSearch search = search("Miami Beach", "suite");

		List<HotelResult> results = this.hotels.search(search);
		List<SoldOutHotel> soldOut = this.hotels.soldOut(search, results);

		assertThat(results).isEmpty();
		assertThat(soldOut).singleElement().satisfies((hotel) -> {
			assertThat(hotel.card().hotel().getId()).isEqualTo(3);
			assertThat(hotel.checkin()).isEqualTo(FUTURE.plusDays(3));
			assertThat(hotel.checkout()).isEqualTo(FUTURE.plusDays(5));
		});
		assertThat(this.hotels.bookedRooms(3, search)).extracting(NextAvailable::checkin)
			.containsExactly(FUTURE.plusDays(3), FUTURE.plusDays(4));
	}

	private static HotelSearch search(String destination, String roomtype) {
		HotelSearch search = new HotelSearch();
		search.setDestination(destination);
		search.setRoomtype(roomtype);
		search.setCheckin(FUTURE);
		search.setCheckout(FUTURE.plusDays(2));
		return search;
	}

}
