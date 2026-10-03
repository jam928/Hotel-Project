package com.hulton.hotels.hotel;

import java.io.Serializable;

public record ServiceOfferingId(String type, Integer hotelId) implements Serializable {
}
