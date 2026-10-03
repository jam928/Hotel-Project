package com.hulton.hotels.account;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	Optional<Customer> findByEmailAndPassword(String email, String password);

	boolean existsByEmail(String email);

	boolean existsByEmailAndIdNot(String email, Integer id);

	/**
	 * Names of the customers who spent the most on stays that fall entirely within
	 * the period, biggest spender first.
	 */
	@Query("""
			select c.name from Reservation r join Customer c on c.id = r.customerId
			where r.inDate between :beginDate and :endDate
			  and r.outDate between :beginDate and :endDate
			group by c.id, c.name
			order by sum(r.totalAmount) desc
			""")
	List<String> findBestCustomerNames(LocalDate beginDate, LocalDate endDate, Limit limit);

}
