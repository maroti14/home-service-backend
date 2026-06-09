package com.homeservice.domain.customer.repository;


import com.homeservice.domain.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	Optional<Customer> findByEmail(String email);

	Optional<Customer> findByMobile(String mobile);

	boolean existsByEmail(String email);

	boolean existsByMobile(String mobile);
}
