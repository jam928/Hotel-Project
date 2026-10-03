package com.hulton.hotels.account;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Customer sign-up, login and profile changes.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

	private final CustomerRepository customers;

	private final CustomerMapper mapper;

	public Optional<Customer> login(String email, String password) {
		return this.customers.findByEmailAndPassword(email, password);
	}

	/**
	 * Creates an account.
	 * @throws EmailTakenException if another account already uses the email
	 */
	@Transactional
	public Customer register(CustomerForm form) {
		if (this.customers.existsByEmail(form.getEmail())) {
			throw new EmailTakenException(form.getEmail());
		}
		return this.customers.save(this.mapper.toCustomer(form));
	}

	/** The customer's current details as an edit form, or empty if the customer no longer exists. */
	public Optional<CustomerForm> editForm(int customerId) {
		return this.customers.findById(customerId).map(this.mapper::toForm);
	}

	/**
	 * Updates the customer's details, returning the updated customer, or empty if it
	 * no longer exists.
	 * @throws EmailTakenException if another account already uses the new email
	 */
	@Transactional
	public Optional<Customer> update(int customerId, CustomerForm form) {
		if (this.customers.existsByEmailAndIdNot(form.getEmail(), customerId)) {
			throw new EmailTakenException(form.getEmail());
		}
		return this.customers.findById(customerId).map((customer) -> {
			this.mapper.update(form, customer);
			return customer;
		});
	}

}
