package com.hulton.hotels.reservation;

import java.time.LocalDate;
import java.time.YearMonth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "creditcard")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreditCard {

	/** 12 to 16 digits, the size of the Cnumber column. */
	public static final String NUMBER_PATTERN = "\\d{12,16}";

	@Id
	@Column(name = "Cnumber")
	private String number;

	@Column(name = "ExpDate")
	private LocalDate expires;

	@Column(name = "SecCode")
	private Integer securityCode;

	/** One of VISA, AMEX, DISC, MSTR. */
	@Column(name = "Type")
	private String type;

	@Column(name = "Name")
	private String name;

	@Column(name = "BillingAddr")
	private String billingAddress;

	public String getTypeLabel() {
		return CardType.labelOf(this.type);
	}

	public String getLastFour() {
		return lastFour(this.number);
	}

	/** Cards expire at the end of their expiry month. */
	public boolean isExpired(YearMonth currentMonth) {
		return YearMonth.from(this.expires).isBefore(currentMonth);
	}

	static String lastFour(String number) {
		return number.substring(Math.max(0, number.length() - 4));
	}

}
