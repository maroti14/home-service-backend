package com.homeservice.domain.customer.dto.response;

import com.homeservice.common.enums.AddressLabel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerAddressResponse {

	private Long id;
	private String fullAddress;
	private String houseNumber;
	private String buildingName;
	private String street;
	private String landmark;
	private String city;
	private String state;
	private String pincode;
	private Double latitude;
	private Double longitude;
	private AddressLabel label;
	private Boolean isDefault;
}
