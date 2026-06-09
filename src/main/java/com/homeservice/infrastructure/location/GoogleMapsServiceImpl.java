package com.homeservice.infrastructure.location;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PlaceAutocompleteRequest;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.AutocompletePrediction;
import com.google.maps.model.ComponentFilter;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceAutocompleteType;
import com.google.maps.model.PlaceDetails;
import com.homeservice.infrastructure.location.dto.PlaceAutoCompleteResponse;
import com.homeservice.infrastructure.location.dto.PlaceDetailResponse;
import com.homeservice.infrastructure.location.dto.ReverseGeocodeResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GoogleMapsServiceImpl implements GoogleMapsService {

	@Value("${google.maps.api-key:}")
	private String apiKey;

	@Value("${google.maps.enabled:false}")
	private boolean mapsEnabled;

	private GeoApiContext context;

	@PostConstruct
	public void init() {
		if (mapsEnabled && apiKey != null && !apiKey.isBlank()) {
			context = new GeoApiContext.Builder().apiKey(apiKey).build();
			log.info("Google Maps initialised.");
		} else {
			log.warn("Google Maps disabled. " + "OTP printed to console. " + "Set google.maps.enabled=true "
					+ "to enable.");
		}
	}

	// ── Reverse Geocode ───────────────────────────────

	@Override
	public ReverseGeocodeResponse reverseGeocode(Double latitude, Double longitude) {

		if (!mapsEnabled) {
			return buildMockGeocode(latitude, longitude);
		}

		try {
			GeocodingResult[] results = GeocodingApi.reverseGeocode(context, new LatLng(latitude, longitude))
					.language("en").await();

			if (results == null || results.length == 0) {
				log.warn("No geocoding results | " + "lat={} lng={}", latitude, longitude);
				return buildMockGeocode(latitude, longitude);
			}

			return parseGeocodingResult(results[0], latitude, longitude);

		} catch (Exception e) {
			log.error("Reverse geocode failed | " + "lat={} lng={} error={}", latitude, longitude, e.getMessage());
			// graceful fallback
			return buildMockGeocode(latitude, longitude);
		}
	}

	// ── Autocomplete ──────────────────────────────────

	@Override
	public PlaceAutoCompleteResponse autocomplete(String query, String sessionToken) {

		if (!mapsEnabled) {
			return buildMockAutocomplete(query);
		}

		try {
			// ── FIX: handle null sessionToken ─────────
			PlaceAutocompleteRequest.SessionToken token;

			if (sessionToken != null && !sessionToken.isBlank()) {
				token = new PlaceAutocompleteRequest.SessionToken(sessionToken);
			} else {
				// generate a random one if not provided
				token = new PlaceAutocompleteRequest.SessionToken(java.util.UUID.randomUUID().toString());
			}

			AutocompletePrediction[] predictions = PlacesApi.placeAutocomplete(context, query, token).language("en")
					.components(ComponentFilter.country("IN")).await();

			if (predictions == null || predictions.length == 0) {
				return PlaceAutoCompleteResponse.builder().suggestions(List.of()).build();
			}

			List<PlaceAutoCompleteResponse.PlaceSuggestion> suggestions = Arrays.stream(predictions)
					.map(p -> PlaceAutoCompleteResponse.PlaceSuggestion.builder().placeId(p.placeId)
							.description(p.description)
							.mainText(p.structuredFormatting != null ? p.structuredFormatting.mainText : p.description)
							.secondaryText(p.structuredFormatting != null ? p.structuredFormatting.secondaryText : "")
							.build())
					.collect(Collectors.toList());

			return PlaceAutoCompleteResponse.builder().suggestions(suggestions).build();

		} catch (Exception e) {
			log.error("Autocomplete failed | " + "query={} error={}", query, e.getMessage());
			return buildMockAutocomplete(query);
		}
	}

	// ── Place Details ─────────────────────────────────

	@Override
	public PlaceDetailResponse getPlaceDetails(String placeId) {

		if (!mapsEnabled) {
			return buildMockPlaceDetail(placeId);
		}

		try {
			PlaceDetails details = PlacesApi.placeDetails(context, placeId)
					.fields(PlaceDetailsRequest.FieldMask.FORMATTED_ADDRESS,
							PlaceDetailsRequest.FieldMask.ADDRESS_COMPONENT, PlaceDetailsRequest.FieldMask.GEOMETRY)
					.await();

			if (details == null) {
				return buildMockPlaceDetail(placeId);
			}

			String city = getComponent(details.addressComponents, AddressComponentType.LOCALITY);
			String state = getComponent(details.addressComponents, AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1);
			String pincode = getComponent(details.addressComponents, AddressComponentType.POSTAL_CODE);

			return PlaceDetailResponse.builder().placeId(placeId).fullAddress(details.formattedAddress).city(city)
					.state(state).pincode(pincode)
					.latitude(details.geometry != null ? details.geometry.location.lat : 0.0)
					.longitude(details.geometry != null ? details.geometry.location.lng : 0.0).build();

		} catch (Exception e) {
			log.error("Place details failed | " + "placeId={} error={}", placeId, e.getMessage());
			return buildMockPlaceDetail(placeId);
		}
	}

	// ── Private helpers ───────────────────────────────

	private ReverseGeocodeResponse parseGeocodingResult(GeocodingResult result, Double lat, Double lng) {

		return ReverseGeocodeResponse.builder().fullAddress(result.formattedAddress)
				.houseNumber(getComponent(result.addressComponents, AddressComponentType.STREET_NUMBER))
				.street(getComponent(result.addressComponents, AddressComponentType.ROUTE))
				.city(getComponent(result.addressComponents, AddressComponentType.LOCALITY))
				.state(getComponent(result.addressComponents, AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1))
				.pincode(getComponent(result.addressComponents, AddressComponentType.POSTAL_CODE)).latitude(lat)
				.longitude(lng).build();
	}

	private String getComponent(AddressComponent[] components, AddressComponentType type) {

		if (components == null)
			return null;

		return Arrays.stream(components).filter(c -> Arrays.asList(c.types).contains(type)).map(c -> c.longName)
				.findFirst().orElse(null);
	}

	// ── Mock responses (dev mode) ─────────────────────

	private ReverseGeocodeResponse buildMockGeocode(Double lat, Double lng) {
		return ReverseGeocodeResponse.builder().fullAddress("Sector 14, Gurgaon, " + "Haryana 122001, India")
				.houseNumber("14").street("Sector 14 Road").city("Gurgaon").state("Haryana").pincode("122001")
				.latitude(lat).longitude(lng).build();
	}

	private PlaceAutoCompleteResponse buildMockAutocomplete(String query) {
		return PlaceAutoCompleteResponse.builder()
				.suggestions(List.of(
						PlaceAutoCompleteResponse.PlaceSuggestion.builder().placeId("mock_place_1")
								.description(query + ", Gurgaon, Haryana, India").mainText(query)
								.secondaryText("Gurgaon, Haryana").build(),
						PlaceAutoCompleteResponse.PlaceSuggestion.builder().placeId("mock_place_2")
								.description(query + ", Pune, Maharashtra, India").mainText(query)
								.secondaryText("Pune, Maharashtra").build()))
				.build();
	}

	private PlaceDetailResponse buildMockPlaceDetail(String placeId) {
		return PlaceDetailResponse.builder().placeId(placeId).fullAddress("Sector 14, Gurgaon, " + "Haryana 122001")
				.city("Gurgaon").state("Haryana").pincode("122001").latitude(28.4744).longitude(77.0266).build();
	}
}