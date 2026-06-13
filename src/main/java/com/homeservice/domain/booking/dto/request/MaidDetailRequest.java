package com.homeservice.domain.booking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaidDetailRequest {

	private Boolean sweeping = true;
	private Boolean mopping = true;
	private Boolean dusting = false;
	private Boolean dishwashing = false;
	private Boolean laundry = false;

	// ONE_TIME / DAILY / WEEKLY
	private String frequency;
}
