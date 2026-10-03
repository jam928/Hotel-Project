package com.hulton.hotels.hotel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hotel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hotel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HotelID")
	private Integer id;

	@Column(name = "Street")
	private String street;

	@Column(name = "City")
	private String city;

	@Column(name = "State")
	private String state;

	@Column(name = "ZIP")
	private String zip;

	@Column(name = "Country")
	private String country;

	/** Key of the hotel's photo in the image bucket, or {@code null}. */
	@Column(name = "ImageKey")
	private String imageKey;

	/** Hotels have no name column, so each is known by its city. */
	public String getName() {
		return "Hulton " + this.city;
	}

	public String getLocation() {
		return this.city + ", " + this.state;
	}

}
