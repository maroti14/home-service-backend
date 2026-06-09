package com.homeservice.infrastructure.location.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceAutoCompleteResponse {

	private List<PlaceSuggestion> suggestions;

	@Getter
	@Builder
	public static class PlaceSuggestion {
		private String placeId;
		private String description;
		private String mainText;
		private String secondaryText;
	}
}
