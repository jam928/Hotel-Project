package com.hulton.hotels.hotel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BreakfastRepository extends JpaRepository<Breakfast, BreakfastId> {

	List<Breakfast> findByHotelIdOrderByPrice(Integer hotelId);

}
