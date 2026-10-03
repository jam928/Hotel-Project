package com.hulton.hotels.account;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface CustomerMapper {

	@Mapping(target = "id", ignore = true)
	Customer toCustomer(CustomerForm form);

	@Mapping(target = "id", ignore = true)
	void update(CustomerForm form, @MappingTarget Customer customer);

	/** Pre-fills the edit form. The password is left blank to be re-entered. */
	@Mapping(target = "password", ignore = true)
	CustomerForm toForm(Customer customer);

}
