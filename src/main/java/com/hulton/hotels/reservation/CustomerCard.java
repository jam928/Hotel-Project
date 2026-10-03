package com.hulton.hotels.reservation;

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

/** A card saved to a customer's account, the first time they pay with it. */
@Entity
@Table(name = "customer_card")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerCard {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "CID")
	private Integer customerId;

	@Column(name = "Cnumber")
	private String cardNumber;

	@Column(name = "SavedOn")
	private LocalDate savedOn;

	public CustomerCard(Integer customerId, String cardNumber, LocalDate savedOn) {
		this.customerId = customerId;
		this.cardNumber = cardNumber;
		this.savedOn = savedOn;
	}

}
