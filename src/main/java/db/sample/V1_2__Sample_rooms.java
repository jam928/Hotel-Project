package db.sample;

import java.sql.PreparedStatement;

import com.hulton.hotels.hotel.RoomLayout;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

/**
 * Fills each demo hotel from {@code V1_1__Sample_hotels.sql} with the rooms from
 * {@link RoomLayout}. Generated in Java rather than written out as ~100 SQL rows so the
 * layout and pricing rules stay readable.
 */
public class V1_2__Sample_rooms extends BaseJavaMigration {

	@Override
	public void migrate(Context context) throws Exception {
		try (PreparedStatement insert = context.getConnection().prepareStatement(RoomLayout.INSERT_SQL)) {
			for (int hotelId : RoomLayout.sampleHotelIds()) {
				for (RoomLayout.Row row : RoomLayout.roomsFor(hotelId)) {
					Object[] params = row.params();
					for (int i = 0; i < params.length; i++) {
						insert.setObject(i + 1, params[i]);
					}
					insert.addBatch();
				}
			}
			insert.executeBatch();
		}
	}

}
