package com.hulton.hotels.reservation;

import static com.hulton.hotels.web.LoginRequiredInterceptor.CID;
import static com.hulton.hotels.web.LoginRequiredInterceptor.NAME;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Checkout: the room picked on a hotel's page is priced and held in the session,
 * then paid for on the payment page. Picking a room needs no login; paying does.
 */
@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

	/** Session attribute holding the {@link BookingQuote} waiting for payment. */
	static final String BOOKING = "booking";

	private static final DateTimeFormatter STAY_DATE = DateTimeFormatter.ofPattern("MMM d");

	private final ReservationService reservations;

	@PostMapping
	public String reserve(@Valid BookingRequest request, BindingResult result, HttpSession session,
			RedirectAttributes redirect) {
		Optional<BookingQuote> quote = result.hasErrors() ? Optional.empty() : this.reservations.quote(request);
		if (quote.isEmpty()) {
			redirect.addFlashAttribute("warning", result.hasFieldErrors("roomNo") ? "Choose a room to reserve."
					: "That room is no longer available for those dates. Please pick another.");
			return redirectToHotel(request, redirect);
		}
		session.setAttribute(BOOKING, quote.get());
		return "redirect:/booking/payment";
	}

	@GetMapping("/payment")
	public String payment(@SessionAttribute(name = BOOKING, required = false) BookingQuote quote,
			@SessionAttribute(NAME) String customerName, Model model) {
		if (quote == null) {
			return "redirect:/hotels";
		}
		PaymentForm form = new PaymentForm();
		form.setCardholderName(customerName);
		model.addAttribute("quote", quote);
		model.addAttribute("form", form);
		return "booking/payment";
	}

	@PostMapping("/payment")
	public String pay(@SessionAttribute(CID) int cid,
			@SessionAttribute(name = BOOKING, required = false) BookingQuote quote,
			@Valid @ModelAttribute("form") PaymentForm form, BindingResult result, HttpSession session, Model model,
			RedirectAttributes redirect) {
		if (quote == null) {
			return "redirect:/hotels";
		}
		model.addAttribute("quote", quote);
		form.validate(result, YearMonth.now());
		if (result.hasErrors()) {
			return "booking/payment";
		}
		Reservation reservation;
		try {
			reservation = this.reservations.book(cid, quote, form);
		}
		catch (CardDetailsMismatchException ex) {
			result.reject("card.mismatch",
					"The expiry date or security code doesn't match this card. Check them or use another card.");
			return "booking/payment";
		}
		catch (RoomUnavailableException ex) {
			session.removeAttribute(BOOKING);
			redirect.addFlashAttribute("warning",
					"Sorry, room " + quote.roomNo() + " was just booked by someone else. Please pick another.");
			return redirectToHotel(quote.toRequest(), redirect);
		}
		session.removeAttribute(BOOKING);
		redirect.addFlashAttribute("notice",
				"Booking confirmed! Room " + quote.roomNo() + " at " + quote.hotelName() + ", "
						+ STAY_DATE.format(quote.checkin()) + " – " + STAY_DATE.format(quote.checkout())
						+ ". Invoice #" + reservation.getInvoiceNo() + ".");
		return "redirect:/reservations";
	}

	private static String redirectToHotel(BookingRequest request, RedirectAttributes redirect) {
		if (request.getHotelId() == null) {
			return "redirect:/hotels";
		}
		redirect.addAttribute("hotelId", request.getHotelId());
		// ISO dates, as the hotel page reads them (the default formatting is locale-specific).
		redirect.addAttribute("checkin", Objects.toString(request.getCheckin(), ""));
		redirect.addAttribute("checkout", Objects.toString(request.getCheckout(), ""));
		redirect.addAttribute("guests", request.getGuests());
		return "redirect:/hotels/{hotelId}";
	}

}
