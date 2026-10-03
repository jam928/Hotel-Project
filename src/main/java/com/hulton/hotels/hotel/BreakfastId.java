package com.hulton.hotels.hotel;

import java.io.Serializable;

public record BreakfastId(String type, Integer hotelId) implements Serializable {
}
