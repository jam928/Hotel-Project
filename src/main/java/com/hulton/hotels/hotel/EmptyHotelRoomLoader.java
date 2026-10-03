package com.hulton.hotels.hotel;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gives every hotel that has no rooms the standard {@link RoomLayout} on startup, so
 * there is always something to book. Rooms are not tied to dates: a room is free on
 * any night it has no reservation, so once added they can be booked this month and
 * every month after. Hotels that already have rooms are left alone.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBooleanProperty("hulton.rooms.seed-empty-hotels")
class EmptyHotelRoomLoader implements ApplicationRunner {

	private final JdbcTemplate jdbc;

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		List<Integer> emptyHotels = this.jdbc.queryForList("""
				SELECT h.HotelID FROM hotel h
				WHERE NOT EXISTS (SELECT 1 FROM room r WHERE r.HotelID = h.HotelID)
				ORDER BY h.HotelID
				""", Integer.class);
		for (int hotelId : emptyHotels) {
			List<Object[]> rows = RoomLayout.roomsFor(hotelId).stream().map(RoomLayout.Row::params).toList();
			this.jdbc.batchUpdate(RoomLayout.INSERT_SQL, rows);
			log.info("Hotel {} had no rooms: added {}", hotelId, rows.size());
		}
	}

}
