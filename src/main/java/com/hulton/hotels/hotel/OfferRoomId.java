package com.hulton.hotels.hotel;

import java.io.Serializable;
import java.time.LocalDate;

public record OfferRoomId(Integer roomNo, LocalDate startDate, LocalDate endDate, Integer hotelId)
		implements Serializable {
}
