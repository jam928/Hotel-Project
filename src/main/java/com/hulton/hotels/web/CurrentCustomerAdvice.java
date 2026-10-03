package com.hulton.hotels.web;

import static com.hulton.hotels.web.LoginRequiredInterceptor.NAME;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;

/** Makes the logged-in customer's name available to every page (for the nav bar). */
@ControllerAdvice
public class CurrentCustomerAdvice {

	@ModelAttribute("customerName")
	public String customerName(@SessionAttribute(name = NAME, required = false) String name) {
		return name;
	}

}
