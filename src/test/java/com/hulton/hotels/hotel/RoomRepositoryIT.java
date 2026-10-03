package com.hulton.hotels.hotel;

import static com.hulton.hotels.support.Bookings.FUTURE;
import static com.hulton.hotels.support.Bookings.book;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import com.hulton.hotels.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@IntegrationTest
class RoomRepositoryIT {

	private static final LocalDate IN = FUTURE;

	private static final LocalDate OUT = FUTURE.plusDays(3);

	@Autowired
	private RoomRepository rooms;

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void findsEveryRoomOfTheTypeWhenNothingIsBooked() {
		List<AvailableRoom> suites = this.rooms.findAvailable(1, null, "suite", 1, IN, OUT);
		assertThat(suites).extracting((available) -> available.room().getRoomNo()).containsExactly(401, 402);
		assertThat(suites).allSatisfy((available) -> assertThat(available.discount()).isNull());
	}

	@Test
	void excludesRoomsWithAnOverlappingReservationOfAnyShape() {
		book(this.jdbc, 1, 101, IN.minusDays(1), IN.plusDays(1));     // overlaps the start
		book(this.jdbc, 1, 102, OUT.minusDays(1), OUT.plusDays(2));   // overlaps the end
		book(this.jdbc, 1, 103, IN.minusDays(2), OUT.plusDays(2));    // covers the whole stay
		book(this.jdbc, 1, 201, IN.plusDays(1), IN.plusDays(2));      // inside the stay

		assertThat(roomNumbers(this.rooms.findAvailable(1, null, "standard", 1, IN, OUT)))
			.doesNotContain(101, 102, 103, 201)
			.contains(202, 203);
	}

	@Test
	void allowsBackToBackStays() {
		book(this.jdbc, 1, 101, IN.minusDays(3), IN);   // checks out the day we check in
		book(this.jdbc, 1, 102, OUT, OUT.plusDays(2));   // checks in the day we check out

		assertThat(roomNumbers(this.rooms.findAvailable(1, null, "standard", 1, IN, OUT))).contains(101, 102);
	}

	@Test
	void filtersByCityAndGuests() {
		List<AvailableRoom> forFive = this.rooms.findAvailable(null, "Toronto", null, 5, IN, OUT);
		assertThat(forFive).extracting((available) -> available.room().getRoomtype()).containsOnly("suite");
		assertThat(forFive).extracting((available) -> available.room().getHotelId()).containsOnly(4);
	}

	@Test
	void appliesTheBestDiscountOnOfferDuringTheStay() {
		this.jdbc.update("insert into `offer-room` (Room_no, SDate, EDate, Discount, HotelID) values (?, ?, ?, ?, ?)",
				401, IN.plusDays(1), IN.plusDays(10), 15, 2);
		this.jdbc.update("insert into `offer-room` (Room_no, SDate, EDate, Discount, HotelID) values (?, ?, ?, ?, ?)",
				401, IN.minusDays(10), IN, 25, 2);

		AvailableRoom suite = this.rooms.findAvailable(2, null, "suite", 1, IN, OUT).get(0);

		assertThat(suite.discount()).isEqualTo(25);
		assertThat(suite.nightlyRate()).isEqualByComparingTo("375.94");
	}

	@Test
	void findsStaysStillRunningInCheckinOrder() {
		book(this.jdbc, 3, 402, IN.plusDays(5), IN.plusDays(7));
		book(this.jdbc, 3, 401, IN.plusDays(1), IN.plusDays(2));
		book(this.jdbc, 3, 401, IN.minusDays(5), IN);   // ended before IN

		assertThat(this.rooms.findStaysEndingAfter(3, IN)).containsExactly(
				new BookedStay(3, 401, IN.plusDays(1), IN.plusDays(2)),
				new BookedStay(3, 402, IN.plusDays(5), IN.plusDays(7)));
	}

	private static List<Integer> roomNumbers(List<AvailableRoom> available) {
		return available.stream().map((room) -> room.room().getRoomNo()).toList();
	}

}
