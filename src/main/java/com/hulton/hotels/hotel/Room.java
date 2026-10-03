package com.hulton.hotels.hotel;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "room")
@IdClass(RoomId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {

	@Id
	@Column(name = "Room_no")
	private Integer roomNo;

	@Id
	@Column(name = "HotelID")
	private Integer hotelId;

	@Column(name = "Price")
	private BigDecimal price;

	@Column(name = "Capacity")
	private Integer capacity;

	@Column(name = "Floor_no")
	private Integer floorNo;

	@Column(name = "Description")
	private String description;

	@Column(name = "Roomtype")
	private String roomtype;

	/** Key of the room's photo in the image bucket, or {@code null}. */
	@Column(name = "ImageKey")
	private String imageKey;

}
