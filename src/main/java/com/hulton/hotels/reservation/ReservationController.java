package com.hulton.hotels.reservation;

import static com.hulton.hotels.web.LoginRequiredInterceptor.CID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * The booking page and the customer's list of reservations.
 */
@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

	private final ReservationService reservations;

	@GetMapping
	public String list(@SessionAttribute(CID) int cid, Model model) {
		model.addAttribute("reservations", this.reservations.reservations(cid));
		return "reservation/list";
	}

	@GetMapping("/new")
	public String book() {
		return "reservation/book";
	}

	@PostMapping("/{invoiceNo}/cancel")
	public String cancel(@SessionAttribute(CID) int cid, @PathVariable int invoiceNo, RedirectAttributes redirect) {
		boolean cancelled = this.reservations.cancel(cid, invoiceNo);
		redirect.addFlashAttribute("notice",
				cancelled ? "Reservation #" + invoiceNo + " was cancelled." : "That reservation could not be cancelled.");
		return "redirect:/reservations";
	}

}
