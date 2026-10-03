package com.hulton.hotels.statistics;

import java.time.LocalDate;
import java.util.List;

import com.hulton.hotels.account.CustomerRepository;
import com.hulton.hotels.hotel.HotelRepository;
import com.hulton.hotels.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Administrator reports over a date range.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

	private static final int BEST_CUSTOMER_COUNT = 5;

	private final HotelRepository hotels;

	private final CustomerRepository customers;

	private final ReviewRepository reviews;

	/** One line per hotel: room type, hotel ID, street. */
	public List<String> highestRatedRoomTypes(LocalDate beginDate, LocalDate endDate) {
		return this.hotels.findHighestRatedRoomTypes(beginDate, endDate)
			.stream()
			.map((row) -> row.getRoomtype() + ", " + row.getHotelId() + ", " + row.getStreet())
			.toList();
	}

	public List<String> bestCustomers(LocalDate beginDate, LocalDate endDate) {
		return this.customers.findBestCustomerNames(beginDate, endDate, Limit.of(BEST_CUSTOMER_COUNT));
	}

	public List<String> highestRatedBreakfastTypes(LocalDate beginDate, LocalDate endDate) {
		return this.reviews.findReviewedBreakfastTypes(beginDate, endDate);
	}

	public List<String> highestRatedServiceTypes(LocalDate beginDate, LocalDate endDate) {
		return this.reviews.findReviewedServiceTypes(beginDate, endDate);
	}

	public List<String> run(Report report, LocalDate beginDate, LocalDate endDate) {
		return switch (report) {
			case HIGHEST_RATED_ROOM -> highestRatedRoomTypes(beginDate, endDate);
			case FIVE_BEST_CUSTOMERS -> bestCustomers(beginDate, endDate);
			case HIGHEST_RATED_BREAKFAST -> highestRatedBreakfastTypes(beginDate, endDate);
			case HIGHEST_RATED_SERVICE -> highestRatedServiceTypes(beginDate, endDate);
		};
	}

}
