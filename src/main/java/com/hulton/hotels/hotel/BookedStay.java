package com.hulton.hotels.hotel;

import java.time.LocalDate;

/** The dates a room is reserved for; the room is free again on {@code outDate}. */
public record BookedStay(Integer hotelId, Integer roomNo, LocalDate inDate, LocalDate outDate) {
}
