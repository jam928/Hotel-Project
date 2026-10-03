package com.hulton.hotels.hotel;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

/**
 * The public pages: the landing page, hotel search and a hotel's rooms. None of
 * them need a login; reserving a room asks for one at payment.
 */
@Controller
@RequiredArgsConstructor
public class HotelController {

	private static final int POPULAR_COUNT = 6;

	private static final int REVIEW_COUNT = 6;

	private final HotelService hotels;

	@ModelAttribute("search")
	public HotelSearch normalizedSearch(HotelSearch search) {
		search.normalize(LocalDate.now());
		return search;
	}

	@ModelAttribute("today")
	public LocalDate today() {
		return LocalDate.now();
	}

	@GetMapping("/")
	public String landing(Model model) {
		model.addAttribute("destinations", this.hotels.destinations());
		model.addAttribute("popular", this.hotels.popular(POPULAR_COUNT));
		return "landing";
	}

	@GetMapping("/hotels")
	public String search(@ModelAttribute(name = "search", binding = false) HotelSearch search, Model model) {
		model.addAttribute("destinations", this.hotels.destinations());
		model.addAttribute("roomTypes", HotelService.ROOM_TYPES);
		List<HotelResult> results = this.hotels.search(search);
		model.addAttribute("results", results);
		model.addAttribute("soldOut", this.hotels.soldOut(search, results));
		return "hotel/search";
	}

	@GetMapping("/hotels/{hotelId}")
	public String show(@PathVariable int hotelId,
			@ModelAttribute(name = "search", binding = false) HotelSearch search, Model model) {
		HotelCard card = this.hotels.card(hotelId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		model.addAttribute("card", card);
		model.addAttribute("roomTypes", HotelService.ROOM_TYPES);
		model.addAttribute("rooms", this.hotels.availableRooms(hotelId, search));
		model.addAttribute("bookedRooms", this.hotels.bookedRooms(hotelId, search));
		model.addAttribute("breakfasts", this.hotels.breakfasts(hotelId));
		model.addAttribute("services", this.hotels.services(hotelId));
		model.addAttribute("reviews", this.hotels.recentReviews(hotelId, REVIEW_COUNT));
		return "hotel/show";
	}

}
