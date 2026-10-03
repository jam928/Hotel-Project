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

/** A paid service a hotel offers (the {@code service} table). */
@Entity
@Table(name = "service")
@IdClass(ServiceOfferingId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServiceOffering {

	@Id
	@Column(name = "sType")
	private String type;

	@Id
	@Column(name = "HotelID")
	private Integer hotelId;

	@Column(name = "sCost")
	private BigDecimal cost;

}
