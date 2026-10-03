package com.hulton.hotels.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "InvoiceNo")
	private Integer invoiceNo;

	@Column(name = "ResDate")
	private LocalDate reservedOn;

	@Column(name = "TotalAmt")
	private BigDecimal totalAmount;

	@Column(name = "CID")
	private Integer customerId;

	@Column(name = "Room_no")
	private Integer roomNo;

	@Column(name = "HotelID")
	private Integer hotelId;

	@Column(name = "InDate")
	private LocalDate inDate;

	@Column(name = "OutDate")
	private LocalDate outDate;

	@Column(name = "NoOfDays")
	private Integer numberOfDays;

	@Column(name = "Cnumber")
	private String cardNumber;

	@Column(name = "bType")
	private String breakfastType;

	@Column(name = "sType")
	private String serviceType;

}
