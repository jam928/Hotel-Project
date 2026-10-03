package com.hulton.hotels.reservation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CustomerCardRepository extends JpaRepository<CustomerCard, Integer> {

	boolean existsByCustomerIdAndCardNumber(Integer customerId, String cardNumber);

	/** The cards saved to the customer's account, most recently saved first. */
	@Query("""
			select new com.hulton.hotels.reservation.SavedCard(cc.id, c)
			from CustomerCard cc join CreditCard c on c.number = cc.cardNumber
			where cc.customerId = :customerId
			order by cc.savedOn desc, cc.id desc
			""")
	List<SavedCard> findSavedByCustomerId(Integer customerId);

	@Modifying
	@Query("delete from CustomerCard cc where cc.id = :id and cc.customerId = :customerId")
	int deleteByIdAndCustomerId(Integer id, Integer customerId);

}
