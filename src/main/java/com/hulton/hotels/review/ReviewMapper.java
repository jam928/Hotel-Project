package com.hulton.hotels.review;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface ReviewMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "textComment", source = "form.comment")
	@Mapping(target = "roomNo", ignore = true)
	@Mapping(target = "breakfastType", ignore = true)
	@Mapping(target = "serviceType", ignore = true)
	Review toReview(ReviewForm form, Integer customerId, @Context ReviewKind kind);

	/** Stores the reviewed item in the column that matches its kind. */
	@AfterMapping
	default void setReviewedItem(ReviewForm form, @Context ReviewKind kind, @MappingTarget Review review) {
		switch (kind) {
			case ROOM -> review.setRoomNo(Integer.valueOf(form.getItem()));
			case BREAKFAST -> review.setBreakfastType(form.getItem());
			case SERVICE -> review.setServiceType(form.getItem());
		}
	}

}
