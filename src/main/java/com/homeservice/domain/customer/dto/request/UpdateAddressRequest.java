package com.homeservice.domain.customer.dto.request;

import com.homeservice.common.enums.AddressLabel;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAddressRequest {

	@Size(max = 500)
	private String fullAddress;

	@Size(max = 100)
	private String houseNumber;

	@Size(max = 200)
	private String buildingName;

	@Size(max = 200)
	private String street;

	@Size(max = 200)
	private String landmark;

	@Size(max = 100)
	private String city;

	@Size(max = 100)
	private String state;

	@Pattern(regexp = "^[1-9][0-9]{5}$", message = "Enter valid 6-digit pincode")
	private String pincode;

	@DecimalMin(value = "-90.0")
	@DecimalMax(value = "90.0")
	private Double latitude;

	@DecimalMin(value = "-180.0")
	@DecimalMax(value = "180.0")
	private Double longitude;

	private AddressLabel label;

	private Boolean isDefault;
}
