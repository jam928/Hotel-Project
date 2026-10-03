package com.hulton.hotels.review;

import static com.hulton.hotels.web.LoginRequiredInterceptor.CID;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Reservation history and reviews for rooms, breakfasts and services.
 */
@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

	private static final String KIND = "{kind:room|breakfast|service}";

	private final ReviewService reviews;

	@ModelAttribute("kind")
	public ReviewKind kind(@PathVariable(required = false) String kind) {
		return (kind != null) ? ReviewKind.fromLabel(kind) : null;
	}

	@GetMapping("/history")
	public String history(@SessionAttribute(CID) int cid, Model model) {
		model.addAttribute("groups",
				List.of(new ReviewableGroup("Rooms", ReviewKind.ROOM, this.reviews.reviewable(ReviewKind.ROOM, cid)),
						new ReviewableGroup("Breakfasts", ReviewKind.BREAKFAST,
								this.reviews.reviewable(ReviewKind.BREAKFAST, cid)),
						new ReviewableGroup("Services", ReviewKind.SERVICE,
								this.reviews.reviewable(ReviewKind.SERVICE, cid))));
		return "review/history";
	}

	@GetMapping("/" + KIND)
	public String list(@SessionAttribute(CID) int cid, @ModelAttribute("kind") ReviewKind kind,
			@RequestParam String item, @RequestParam int hotelid, Model model) {
		model.addAttribute("item", item);
		model.addAttribute("hotelid", hotelid);
		model.addAttribute("comments", this.reviews.comments(kind, item, hotelid, cid));
		return "review/list";
	}

	@GetMapping("/" + KIND + "/new")
	public String form(@RequestParam String item, @RequestParam int hotelid, Model model) {
		ReviewForm form = new ReviewForm();
		form.setItem(item);
		form.setHotelId(hotelid);
		model.addAttribute("form", form);
		return "review/form";
	}

	@PostMapping("/" + KIND)
	public String submit(@SessionAttribute(CID) int cid, @ModelAttribute("kind") ReviewKind kind,
			@Valid @ModelAttribute("form") ReviewForm form, BindingResult result, RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return "review/form";
		}
		this.reviews.write(kind, form, cid);
		redirect.addFlashAttribute("notice", "Thanks! Your review was saved.");
		redirect.addAttribute("item", form.getItem());
		redirect.addAttribute("hotelid", form.getHotelId());
		return "redirect:/review/" + kind.label();
	}

}
