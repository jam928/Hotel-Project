package com.hulton.hotels.hotel;

import java.time.LocalDate;

/** A room that is booked for the searched dates, with the first stay of the same length it is free for. */
public record NextAvailable(Room room, LocalDate checkin, LocalDate checkout) {
}
