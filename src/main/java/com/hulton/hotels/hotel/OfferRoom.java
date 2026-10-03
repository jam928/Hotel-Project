package com.hulton.hotels.hotel;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** A discount on a room for a date range. */
@Entity
@Table(name = "`offer-room`")
@IdClass(OfferRoomId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OfferRoom {

	@Id
	@Column(name = "Room_no")
	private Integer roomNo;

	@Id
	@Column(name = "SDate")
	private LocalDate startDate;

	@Id
	@Column(name = "EDate")
	private LocalDate endDate;

	@Id
	@Column(name = "HotelID")
	private Integer hotelId;

	@Column(name = "Discount")
	private Integer discount;

}
