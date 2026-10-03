package com.hulton.hotels.account;

import static com.hulton.hotels.web.LoginRequiredInterceptor.CID;
import static com.hulton.hotels.web.LoginRequiredInterceptor.NAME;
import static com.hulton.hotels.web.LoginRequiredInterceptor.isSafeNext;

import java.time.YearMonth;
import java.util.Optional;

import com.hulton.hotels.reservation.ReservationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Login, sign-up, the customer dashboard and account editing, with the customer's
 * saved cards and payment history.
 */
@Controller
@RequiredArgsConstructor
public class AccountController {

	private static final String NOTICE = "notice";

	private final AccountService accounts;

	private final ReservationService reservations;

	/** Where to go after logging in, carried through the login and sign-up forms. */
	@ModelAttribute
	public void next(@RequestParam(required = false) String next, Model model) {
		if (isSafeNext(next)) {
			model.addAttribute("next", next);
		}
	}

	@GetMapping("/login")
	public String loginPage(Model model) {
		model.addAttribute("form", new LoginForm());
		return "account/login";
	}

	@PostMapping("/login")
	public String login(@Valid @ModelAttribute("form") LoginForm form, BindingResult result,
			@RequestParam(required = false) String next, HttpServletRequest request) {
		if (result.hasErrors()) {
			return "account/login";
		}
		Optional<Customer> customer = this.accounts.login(form.getEmail(), form.getPassword());
		if (customer.isEmpty()) {
			result.reject("login.failed", "Incorrect email or password.");
			return "account/login";
		}
		// A new session ID on login, so an ID set before login can't be used to take over the account.
		HttpSession session = request.getSession();
		request.changeSessionId();
		session.setAttribute(CID, customer.get().getId());
		session.setAttribute(NAME, customer.get().getName());
		return "redirect:" + (isSafeNext(next) ? next : "/welcome");
	}

	@PostMapping("/logout")
	public String logout(HttpSession session, RedirectAttributes redirect) {
		session.invalidate();
		redirect.addFlashAttribute(NOTICE, "You have been logged out.");
		return "redirect:/";
	}

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("form", new CustomerForm());
		return "account/signup";
	}

	@PostMapping("/signup")
	public String create(@Valid @ModelAttribute("form") CustomerForm form, BindingResult result,
			@RequestParam(required = false) String next, RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return "account/signup";
		}
		try {
			this.accounts.register(form);
		}
		catch (EmailTakenException ex) {
			result.rejectValue("email", "email.taken", "is already used by another account");
			return "account/signup";
		}
		redirect.addFlashAttribute(NOTICE, "Your account was created. You can log in now.");
		if (isSafeNext(next)) {
			redirect.addAttribute("next", next);
		}
		return "redirect:/login";
	}

	@GetMapping("/welcome")
	public String welcome() {
		return "welcome";
	}

	@GetMapping("/account/edit")
	public String edit(@SessionAttribute(CID) int cid, HttpSession session, Model model) {
		Optional<CustomerForm> form = this.accounts.editForm(cid);
		if (form.isEmpty()) {
			session.invalidate();
			return "redirect:/";
		}
		model.addAttribute("form", form.get());
		addPaymentDetails(cid, model);
		return "account/edit";
	}

	@PostMapping("/account/edit")
	public String update(@SessionAttribute(CID) int cid, @Valid @ModelAttribute("form") CustomerForm form,
			BindingResult result, HttpSession session, Model model, RedirectAttributes redirect) {
		if (result.hasErrors()) {
			addPaymentDetails(cid, model);
			return "account/edit";
		}
		try {
			if (this.accounts.update(cid, form).isEmpty()) {
				session.invalidate();
				return "redirect:/";
			}
		}
		catch (EmailTakenException ex) {
			result.rejectValue("email", "email.taken", "is already used by another account");
			addPaymentDetails(cid, model);
			return "account/edit";
		}
		session.setAttribute(NAME, form.getName());
		redirect.addFlashAttribute(NOTICE, "Your account details were saved.");
		return "redirect:/welcome";
	}

	@PostMapping("/account/cards/{savedCardId}/remove")
	public String removeCard(@SessionAttribute(CID) int cid, @PathVariable int savedCardId,
			RedirectAttributes redirect) {
		boolean removed = this.reservations.removeSavedCard(cid, savedCardId);
		redirect.addFlashAttribute(removed ? NOTICE : "warning",
				removed ? "The card was removed from your account." : "That card could not be removed.");
		return "redirect:/account/edit";
	}

	private void addPaymentDetails(int cid, Model model) {
		model.addAttribute("cards", this.reservations.savedCards(cid));
		model.addAttribute("payments", this.reservations.payments(cid));
		model.addAttribute("currentMonth", YearMonth.now());
	}

}
