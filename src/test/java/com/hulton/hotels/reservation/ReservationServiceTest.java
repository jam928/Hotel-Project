package com.hulton.hotels.reservation;

import static com.hulton.hotels.support.Entities.breakfast;
import static com.hulton.hotels.support.Entities.hotel;
import static com.hulton.hotels.support.Entities.room;
import static com.hulton.hotels.support.Entities.service;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import com.hulton.hotels.hotel.AvailableRoom;
import com.hulton.hotels.hotel.HotelCard;
import com.hulton.hotels.hotel.HotelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

	private static final LocalDate CHECKIN = LocalDate.of(2026, 10, 3);

	private static final int CUSTOMER = 7;

	@Mock
	private ReservationRepository reservations;

	@Mock
	private CreditCardRepository creditCards;

	@Mock
	private CustomerCardRepository customerCards;

	@Mock
	private HotelService hotels;

	private ReservationService service;

	@BeforeEach
	void createService() {
		this.service = new ReservationService(this.reservations, this.creditCards, this.customerCards, this.hotels,
				new ReservationMapperImpl());
	}

	@Test
	void quotePricesTheDiscountedRoomAndExtras() {
		roomIsFree(20);
		given(this.hotels.breakfast(3, "American")).willReturn(Optional.of(breakfast(3, "American", "24.00")));
		given(this.hotels.service(3, "Spa")).willReturn(Optional.of(service(3, "Spa", "110.00")));

		BookingQuote quote = this.service.quote(request("American", "Spa")).orElseThrow();

		assertThat(quote.hotelName()).isEqualTo("Hulton Miami Beach");
		assertThat(quote.nightlyRate()).isEqualByComparingTo("113.52");
		assertThat(quote.discount()).isEqualTo(20);
		assertThat(quote.total()).isEqualByComparingTo("522.56");
	}

	@Test
	void quoteTreatsBlankExtrasAsNone() {
		roomIsFree(0);

		BookingQuote quote = this.service.quote(request("", "")).orElseThrow();

		assertThat(quote.breakfastType()).isNull();
		assertThat(quote.serviceType()).isNull();
		assertThat(quote.total()).isEqualByComparingTo("425.70");
	}

	@Test
	void quoteIsEmptyWhenTheRoomIsTaken() {
		given(this.hotels.availableRoom(eq(3), eq(101), any())).willReturn(Optional.empty());
		assertThat(this.service.quote(request(null, null))).isEmpty();
	}

	@Test
	void quoteIsEmptyWhenTheHotelDoesNotOfferTheBreakfast() {
		given(this.hotels.availableRoom(eq(3), eq(101), any()))
			.willReturn(Optional.of(new AvailableRoom(room(3, 101, "standard", "141.90", 2), null)));
		given(this.hotels.breakfast(3, "Vegan")).willReturn(Optional.empty());
		assertThat(this.service.quote(request("Vegan", null))).isEmpty();
	}

	@Test
	void quoteIsEmptyWhenCheckoutIsNotAfterCheckin() {
		BookingRequest request = request(null, null);
		request.setCheckout(CHECKIN);
		assertThat(this.service.quote(request)).isEmpty();
		then(this.hotels).shouldHaveNoInteractions();
	}

	@Test
	void bookSavesANewCardAndTheReservation() {
		roomIsFree(0);
		given(this.creditCards.findById("4242424242424242")).willReturn(Optional.empty());
		given(this.creditCards.save(any())).willAnswer((invocation) -> invocation.getArgument(0));
		given(this.reservations.save(any())).willAnswer((invocation) -> invocation.getArgument(0));

		Reservation reservation = this.service.book(CUSTOMER, quote(), payment("4242 4242 4242 4242", "12/29", "123"));

		ArgumentCaptor<CreditCard> card = ArgumentCaptor.forClass(CreditCard.class);
		then(this.creditCards).should().save(card.capture());
		assertThat(card.getValue().getType()).isEqualTo("VISA");
		assertThat(card.getValue().getExpires()).isEqualTo(LocalDate.of(2029, 12, 31));
		assertThat(card.getValue().getSecurityCode()).isEqualTo(123);
		then(this.customerCards).should().save(argThat((saved) -> saved.getCustomerId() == CUSTOMER
				&& saved.getCardNumber().equals("4242424242424242")));
		assertThat(reservation.getCustomerId()).isEqualTo(CUSTOMER);
		assertThat(reservation.getCardNumber()).isEqualTo("4242424242424242");
		assertThat(reservation.getInDate()).isEqualTo(CHECKIN);
		assertThat(reservation.getNumberOfDays()).isEqualTo(3);
		assertThat(reservation.getTotalAmount()).isEqualByComparingTo("425.70");
	}

	@Test
	void bookReusesACardOnFileWhenTheDetailsMatch() {
		roomIsFree(0);
		given(this.creditCards.findById("4242424242424242")).willReturn(Optional.of(card("2029-12-31", 123)));
		given(this.customerCards.existsByCustomerIdAndCardNumber(CUSTOMER, "4242424242424242")).willReturn(true);
		given(this.reservations.save(any())).willAnswer((invocation) -> invocation.getArgument(0));

		this.service.book(CUSTOMER, quote(), payment("4242424242424242", "12/29", "123"));

		then(this.creditCards).should(never()).save(any());
		then(this.customerCards).should(never()).save(any());
		then(this.reservations).should().save(argThat((r) -> r.getCardNumber().equals("4242424242424242")));
	}

	@Test
	void bookSavesACardOnFileToTheAccountTheFirstTimeTheCustomerUsesIt() {
		roomIsFree(0);
		given(this.creditCards.findById("4242424242424242")).willReturn(Optional.of(card("2029-12-31", 123)));
		given(this.reservations.save(any())).willAnswer((invocation) -> invocation.getArgument(0));

		this.service.book(CUSTOMER, quote(), payment("4242424242424242", "12/29", "123"));

		then(this.creditCards).should(never()).save(any());
		then(this.customerCards).should().save(argThat((saved) -> saved.getCustomerId() == CUSTOMER));
	}

	@Test
	void bookRefusesACardOnFileWithADifferentSecurityCode() {
		roomIsFree(0);
		given(this.creditCards.findById("4242424242424242")).willReturn(Optional.of(card("2029-12-31", 999)));

		assertThatExceptionOfType(CardDetailsMismatchException.class)
			.isThrownBy(() -> this.service.book(CUSTOMER, quote(), payment("4242424242424242", "12/29", "123")));
		then(this.customerCards).shouldHaveNoInteractions();
		then(this.reservations).should(never()).save(any());
	}

	@Test
	void bookFailsWhenTheRoomWasTakenSinceTheQuote() {
		given(this.hotels.availableRoom(anyInt(), anyInt(), any())).willReturn(Optional.empty());

		assertThatExceptionOfType(RoomUnavailableException.class)
			.isThrownBy(() -> this.service.book(CUSTOMER, quote(), payment("4242424242424242", "12/29", "123")));
		then(this.creditCards).shouldHaveNoInteractions();
		then(this.customerCards).shouldHaveNoInteractions();
		then(this.reservations).shouldHaveNoInteractions();
	}

	private void roomIsFree(int discount) {
		given(this.hotels.availableRoom(eq(3), eq(101), any())).willReturn(
				Optional.of(new AvailableRoom(room(3, 101, "standard", "141.90", 2), (discount > 0) ? discount : null)));
		given(this.hotels.card(3))
			.willReturn(Optional.of(new HotelCard(hotel(3, "Miami Beach"), BigDecimal.ONE, null, 0L, 0L)));
	}

	private static BookingRequest request(String breakfast, String service) {
		return new BookingRequest(3, 101, CHECKIN, CHECKIN.plusDays(3), 2, breakfast, service);
	}

	private static BookingQuote quote() {
		BigDecimal price = new BigDecimal("141.90");
		return new BookingQuote(3, "Hulton Miami Beach", "Miami Beach, FL", 101, "Queen bed", "standard", null,
				CHECKIN, CHECKIN.plusDays(3), 2, price, 0, price, null, null, null, null);
	}

	private static PaymentForm payment(String number, String expiry, String securityCode) {
		PaymentForm form = new PaymentForm();
		form.setCardholderName("Jane Doe");
		form.setCardNumber(number);
		form.setExpiry(expiry);
		form.setSecurityCode(securityCode);
		form.setBillingAddress("1 Main St");
		return form;
	}

	private static CreditCard card(String expires, int securityCode) {
		return CreditCard.builder()
			.number("4242424242424242")
			.expires(LocalDate.parse(expires))
			.securityCode(securityCode)
			.type("VISA")
			.name("Jane Doe")
			.billingAddress("1 Main St")
			.build();
	}

}
