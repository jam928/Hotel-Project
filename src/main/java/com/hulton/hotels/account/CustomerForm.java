package com.hulton.hotels.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Account details from the sign-up and edit-account forms. Sizes match the customer table. */
@Data
public class CustomerForm {

	@NotBlank
	@Size(max = 32)
	private String name;

	@NotBlank
	@Size(max = 32)
	private String address;

	@NotBlank
	@Pattern(regexp = "\\(?\\d{3}\\)?[-. ]?\\d{3}[-. ]?\\d{4}", message = "must look like 555-555-5555")
	private String phone;

	@NotBlank
	@Email
	@Size(max = 32)
	private String email;

	@NotBlank
	@Size(max = 32)
	private String password;

}
