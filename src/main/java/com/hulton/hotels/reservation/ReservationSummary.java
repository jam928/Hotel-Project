package com.hulton.hotels.reservation;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

/** A customer's reservation as shown on the cancel page. */
public record ReservationSummary(@JsonProperty("InvoiceNo") Integer invoiceNo,
		@JsonProperty("Room_no") Integer roomNo, @JsonProperty("Description") String description,
		@JsonProperty("HotelID") Integer hotelId, @JsonProperty("InDate") LocalDate inDate,
		@JsonProperty("OutDate") LocalDate outDate) {
}
