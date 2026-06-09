package com.homeservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.homeservice.common.enums.ServiceKey;
import com.homeservice.domain.auth.entity.Role;
import com.homeservice.domain.auth.repository.RoleRepository;
import com.homeservice.domain.servicetype.entity.ServiceType;
import com.homeservice.domain.servicetype.repository.ServiceTypeRepository;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

	private final RoleRepository roleRepo;
	private final ServiceTypeRepository serviceTypeRepo;

	@Bean
	@Transactional
	CommandLineRunner seedData() {
		return args -> {
			seedRoles();
			seedServiceTypes();
		};
	}

	private void seedRoles() {
		List.of("ROLE_CUSTOMER", "ROLE_WORKER", "ROLE_ADMIN").forEach(name -> {
			if (roleRepo.findByName(name).isEmpty()) {
				roleRepo.save(new Role(null, name));
				log.info("Role seeded | name={}", name);
			}
		});
	}

	private void seedServiceTypes() {
		record SvcData(ServiceKey key, String name, String desc, String icon, String color, String bg, int minSlots,
				int maxSlots, boolean cuisine, boolean isNew, int order) {
		}

		List.of(new SvcData(ServiceKey.CLEANING, "Home Cleaning", "Sweep, mop, dust your home", "ic_cleaning",
				"#F97316", "#FFF0E6", 2, 6, false, false, 1),
				new SvcData(ServiceKey.DISHWASHING, "Dishwashing", "Kitchen utensils cleaned", "ic_dishwash", "#0C447C",
						"#E6F1FB", 1, 3, false, false, 2),
				new SvcData(ServiceKey.MAID, "Maid Service", "Daily household tasks", "ic_maid", "#633806", "#FAEEDA",
						4, 8, false, false, 3),
				new SvcData(ServiceKey.COOKING, "Home Cooking", "Fresh meals at your kitchen", "ic_cooking", "#72243E",
						"#FBEAF0", 2, 6, true, true, 4),
				new SvcData(ServiceKey.BATHROOM_CLEANING, "Bathroom Cleaning", "Deep clean bathrooms", "ic_bathroom",
						"#1D4ED8", "#EFF6FF", 1, 4, false, true, 5),
				new SvcData(ServiceKey.KITCHEN_PREP, "Kitchen Prep", "Veg cutting, dough, masala", "ic_kitchen_prep",
						"#15803D", "#F0FDF4", 2, 4, true, true, 6),
				new SvcData(ServiceKey.DUSTING_WIPING, "Dusting & Wiping", "Dust-free home surfaces", "ic_dusting",
						"#7C3AED", "#F5F3FF", 1, 4, false, false, 7),
				new SvcData(ServiceKey.BIKE_WASH, "Bike Wash", "Professional wash at your spot", "ic_bike_wash",
						"#0F172A", "#F8FAFC", 1, 2, false, true, 8))
				.forEach(s -> {
					if (!serviceTypeRepo.existsByServiceKey(s.key())) {
						serviceTypeRepo.save(ServiceType.builder().serviceKey(s.key()).name(s.name())
								.description(s.desc()).iconName(s.icon()).colorCode(s.color()).bgColorCode(s.bg())
								.minSlots(s.minSlots()).maxSlots(s.maxSlots()).slotDurationMinutes(30)
								.requiresCuisineTag(s.cuisine()).requiresPhotos(true).isNew(s.isNew()).isActive(true)
								.displayOrder(s.order()).build());
						log.info("Service seeded | name={}", s.name());
					}
				});
	}
}