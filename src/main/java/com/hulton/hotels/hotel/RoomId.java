package com.hulton.hotels.hotel;

import java.io.Serializable;

public record RoomId(Integer roomNo, Integer hotelId) implements Serializable {
}
