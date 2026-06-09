package com.homeservice.domain.customer.mapper;

import com.homeservice.domain.customer.dto.response.CustomerAddressResponse;
import com.homeservice.domain.customer.dto.response.CustomerProfileResponse;
import com.homeservice.domain.customer.entity.Customer;
import com.homeservice.domain.customer.entity.CustomerAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

	@Mapping(source = "isMobileVerified", target = "isMobileVerified")
	CustomerProfileResponse toProfileResponse(Customer customer);

	CustomerAddressResponse toAddressResponse(CustomerAddress address);

	List<CustomerAddressResponse> toAddressResponseList(List<CustomerAddress> addresses);
}
