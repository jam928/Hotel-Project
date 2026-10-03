package com.hulton.hotels.reservation;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/** The room and extras picked on a hotel's page. Breakfast and service are optional. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest implements Serializable {

	@NotNull
	private Integer hotelId;

	@NotNull(message = "Choose a room")
	private Integer roomNo;

	@NotNull
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate checkin;

	@NotNull
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate checkout;

	private int guests = 1;

	private String breakfast;

	private String service;

}
