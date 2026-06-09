package com.homeservice.domain.city.entity;

import com.homeservice.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cities", indexes = { @Index(name = "idx_city_code", columnList = "cityCode") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class City extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 100)
	private String name;

	@Column(nullable = false, length = 100)
	private String state;

	@Column(nullable = false, unique = true, length = 20)
	private String cityCode;

	@Builder.Default
	@Column(nullable = false)
	private Boolean isActive = true;
}