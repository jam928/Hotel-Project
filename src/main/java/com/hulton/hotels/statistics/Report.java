package com.hulton.hotels.statistics;

/** The reports an administrator can run. */
public enum Report {

	HIGHEST_RATED_ROOM("Highest rated room type"),

	FIVE_BEST_CUSTOMERS("5 best customers"),

	HIGHEST_RATED_BREAKFAST("Highest rated breakfast type"),

	HIGHEST_RATED_SERVICE("Highest rated service type");

	private final String title;

	Report(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

}
