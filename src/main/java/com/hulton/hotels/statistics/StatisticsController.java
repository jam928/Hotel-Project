package com.hulton.hotels.statistics;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Administrator reservation statistics.
 */
@Controller
@RequiredArgsConstructor
public class StatisticsController {

	private final StatisticsService statistics;

	@ModelAttribute("reports")
	public Report[] reports() {
		return Report.values();
	}

	@GetMapping("/admin/login")
	public String adminLogin() {
		return "admin/login";
	}

	@GetMapping("/statistics")
	public String form(Model model) {
		model.addAttribute("form", new StatisticsForm());
		return "admin/statistics";
	}

	@PostMapping("/statistics")
	public String run(@Valid @ModelAttribute("form") StatisticsForm form, BindingResult result, Model model) {
		if (!result.hasErrors()) {
			model.addAttribute("lines",
					this.statistics.run(form.getReport(), form.getBeginDate(), form.getEndDate()));
		}
		return "admin/statistics";
	}

}
