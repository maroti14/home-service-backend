package com.homeservice.domain.booking.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DustingDetailRequest {

	// list of rooms: LIVING_ROOM, BEDROOM etc.
	private List<String> rooms;

	// WOOD / GLASS / MARBLE / MIXED
	private String surfaceType;

	private Integer ceilingFanCount;
}
