package com.hulton.hotels.statistics;

import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Data
public class StatisticsForm {

	@NotNull
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate beginDate;

	@NotNull
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate endDate;

	@NotNull
	private Report report = Report.HIGHEST_RATED_ROOM;

	@AssertTrue(message = "End date cannot be before the begin date.")
	public boolean isDateRangeValid() {
		return this.beginDate == null || this.endDate == null || !this.endDate.isBefore(this.beginDate);
	}

}
