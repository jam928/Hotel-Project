package com.hulton.hotels.review;

import java.util.List;

/** The items of one kind that a customer can review, as listed on the history page. */
public record ReviewableGroup(String title, ReviewKind kind, List<ReviewableItem> items) {
}
