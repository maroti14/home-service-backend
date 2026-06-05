package com.homeservice.config;



import com.homeservice.security.JwtAuthenticationEntryPoint;
import com.homeservice.security.JwtOnceperRequestFilter;
import com.homeservice.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication
        .AuthenticationManager;
import org.springframework.security.authentication.dao
        .DaoAuthenticationProvider;
import org.springframework.security.config.annotation
        .authentication.configuration
        .AuthenticationConfiguration;
import org.springframework.security.config.annotation
        .method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation
        .web.builders.HttpSecurity;
import org.springframework.security.config.annotation
        .web.configuration.EnableWebSecurity;
import org.springframework.security.config.http
        .SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt
        .BCryptPasswordEncoder;
import org.springframework.security.crypto.password
        .PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication
        .UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint entryPoint;
    private final JwtOnceperRequestFilter jwtFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Value("${cors.allowedOrigins}")
    private String corsAllowedOrigins;

    // ── Public endpoints (no token needed) ───────────────
    private static final String[] PUBLIC_URLS = {
        "/api/v1/auth/**",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/actuator/health"
    };

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        return http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex ->
                ex.authenticationEntryPoint(entryPoint))
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))
            .authenticationProvider(
                authenticationProvider())
            .addFilterBefore(jwtFilter,
                UsernamePasswordAuthenticationFilter.class)
            .cors(cors ->
                cors.configurationSource(
                    corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_URLS)
                    .permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                // role-based access examples
                .requestMatchers("/api/v1/admin/**")
                    .hasRole("ADMIN")
                .requestMatchers("/api/v1/worker/**")
                    .hasAnyRole("WORKER", "ADMIN")
                .requestMatchers("/api/v1/customer/**")
                    .hasAnyRole("CUSTOMER", "ADMIN")
                .anyRequest().authenticated()
            )
            .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(
            Arrays.asList(
                corsAllowedOrigins.split(",")));
        cfg.setAllowedMethods(
            List.of("GET","POST","PUT",
                    "DELETE","PATCH","OPTIONS"));
        cfg.setAllowedHeaders(
            List.of("Authorization",
                    "Cache-Control",
                    "Content-Type"));
        cfg.setExposedHeaders(
            List.of("Authorization"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
            new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}