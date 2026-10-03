package com.hulton.hotels.account;

/** Thrown when an account change would reuse another customer's email address. */
public class EmailTakenException extends RuntimeException {

	public EmailTakenException(String email) {
		super("An account with email " + email + " already exists");
	}

}
