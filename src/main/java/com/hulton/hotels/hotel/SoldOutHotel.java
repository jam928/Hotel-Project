package com.hulton.hotels.hotel;

import java.time.LocalDate;

/**
 * A hotel with rooms that match the search but none free for its dates, with the
 * earliest stay of the same length that one of them is free for.
 */
public record SoldOutHotel(HotelCard card, LocalDate checkin, LocalDate checkout) {
}
