package com.homeservice.domain.customer.entity;

import com.homeservice.common.base.BaseEntity;
import com.homeservice.common.enums.AddressLabel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer_addresses", indexes = { @Index(name = "idx_address_customer_id", columnList = "customer_id"),
		@Index(name = "idx_address_is_default", columnList = "customer_id, isDefault") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerAddress extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	// full readable address
	// e.g. "Flat 402, Sector 14, Gurgaon 122001"
	@Column(nullable = false, length = 500)
	private String fullAddress;

	// flat/house number
	@Column(length = 100)
	private String houseNumber;

	// building or society name
	@Column(length = 200)
	private String buildingName;

	// street or locality
	@Column(length = 200)
	private String street;

	// landmark for worker navigation
	@Column(length = 200)
	private String landmark;

	@Column(length = 100)
	private String city;

	@Column(length = 100)
	private String state;

	@Column(length = 10)
	private String pincode;

	// GPS coordinates for dispatch engine
	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;

	// Home / Work / Other
	@Enumerated(EnumType.STRING)
	@Builder.Default
	@Column(nullable = false, length = 20)
	private AddressLabel label = AddressLabel.HOME;

	@Builder.Default
	@Column(nullable = false)
	private Boolean isDefault = false;

	@Builder.Default
	@Column(nullable = false)
	private Boolean isDeleted = false;
}