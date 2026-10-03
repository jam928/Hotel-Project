package com.hulton.hotels.reservation;

import static com.hulton.hotels.web.LoginRequiredInterceptor.CID;
import static com.hulton.hotels.web.LoginRequiredInterceptor.NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

	private static final LocalDate CHECKIN = LocalDate.of(2026, 10, 3);

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private ReservationService reservations;

	@Test
	void reservingHoldsTheQuoteInTheSessionWithoutNeedingALogin() throws Exception {
		BookingQuote quote = quote();
		given(this.reservations.quote(any())).willReturn(Optional.of(quote));
		MockHttpSession session = new MockHttpSession();

		this.mvc.perform(post("/booking").session(session).params(reserveForm()))
			.andExpect(redirectedUrl("/booking/payment"));

		assertThat(session.getAttribute(BookingController.BOOKING)).isEqualTo(quote);
		then(this.reservations).should()
			.quote(new BookingRequest(3, 101, CHECKIN, CHECKIN.plusDays(3), 2, "American", ""));
	}

	@Test
	void reservingATakenRoomSendsTheGuestBackToTheHotelWithAWarning() throws Exception {
		given(this.reservations.quote(any())).willReturn(Optional.empty());

		this.mvc.perform(post("/booking").params(reserveForm()))
			.andExpect(redirectedUrl("/hotels/3?checkin=2026-10-03&checkout=2026-10-06&guests=2"))
			.andExpect(flash().attribute("warning", containsString("no longer available")));
	}

	@Test
	void reservingWithoutChoosingARoomAsksForOne() throws Exception {
		MultiValueMap<String, String> form = reserveForm();
		form.remove("roomNo");

		this.mvc.perform(post("/booking").params(form))
			.andExpect(flash().attribute("warning", "Choose a room to reserve."));
		then(this.reservations).shouldHaveNoInteractions();
	}

	@Test
	void paymentPageNeedsALoginAndComesBackAfterwards() throws Exception {
		this.mvc.perform(get("/booking/payment")).andExpect(redirectedUrl("/login?next=/booking/payment"));
	}

	@Test
	void paymentPageWithoutAHeldRoomGoesToSearch() throws Exception {
		this.mvc.perform(get("/booking/payment").session(loggedIn())).andExpect(redirectedUrl("/hotels"));
	}

	@Test
	void paymentPageShowsTheQuoteAndPrefillsTheCardholder() throws Exception {
		MockHttpSession session = loggedIn();
		session.setAttribute(BookingController.BOOKING, quote());

		this.mvc.perform(get("/booking/payment").session(session))
			.andExpect(status().isOk())
			.andExpect(view().name("booking/payment"))
			.andExpect(content().string(containsString("Pay $425.70")))
			.andExpect(content().string(containsString("value=\"Maria Garcia\"")));
	}

	@Test
	void invalidCardDetailsAreShownWithoutBooking() throws Exception {
		MockHttpSession session = loggedIn();
		session.setAttribute(BookingController.BOOKING, quote());

		this.mvc.perform(post("/booking/payment").session(session)
			.params(paymentForm("4242424242424241", "12/29", "123")))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrorCode("form", "cardNumber", "card.invalid"));
		then(this.reservations).should(never()).book(eq(7), any(), any());
	}

	@Test
	void payingBooksTheRoomAndReleasesTheHold() throws Exception {
		MockHttpSession session = loggedIn();
		BookingQuote quote = quote();
		session.setAttribute(BookingController.BOOKING, quote);
		Reservation reservation = BeanUtils.instantiateClass(Reservation.class);
		reservation.setInvoiceNo(42);
		given(this.reservations.book(eq(7), eq(quote), any())).willReturn(reservation);

		this.mvc.perform(post("/booking/payment").session(session)
			.params(paymentForm("4242 4242 4242 4242", "12/29", "123")))
			.andExpect(redirectedUrl("/reservations"))
			.andExpect(flash().attribute("notice", containsString("Invoice #42")));
		assertThat(session.getAttribute(BookingController.BOOKING)).isNull();
	}

	@Test
	void aCardOnFileWithOtherDetailsIsRefused() throws Exception {
		MockHttpSession session = loggedIn();
		session.setAttribute(BookingController.BOOKING, quote());
		given(this.reservations.book(eq(7), any(), any())).willThrow(new CardDetailsMismatchException());

		this.mvc.perform(post("/booking/payment").session(session)
			.params(paymentForm("4242424242424242", "12/29", "123")))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("doesn&#39;t match this card")));
		assertThat(session.getAttribute(BookingController.BOOKING)).isNotNull();
	}

	@Test
	void aRoomTakenDuringPaymentSendsTheGuestBackToPickAnother() throws Exception {
		MockHttpSession session = loggedIn();
		session.setAttribute(BookingController.BOOKING, quote());
		given(this.reservations.book(eq(7), any(), any())).willThrow(new RoomUnavailableException());

		this.mvc.perform(post("/booking/payment").session(session)
			.params(paymentForm("4242424242424242", "12/29", "123")))
			.andExpect(redirectedUrl("/hotels/3?checkin=2026-10-03&checkout=2026-10-06&guests=2"))
			.andExpect(flash().attribute("warning", containsString("room 101 was just booked")));
		assertThat(session.getAttribute(BookingController.BOOKING)).isNull();
	}

	private static MockHttpSession loggedIn() {
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(CID, 7);
		session.setAttribute(NAME, "Maria Garcia");
		return session;
	}

	private static BookingQuote quote() {
		BigDecimal price = new BigDecimal("141.90");
		return new BookingQuote(3, "Hulton Miami Beach", "Miami Beach, FL", 101, "Queen bed", "standard", null,
				CHECKIN, CHECKIN.plusDays(3), 2, price, 0, price, null, null, null, null);
	}

	private static MultiValueMap<String, String> reserveForm() {
		var form = new LinkedMultiValueMap<String, String>();
		form.add("hotelId", "3");
		form.add("roomNo", "101");
		form.add("checkin", CHECKIN.toString());
		form.add("checkout", CHECKIN.plusDays(3).toString());
		form.add("guests", "2");
		form.add("breakfast", "American");
		form.add("service", "");
		return form;
	}

	private static MultiValueMap<String, String> paymentForm(String number, String expiry,
			String securityCode) {
		var form = new LinkedMultiValueMap<String, String>();
		form.add("cardholderName", "Maria Garcia");
		form.add("cardNumber", number);
		form.add("expiry", expiry);
		form.add("securityCode", securityCode);
		form.add("billingAddress", "18 Elm St");
		return form;
	}

}
