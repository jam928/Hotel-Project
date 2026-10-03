package com.hulton.hotels.review;

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

/**
 * A customer's review of a room, breakfast or service. Exactly one of
 * {@code roomNo}, {@code breakfastType} and {@code serviceType} is set.
 */
@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ReviewID")
	private Integer id;

	@Column(name = "Rating")
	private Integer rating;

	@Column(name = "TextComment")
	private String textComment;

	@Column(name = "Room_no")
	private Integer roomNo;

	@Column(name = "bType")
	private String breakfastType;

	@Column(name = "sType")
	private String serviceType;

	@Column(name = "HotelID")
	private Integer hotelId;

	@Column(name = "CID")
	private Integer customerId;

}
