package com.hulton.hotels.support;

import java.math.BigDecimal;

import com.hulton.hotels.hotel.Breakfast;
import com.hulton.hotels.hotel.Hotel;
import com.hulton.hotels.hotel.Room;
import com.hulton.hotels.hotel.ServiceOffering;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

/** Builds entities for unit tests; they only have JPA's constructor and no setters. */
public final class Entities {

	private Entities() {
	}

	public static Hotel hotel(int id, String city) {
		Hotel hotel = BeanUtils.instantiateClass(Hotel.class);
		ReflectionTestUtils.setField(hotel, "id", id);
		ReflectionTestUtils.setField(hotel, "street", "1 Main St");
		ReflectionTestUtils.setField(hotel, "city", city);
		ReflectionTestUtils.setField(hotel, "state", "NY");
		ReflectionTestUtils.setField(hotel, "country", "United States");
		return hotel;
	}

	public static Room room(int hotelId, int roomNo, String roomtype, String price, int capacity) {
		Room room = BeanUtils.instantiateClass(Room.class);
		ReflectionTestUtils.setField(room, "hotelId", hotelId);
		ReflectionTestUtils.setField(room, "roomNo", roomNo);
		ReflectionTestUtils.setField(room, "roomtype", roomtype);
		ReflectionTestUtils.setField(room, "price", new BigDecimal(price));
		ReflectionTestUtils.setField(room, "capacity", capacity);
		ReflectionTestUtils.setField(room, "floorNo", roomNo / 100);
		ReflectionTestUtils.setField(room, "description", "A " + roomtype + " room");
		return room;
	}

	public static Breakfast breakfast(int hotelId, String type, String price) {
		Breakfast breakfast = BeanUtils.instantiateClass(Breakfast.class);
		ReflectionTestUtils.setField(breakfast, "hotelId", hotelId);
		ReflectionTestUtils.setField(breakfast, "type", type);
		ReflectionTestUtils.setField(breakfast, "price", new BigDecimal(price));
		return breakfast;
	}

	public static ServiceOffering service(int hotelId, String type, String cost) {
		ServiceOffering service = BeanUtils.instantiateClass(ServiceOffering.class);
		ReflectionTestUtils.setField(service, "hotelId", hotelId);
		ReflectionTestUtils.setField(service, "type", type);
		ReflectionTestUtils.setField(service, "cost", new BigDecimal(cost));
		return service;
	}

}
