package com.hulton.hotels.hotel;

import static org.assertj.core.api.Assertions.assertThat;

import com.hulton.hotels.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@IntegrationTest
class EmptyHotelRoomLoaderIT {

	@Autowired
	private EmptyHotelRoomLoader loader;

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void addsRoomsToHotelsWithoutAny() {
		this.jdbc.update("insert into hotel (HotelID, Street, City, State, ZIP, Country) values (?, ?, ?, ?, ?, ?)",
				99, "1 Main St", "Chicago", "IL", "60601", "United States");

		this.loader.run(null);

		assertThat(roomCount(99)).isEqualTo(24);
		assertThat(this.jdbc.queryForObject("select Price from room where HotelID = 99 and Room_no = 401",
				String.class)).isEqualTo("404.00");
	}

	@Test
	void leavesHotelsWithRoomsAlone() {
		this.loader.run(null);

		assertThat(roomCount(1)).isEqualTo(24);
		assertThat(this.jdbc.queryForObject("select count(*) from room", Integer.class)).isEqualTo(96);
	}

	private int roomCount(int hotelId) {
		return this.jdbc.queryForObject("select count(*) from room where HotelID = ?", Integer.class, hotelId);
	}

}
