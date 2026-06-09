package com.homeservice.domain.customer.dto.request;

import com.homeservice.common.enums.AddressLabel;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveAddressRequest {

	@NotBlank(message = "Full address is required")
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

	@NotNull(message = "Latitude is required")
	@DecimalMin(value = "-90.0", message = "Invalid latitude")
	@DecimalMax(value = "90.0", message = "Invalid latitude")
	private Double latitude;

	@NotNull(message = "Longitude is required")
	@DecimalMin(value = "-180.0", message = "Invalid longitude")
	@DecimalMax(value = "180.0", message = "Invalid longitude")
	private Double longitude;

	private AddressLabel label = AddressLabel.HOME;

	private Boolean isDefault = false;
}
