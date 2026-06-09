package com.homeservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.homeservice.common.constant.ApiConstants;
import com.homeservice.security.JwtAuthenticationEntryPoint;
import com.homeservice.security.JwtAuthenticationFilter;
import com.homeservice.security.UserDetailsServiceImpl;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationEntryPoint entryPoint;
	private final JwtAuthenticationFilter jwtFilter;
	private final UserDetailsServiceImpl userDetailsService;

	@Value("${cors.allowed-origins}")
	private String corsAllowedOrigins;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		return http.csrf(csrf -> csrf.disable()).exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.authorizeHttpRequests(auth -> auth.requestMatchers(ApiConstants.SWAGGER_WHITELIST).permitAll()
						.requestMatchers(HttpMethod.POST, ApiConstants.CUSTOMER_REGISTER, ApiConstants.WORKER_REGISTER,
								ApiConstants.ADMIN_REGISTER, ApiConstants.LOGIN, ApiConstants.LOGIN_SEND_OTP,
								ApiConstants.LOGIN_VERIFY_OTP, ApiConstants.VERIFY_MOBILE, ApiConstants.RESEND_OTP,
								ApiConstants.REFRESH_TOKEN, ApiConstants.LOGOUT)
						.permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/cities", "/api/v1/cities/**", "/api/v1/services",
								"/api/v1/services/**", "/api/v1/pricing/**","/api/v1/slots/**")
						.permitAll().requestMatchers(HttpMethod.OPTIONS, "/**").permitAll().anyRequest()
						.authenticated())
				.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration cfg = new CorsConfiguration();
		cfg.setAllowedOrigins(Arrays.asList(corsAllowedOrigins.split(",")));
		cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		cfg.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
		cfg.setExposedHeaders(List.of("Authorization"));
		cfg.setAllowCredentials(true);
		cfg.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", cfg);
		return source;
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}