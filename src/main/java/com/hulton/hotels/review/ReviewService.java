package com.hulton.hotels.review;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * What a customer can review, their existing reviews, and writing new ones.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

	private final ReviewRepository reviews;

	private final ReviewMapper mapper;

	public List<ReviewableItem> reviewable(ReviewKind kind, int customerId) {
		return switch (kind) {
			case ROOM -> this.reviews.findReservedRooms(customerId);
			case BREAKFAST -> this.reviews.findReservedBreakfasts(customerId);
			case SERVICE -> this.reviews.findReservedServices(customerId);
		};
	}

	public List<String> comments(ReviewKind kind, String item, int hotelId, int customerId) {
		List<Review> found = switch (kind) {
			case ROOM -> this.reviews.findByRoomNoAndHotelIdAndCustomerId(Integer.valueOf(item), hotelId, customerId);
			case BREAKFAST -> this.reviews.findByBreakfastTypeAndHotelIdAndCustomerId(item, hotelId, customerId);
			case SERVICE -> this.reviews.findByServiceTypeAndHotelIdAndCustomerId(item, hotelId, customerId);
		};
		return found.stream().map(Review::getTextComment).toList();
	}

	@Transactional
	public Review write(ReviewKind kind, ReviewForm form, int customerId) {
		return this.reviews.save(this.mapper.toReview(form, customerId, kind));
	}

}
