package com.hulton.hotels;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.hulton.hotels.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@AutoConfigureMockMvc
class AccountIT {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void signUpThenSignInAndSeeTheDashboard() throws Exception {
		this.mvc.perform(post("/signup").param("name", "Ada Lovelace").param("address", "12 St James's Sq")
			.param("phone", "020-555-0100").param("email", "ada@example.com").param("password", "engine"))
			.andExpect(redirectedUrl("/login"));

		MockHttpSession session = new MockHttpSession();
		this.mvc.perform(post("/login").session(session).param("email", "ada@example.com")
			.param("password", "engine"))
			.andExpect(redirectedUrl("/welcome"));
		this.mvc.perform(get("/welcome").session(session))
			.andExpect(content().string(containsString("Welcome back, Ada Lovelace!")));
	}

	@Test
	void signingUpWithATakenEmailIsRefused() throws Exception {
		this.mvc.perform(post("/signup").param("name", "Another Maria").param("address", "1 Main St")
			.param("phone", "555-555-5555").param("email", "maria@example.com").param("password", "secret"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("is already used by another account")));
	}

	@Test
	void wrongPasswordIsRefused() throws Exception {
		this.mvc.perform(post("/login").param("email", "maria@example.com").param("password", "nope"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Incorrect email or password.")));
	}

	@Test
	void signInIgnoresRedirectsToOtherSites() throws Exception {
		this.mvc.perform(post("/login").param("email", "maria@example.com").param("password", "password")
			.param("next", "//evil.example/phish"))
			.andExpect(redirectedUrl("/welcome"));
	}

	@Test
	void sampleGuestsSeeTheirReservationsAndReviewableStays() throws Exception {
		MockHttpSession session = new MockHttpSession();
		this.mvc.perform(post("/login").session(session).param("email", "priya@example.com")
			.param("password", "password"));

		this.mvc.perform(get("/reservations").session(session))
			.andExpect(content().string(containsString("Separate living room")));
		this.mvc.perform(get("/review/history").session(session))
			.andExpect(content().string(containsString("/review/room?item=402&amp;hotelid=4")));
	}

	@Test
	void accountPageShowsTheGuestsCardsAndPaymentsOnly() throws Exception {
		MockHttpSession session = new MockHttpSession();
		this.mvc.perform(post("/login").session(session).param("email", "maria@example.com")
			.param("password", "password"));

		this.mvc.perform(get("/account/edit").session(session))
			.andExpect(content().string(containsString("•••• •••• •••• 1111")))
			.andExpect(content().string(containsString("Hulton New York · Room 401")))
			.andExpect(content().string(containsString("Hulton Toronto · Room 301")))
			.andExpect(content().string(containsString("Visa •••• 1111")))
			.andExpect(content().string(not(containsString("4444"))));
	}

	@Test
	void accountPageKeepsPaymentDetailsWhenTheFormHasErrors() throws Exception {
		MockHttpSession session = new MockHttpSession();
		this.mvc.perform(post("/login").session(session).param("email", "maria@example.com")
			.param("password", "password"));

		this.mvc.perform(post("/account/edit").session(session).param("name", "").param("address", "18 Elm St")
			.param("phone", "617-555-0111").param("email", "maria@example.com").param("password", "password"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Payment history")))
			.andExpect(content().string(containsString("Visa •••• 1111")));
	}

	@Test
	void savedCardsStayAfterTheirReservationsAreCancelled() throws Exception {
		MockHttpSession session = new MockHttpSession();
		this.mvc.perform(post("/login").session(session).param("email", "maria@example.com")
			.param("password", "password"));
		List<Integer> invoices = this.jdbc.queryForList("""
				select r.InvoiceNo from reservation r join customer c on c.CID = r.CID
				where c.Email = 'maria@example.com'
				""", Integer.class);
		for (int invoice : invoices) {
			this.mvc.perform(post("/reservations/{id}/cancel", invoice).session(session));
		}

		this.mvc.perform(get("/account/edit").session(session))
			.andExpect(content().string(containsString("No payments yet.")))
			.andExpect(content().string(containsString("•••• •••• •••• 1111")));
	}

	@Test
	void guestsCanRemoveTheirOwnSavedCardsOnly() throws Exception {
		MockHttpSession session = new MockHttpSession();
		this.mvc.perform(post("/login").session(session).param("email", "maria@example.com")
			.param("password", "password"));
		Integer mariasCard = savedCardId("maria@example.com");
		Integer jamessCard = savedCardId("james@example.com");

		this.mvc.perform(post("/account/cards/{id}/remove", jamessCard).session(session))
			.andExpect(redirectedUrl("/account/edit"))
			.andExpect(flash().attribute("warning", "That card could not be removed."));
		this.mvc.perform(post("/account/cards/{id}/remove", mariasCard).session(session))
			.andExpect(redirectedUrl("/account/edit"))
			.andExpect(flash().attribute("notice", "The card was removed from your account."));

		this.mvc.perform(get("/account/edit").session(session))
			.andExpect(content().string(containsString("No saved cards yet.")))
			.andExpect(content().string(containsString("Visa •••• 1111")));   // still in the payment history
		assertThat(savedCardId("james@example.com")).isEqualTo(jamessCard);
	}

	private Integer savedCardId(String email) {
		return this.jdbc.queryForObject("""
				select cc.ID from customer_card cc join customer c on c.CID = cc.CID where c.Email = ?
				""", Integer.class, email);
	}

}
