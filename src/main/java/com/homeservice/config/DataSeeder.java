package com.homeservice.config;

import com.homeservice.common.enums.ServiceKey;
import com.homeservice.domain.auth.entity.Role;
import com.homeservice.domain.auth.repository.RoleRepository;
import com.homeservice.domain.servicetype.entity.ServiceType;
import com.homeservice.domain.servicetype.repository.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

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
		List<String> roles = List.of("ROLE_CUSTOMER", "ROLE_WORKER", "ROLE_ADMIN");
		for (String roleName : roles) {
			if (roleRepo.findByName(roleName).isEmpty()) {
				roleRepo.save(new Role(null, roleName));
				log.info("Role seeded: {}", roleName);
			}
		}
	}

	private void seedServiceTypes() {

		// each record:
		// serviceKey, name, color, bgColor,
		// minSlots, maxSlots, needsCuisine,
		// isNew, displayOrder
		List<Object[]> services = List.of(
				new Object[] { ServiceKey.CLEANING, "Home Cleaning", "Sweep, mop, dust your entire home", "ic_cleaning",
						"#F97316", "#FFF0E6", 2, 6, false, false, 1 },
				new Object[] { ServiceKey.DISHWASHING, "Dishwashing", "Kitchen utensils and vessels cleaned",
						"ic_dishwash", "#0C447C", "#E6F1FB", 1, 3, false, false, 2 },
				new Object[] { ServiceKey.MAID, "Maid Service", "Daily household tasks done right", "ic_maid",
						"#633806", "#FAEEDA", 4, 8, false, false, 3 },
				new Object[] { ServiceKey.COOKING, "Home Cooking", "Fresh home-cooked meals at your kitchen",
						"ic_cooking", "#72243E", "#FBEAF0", 2, 6, true, true, 4 },
				new Object[] { ServiceKey.BATHROOM_CLEANING, "Bathroom Cleaning",
						"Deep clean your bathroom and toilets", "ic_bathroom", "#1D4ED8", "#EFF6FF", 1, 4, false, true,
						5 },
				new Object[] { ServiceKey.KITCHEN_PREP, "Kitchen Prep", "Vegetable cutting, dough, masala prep",
						"ic_kitchen_prep", "#15803D", "#F0FDF4", 2, 4, true, true, 6 },
				new Object[] { ServiceKey.DUSTING_WIPING, "Dusting & Wiping", "Dust-free surfaces and ceiling fans",
						"ic_dusting", "#7C3AED", "#F5F3FF", 1, 4, false, false, 7 },
				new Object[] { ServiceKey.BIKE_WASH, "Bike Wash", "Professional wash at your parking spot",
						"ic_bike_wash", "#0F172A", "#F8FAFC", 1, 2, false, true, 8 });

		for (Object[] s : services) {
			ServiceKey key = (ServiceKey) s[0];

			if (!serviceTypeRepo.existsByServiceKey(key)) {

				ServiceType service = ServiceType.builder().serviceKey(key).name((String) s[1])
						.description((String) s[2]).iconName((String) s[3]).colorCode((String) s[4])
						.bgColorCode((String) s[5]).minSlots((Integer) s[6]).maxSlots((Integer) s[7])
						.slotDurationMinutes(30).requiresCuisineTag((Boolean) s[8]).requiresPhotos(true)
						.isNew((Boolean) s[9]).isActive(true).displayOrder((Integer) s[10]).build();

				serviceTypeRepo.save(service);
				log.info("Service seeded: {}", service.getName());
			}
		}
	}
}