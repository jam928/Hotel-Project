package com.hulton.hotels.hotel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, ServiceOfferingId> {

	List<ServiceOffering> findByHotelIdOrderByCost(Integer hotelId);

}
