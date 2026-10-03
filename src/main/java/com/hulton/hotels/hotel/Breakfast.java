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
@Table(name = "breakfast")
@IdClass(BreakfastId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Breakfast {

	@Id
	@Column(name = "bType")
	private String type;

	@Id
	@Column(name = "HotelID")
	private Integer hotelId;

	@Column(name = "Description")
	private String description;

	@Column(name = "bPrice")
	private BigDecimal price;

}
