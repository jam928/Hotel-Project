package com.hulton.hotels.reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.hulton.hotels.hotel.AvailableRoom;
import com.hulton.hotels.hotel.Breakfast;
import com.hulton.hotels.hotel.Hotel;
import com.hulton.hotels.hotel.HotelSearch;
import com.hulton.hotels.hotel.HotelService;
import com.hulton.hotels.hotel.ServiceOffering;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pricing and booking stays, and listing and cancelling a customer's reservations.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

	private final ReservationRepository reservations;

	private final CreditCardRepository creditCards;

	private final CustomerCardRepository customerCards;

	private final HotelService hotels;

	private final ReservationMapper mapper;

	/**
	 * Prices the stay, or returns empty if the room is not free for the whole stay or
	 * the breakfast or service is not offered by the hotel.
	 */
	public Optional<BookingQuote> quote(BookingRequest request) {
		if (!request.getCheckout().isAfter(request.getCheckin())) {
			return Optional.empty();
		}
		HotelSearch stay = new HotelSearch();
		stay.setCheckin(request.getCheckin());
		stay.setCheckout(request.getCheckout());
		stay.setGuests(request.getGuests());
		Optional<AvailableRoom> room = this.hotels.availableRoom(request.getHotelId(), request.getRoomNo(), stay);
		Optional<Breakfast> breakfast = optional(request.getBreakfast())
			.map((type) -> this.hotels.breakfast(request.getHotelId(), type).orElse(null));
		Optional<ServiceOffering> service = optional(request.getService())
			.map((type) -> this.hotels.service(request.getHotelId(), type).orElse(null));
		boolean extrasOffered = (optional(request.getBreakfast()).isEmpty() || breakfast.isPresent())
				&& (optional(request.getService()).isEmpty() || service.isPresent());
		if (room.isEmpty() || !extrasOffered) {
			return Optional.empty();
		}
		Hotel hotel = this.hotels.card(request.getHotelId()).orElseThrow().hotel();
		AvailableRoom available = room.get();
		return Optional.of(new BookingQuote(hotel.getId(), hotel.getName(), hotel.getLocation(),
				available.room().getRoomNo(), available.room().getDescription(), available.room().getRoomtype(),
				available.room().getImageKey(),
				request.getCheckin(), request.getCheckout(), request.getGuests(), available.room().getPrice(),
				available.discountPercent(), available.nightlyRate(), breakfast.map(Breakfast::getType).orElse(null),
				breakfast.map(Breakfast::getPrice).orElse(null), service.map(ServiceOffering::getType).orElse(null),
				service.map(ServiceOffering::getCost).orElse(null)));
	}

	/**
	 * Books the stay, pricing it again in case the room was taken or prices changed
	 * since the quote. The card is saved, and saved to the customer's account, the
	 * first time they use it.
	 * @throws RoomUnavailableException if the stay can no longer be booked
	 * @throws CardDetailsMismatchException if the card is on file with other details
	 */
	@Transactional
	public Reservation book(int customerId, BookingQuote quote, PaymentForm payment) {
		BookingQuote current = quote(quote.toRequest()).orElseThrow(RoomUnavailableException::new);
		CreditCard card = this.creditCards.findById(payment.digits())
			.orElseGet(() -> this.creditCards.save(CreditCard.builder()
				.number(payment.digits())
				.expires(payment.expiryMonth().atEndOfMonth())
				.securityCode(Integer.valueOf(payment.getSecurityCode()))
				.type(payment.cardType().orElseThrow().code())
				.name(payment.getCardholderName())
				.billingAddress(payment.getBillingAddress())
				.build()));
		if (!card.getExpires().equals(payment.expiryMonth().atEndOfMonth())
				|| !card.getSecurityCode().equals(Integer.valueOf(payment.getSecurityCode()))) {
			throw new CardDetailsMismatchException();
		}
		if (!this.customerCards.existsByCustomerIdAndCardNumber(customerId, card.getNumber())) {
			this.customerCards.save(new CustomerCard(customerId, card.getNumber(), LocalDate.now()));
		}
		return this.reservations.save(this.mapper.toReservation(current, customerId, card.getNumber()));
	}

	public List<ReservationSummary> reservations(int customerId) {
		return this.reservations.findSummariesByCustomerId(customerId);
	}

	/** What the customer has paid, latest first. Cancelled reservations are deleted, so they are not listed. */
	public List<Payment> payments(int customerId) {
		return this.reservations.findPaymentsByCustomerId(customerId);
	}

	/** The cards saved to the customer's account, most recently saved first. */
	public List<SavedCard> savedCards(int customerId) {
		return this.customerCards.findSavedByCustomerId(customerId);
	}

	/**
	 * Removes a card from the customer's account. The card stays on file for the
	 * reservations paid with it, and is saved again if they pay with it later.
	 * Returns {@code false} if the customer has no saved card with that ID.
	 */
	@Transactional
	public boolean removeSavedCard(int customerId, int savedCardId) {
		return this.customerCards.deleteByIdAndCustomerId(savedCardId, customerId) > 0;
	}

	/**
	 * Cancels one of the customer's reservations. Returns {@code false} if the customer
	 * has no reservation with that invoice number.
	 */
	@Transactional
	public boolean cancel(int customerId, int invoiceNo) {
		return this.reservations.deleteByInvoiceNoAndCustomerId(invoiceNo, customerId) > 0;
	}

	private static Optional<String> optional(String value) {
		return Optional.ofNullable(value).filter((text) -> !text.isBlank());
	}

}
