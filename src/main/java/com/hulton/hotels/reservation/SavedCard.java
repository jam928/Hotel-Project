package com.hulton.hotels.reservation;

/** A card on the customer's account page, with the ID used to remove it. */
public record SavedCard(Integer id, CreditCard card) {
}
