package com.hulton.hotels.support;

import java.time.LocalDate;

import org.springframework.jdbc.core.JdbcTemplate;

/** Inserts reservations directly, for integration tests that need rooms to be taken. */
public final class Bookings {

	/** Far enough ahead that no sample reservation or discount (all within ~60 days) overlaps. */
	public static final LocalDate FUTURE = LocalDate.now().plusDays(300);

	private Bookings() {
	}

	/** Books the room for the sample guest Maria Garcia, with her sample card. */
	public static void book(JdbcTemplate jdbc, int hotelId, int roomNo, LocalDate in, LocalDate out) {
		jdbc.update("""
				insert into reservation (ResDate, TotalAmt, CID, Room_no, HotelID, InDate, OutDate, NoOfDays, Cnumber)
				select curdate(), 100, CID, ?, ?, ?, ?, datediff(?, ?), '4111111111111111'
				from customer where Email = 'maria@example.com'
				""", roomNo, hotelId, in, out, out, in);
	}

}
