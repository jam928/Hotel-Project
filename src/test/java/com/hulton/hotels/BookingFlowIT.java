package com.hulton.hotels;

import static com.hulton.hotels.support.Bookings.FUTURE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Map;

import com.hulton.hotels.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** A guest's whole booking, through the web layer, against MySQL. */
@IntegrationTest
@AutoConfigureMockMvc
class BookingFlowIT {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void searchWithTheFormsBlankFiltersFindsEveryHotel() throws Exception {
		// "All destinations", "Any" room type and no price limit arrive as empty parameters.
		this.mvc.perform(get("/hotels").param("destination", "").param("checkin", FUTURE.toString())
			.param("checkout", FUTURE.plusDays(3).toString()).param("guests", "1").param("roomtype", "")
			.param("maxPrice", ""))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Hulton Toronto")))
			.andExpect(content().string(containsString("Hulton New York")));
		this.mvc.perform(get("/hotels/4").param("checkin", FUTURE.toString())
			.param("checkout", FUTURE.plusDays(3).toString()).param("roomtype", ""))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("name=\"roomNo\" value=\"101\"")));
	}

	@Test
	void guestSearchesReservesSignsInAndPays() throws Exception {
		MockHttpSession session = new MockHttpSession();
		String checkin = FUTURE.toString();
		String checkout = FUTURE.plusDays(3).toString();

		// Search and hotel pages are public.
		this.mvc.perform(get("/hotels").param("destination", "Toronto").param("checkin", checkin)
			.param("checkout", checkout))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Hulton Toronto")))
			.andExpect(content().string(not(containsString("Hulton New York"))));
		this.mvc.perform(get("/hotels/4").param("checkin", checkin).param("checkout", checkout))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("name=\"roomNo\" value=\"401\"")));

		// Reserving holds the room, then payment asks the guest to sign in.
		this.mvc.perform(post("/booking").session(session).param("hotelId", "4").param("roomNo", "401")
			.param("checkin", checkin).param("checkout", checkout).param("guests", "2")
			.param("breakfast", "American").param("service", "Laundry"))
			.andExpect(redirectedUrl("/booking/payment"));
		this.mvc.perform(get("/booking/payment").session(session))
			.andExpect(redirectedUrl("/login?next=/booking/payment"));
		this.mvc.perform(post("/login").session(session).param("email", "james@example.com")
			.param("password", "password").param("next", "/booking/payment"))
			.andExpect(redirectedUrl("/booking/payment"));

		// Suite 401 in Toronto: $404/night + American breakfast $25/night, 3 nights, + laundry $18.
		this.mvc.perform(get("/booking/payment").session(session))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Pay $1,305.00")));
		this.mvc.perform(post("/booking/payment").session(session).param("cardholderName", "James Chen")
			.param("cardNumber", "4242 4242 4242 4242").param("expiry", "12/29").param("securityCode", "321")
			.param("billingAddress", "742 Pine Ave"))
			.andExpect(redirectedUrl("/reservations"))
			.andExpect(flash().attribute("notice", containsString("Booking confirmed! Room 401 at Hulton Toronto")));

		Map<String, Object> saved = this.jdbc.queryForMap("""
				select r.TotalAmt, r.NoOfDays, r.bType, r.sType, c.Email, card.Type, card.SecCode
				from reservation r join customer c on c.CID = r.CID join creditcard card on card.Cnumber = r.Cnumber
				where r.HotelID = 4 and r.Room_no = 401 and r.InDate = ?
				""", FUTURE);
		assertThat((BigDecimal) saved.get("TotalAmt")).isEqualByComparingTo("1305.00");
		assertThat(saved).containsEntry("NoOfDays", 3).containsEntry("bType", "American")
			.containsEntry("sType", "Laundry").containsEntry("Email", "james@example.com")
			.containsEntry("Type", "VISA").containsEntry("SecCode", 321);

		// The new card is saved to James's account.
		this.mvc.perform(get("/account/edit").session(session))
			.andExpect(content().string(containsString("•••• •••• •••• 4242")));

		// The room is no longer offered for those dates, and shows its next free dates instead.
		this.mvc.perform(get("/hotels/4").param("checkin", checkin).param("checkout", checkout)
			.param("roomtype", "suite"))
			.andExpect(content().string(not(containsString("name=\"roomNo\" value=\"401\""))))
			.andExpect(content().string(containsString("Booked for your dates")));
	}

	@Test
	void theSameRoomCannotBeBookedTwiceForOverlappingDates() throws Exception {
		MockHttpSession first = signedIn("maria@example.com");
		MockHttpSession second = signedIn("priya@example.com");
		reserve(first, 2, 101);
		reserve(second, 2, 101);   // both hold the same room

		pay(first, "4111111111111111", "08/29", "123").andExpect(redirectedUrl("/reservations"));
		pay(second, "378282246310005", "01/30", "7890")
			.andExpect(flash().attribute("warning", containsString("was just booked by someone else")));

		assertThat(this.jdbc.queryForObject(
				"select count(*) from reservation where HotelID = 2 and Room_no = 101 and InDate = ?", Integer.class,
				FUTURE)).isEqualTo(1);
	}

	@Test
	void aSavedCardMustBeUsedWithItsOwnDetails() throws Exception {
		MockHttpSession session = signedIn("daniel@example.com");
		reserve(session, 1, 101);

		// Daniel's sample Discover card expires 11/28 with code 321.
		pay(session, "6011111111111117", "11/28", "999").andExpect(status().isOk())
			.andExpect(content().string(containsString("doesn&#39;t match this card")));
		pay(session, "6011111111111117", "11/28", "321").andExpect(redirectedUrl("/reservations"));
	}

	private MockHttpSession signedIn(String email) throws Exception {
		MockHttpSession session = new MockHttpSession();
		this.mvc.perform(post("/login").session(session).param("email", email).param("password", "password"))
			.andExpect(redirectedUrl("/welcome"));
		return session;
	}

	private void reserve(MockHttpSession session, int hotelId, int roomNo) throws Exception {
		this.mvc.perform(post("/booking").session(session).param("hotelId", String.valueOf(hotelId))
			.param("roomNo", String.valueOf(roomNo)).param("checkin", FUTURE.toString())
			.param("checkout", FUTURE.plusDays(2).toString()).param("guests", "1"))
			.andExpect(redirectedUrl("/booking/payment"));
	}

	private ResultActions pay(MockHttpSession session, String number,
			String expiry, String securityCode) throws Exception {
		return this.mvc.perform(post("/booking/payment").session(session).param("cardholderName", "Guest")
			.param("cardNumber", number).param("expiry", expiry).param("securityCode", securityCode)
			.param("billingAddress", "1 Main St"));
	}

}
