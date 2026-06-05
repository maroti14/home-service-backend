package com.homeservice.config;

import com.homeservice.domain.auth.entity.Role;
import com.homeservice.domain.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

	private final RoleRepository roleRepo;

	@Bean
	CommandLineRunner seedRoles() {
		return args -> {
			List<String> roles = List.of("ROLE_CUSTOMER", "ROLE_WORKER", "ROLE_ADMIN");

			for (String roleName : roles) {
				if (roleRepo.findByName(roleName).isEmpty()) {
					roleRepo.save(new Role(null, roleName));
					log.info("Role seeded: {}", roleName);
				}
			}
		};
	}
}
