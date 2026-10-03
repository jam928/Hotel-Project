package com.hulton.hotels.review;

/**
 * The things a customer can review.
 */
public enum ReviewKind {

	ROOM("Room Reviews", "room"),

	BREAKFAST("Breakfast Review", "breakfast"),

	SERVICE("Service Review", "service");

	private final String title;

	private final String label;

	ReviewKind(String title, String label) {
		this.title = title;
		this.label = label;
	}

	public String title() {
		return this.title;
	}

	/** Lower-case name, as used in URLs and page headings. */
	public String label() {
		return this.label;
	}

	public static ReviewKind fromLabel(String label) {
		for (ReviewKind kind : values()) {
			if (kind.label.equals(label)) {
				return kind;
			}
		}
		throw new IllegalArgumentException("Unknown review kind: " + label);
	}

}
