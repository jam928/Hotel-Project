package com.hulton.hotels;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.hulton.hotels.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@IntegrationTest
class HultonHotelsApplicationIT {

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void migrationsCreateTheSchemaAndSampleData() {
		List<String> versions = this.jdbc.queryForList(
				"select version from flyway_schema_history where success = 1 order by installed_rank", String.class);
		assertThat(versions).containsExactly("1", "1.1", "1.2", "1.3", "2", "2.1", "3");
		assertThat(count("hotel")).isEqualTo(4);
		assertThat(count("room")).isEqualTo(96);
		assertThat(count("customer")).isEqualTo(5);
		assertThat(count("reservation")).isEqualTo(12);
		assertThat(count("review")).isEqualTo(24);
	}

	@Test
	void sampleRoomsFollowTheLayoutAndHavePhotos() {
		assertThat(this.jdbc.queryForObject(
				"select Roomtype from room where HotelID = 1 and Room_no = 401", String.class)).isEqualTo("suite");
		assertThat(this.jdbc.queryForObject(
				"select Roomtype from room where HotelID = 1 and Room_no = 104", String.class)).isEqualTo("double");
		assertThat(this.jdbc.queryForObject("select count(*) from room where ImageKey is null", Integer.class))
			.isZero();
		assertThat(this.jdbc.queryForObject("select ImageKey from hotel where HotelID = 4", String.class))
			.isEqualTo("hotels/toronto.jpg");
	}

	private int count(String table) {
		return this.jdbc.queryForObject("select count(*) from `" + table + "`", Integer.class);
	}

}
