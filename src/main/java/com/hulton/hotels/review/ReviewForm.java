package com.hulton.hotels.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** A review submitted from the review form. */
@Data
public class ReviewForm {

	/** Room number, breakfast type or service type, depending on the review kind. */
	@NotBlank
	@Size(max = 32)
	private String item;

	@NotNull
	private Integer hotelId;

	@NotBlank
	@Size(max = 2000)
	private String comment;

	@NotNull
	@Min(1)
	@Max(3)
	private Integer rating = 3;

}
