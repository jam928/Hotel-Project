package com.hulton.hotels.hotel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The standard set of rooms for a hotel: four floors of six rooms (101–106 … 401–406).
 * Used by the sample data migration and by {@link EmptyHotelRoomLoader} for hotels added
 * without rooms.
 */
public final class RoomLayout {

	public static final String INSERT_SQL = """
			INSERT INTO `room` (`Room_no`, `HotelID`, `Price`, `Capacity`, `Floor_no`, `Description`, `Roomtype`)
			VALUES (?, ?, ?, ?, ?, ?, ?)
			""";

	private static final int FLOORS = 4;

	private static final int ROOMS_PER_FLOOR = 6;

	/** Price multiplier per sample hotel ID, since a night in New York costs more than in Toronto. */
	private static final Map<Integer, BigDecimal> HOTEL_RATES = Map.of(1, new BigDecimal("1.40"), 2,
			new BigDecimal("1.25"), 3, new BigDecimal("1.10"), 4, BigDecimal.ONE);

	/** Each higher floor adds this much per night. */
	private static final BigDecimal FLOOR_PREMIUM = new BigDecimal("5.00");

	private enum RoomType {

		STANDARD("standard", "129.00", 2, "Queen bed, work desk and rain shower"),
		DOUBLE("double", "169.00", 4, "Two queen beds and a seating area"),
		DELUXE("deluxe", "229.00", 3, "King bed, city view and soaking tub"),
		SUITE("suite", "389.00", 5, "Separate living room, king bed and panoramic view");

		private final String dbValue;

		private final BigDecimal basePrice;

		private final int capacity;

		private final String description;

		RoomType(String dbValue, String basePrice, int capacity, String description) {
			this.dbValue = dbValue;
			this.basePrice = new BigDecimal(basePrice);
			this.capacity = capacity;
			this.description = description;
		}

	}

	/** One row of {@link #INSERT_SQL}. */
	public record Row(int roomNo, int hotelId, BigDecimal price, int capacity, int floorNo, String description,
			String roomtype) {

		public Object[] params() {
			return new Object[] { this.roomNo, this.hotelId, this.price, this.capacity, this.floorNo,
					this.description, this.roomtype };
		}

	}

	private RoomLayout() {
	}

	/** The IDs of the sample hotels, which the sample data migration fills. */
	public static List<Integer> sampleHotelIds() {
		return HOTEL_RATES.keySet().stream().sorted().toList();
	}

	/** The rooms for the hotel. Hotels other than the sample ones get the base prices. */
	public static List<Row> roomsFor(int hotelId) {
		BigDecimal rate = HOTEL_RATES.getOrDefault(hotelId, BigDecimal.ONE);
		List<Row> rows = new ArrayList<>();
		for (int floor = 1; floor <= FLOORS; floor++) {
			for (int position = 1; position <= ROOMS_PER_FLOOR; position++) {
				RoomType type = typeAt(floor, position);
				BigDecimal price = type.basePrice.multiply(rate)
					.add(FLOOR_PREMIUM.multiply(BigDecimal.valueOf(floor - 1)))
					.setScale(2, RoundingMode.HALF_UP);
				rows.add(new Row(floor * 100 + position, hotelId, price, type.capacity, floor, type.description,
						type.dbValue));
			}
		}
		return rows;
	}

	/** Top floor: two suites then deluxe rooms. Other floors: three standard, two double, one deluxe. */
	private static RoomType typeAt(int floor, int position) {
		if (floor == FLOORS) {
			return (position <= 2) ? RoomType.SUITE : RoomType.DELUXE;
		}
		if (position <= 3) {
			return RoomType.STANDARD;
		}
		return (position <= 5) ? RoomType.DOUBLE : RoomType.DELUXE;
	}

}
