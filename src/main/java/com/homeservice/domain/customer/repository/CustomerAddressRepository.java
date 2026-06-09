package com.homeservice.domain.customer.repository;

import com.homeservice.domain.customer.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {

	// get all non-deleted addresses
	// for a customer
	List<CustomerAddress> findByCustomerIdAndIsDeletedFalse(Long customerId);

	// get default address
	Optional<CustomerAddress> findByCustomerIdAndIsDefaultTrueAndIsDeletedFalse(Long customerId);

	// count addresses to limit per customer
	long countByCustomerIdAndIsDeletedFalse(Long customerId);

	// get specific address owned by customer
	Optional<CustomerAddress> findByIdAndCustomerIdAndIsDeletedFalse(Long id, Long customerId);

	// clear all defaults before setting new one
	@Modifying
	@Transactional
	@Query("""
			UPDATE CustomerAddress ca
			SET ca.isDefault = false
			WHERE ca.customer.id = :customerId
			AND ca.isDeleted = false
			""")
	void clearAllDefaults(Long customerId);
}
