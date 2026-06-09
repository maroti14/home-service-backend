package com.homeservice.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.homeservice.domain.auth.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Optional<User> findByMobile(String mobile);

	boolean existsByEmail(String email);

	boolean existsByMobile(String mobile);

	@Query("""
			SELECT CASE WHEN COUNT(u) > 0
			THEN true ELSE false END
			FROM User u
			WHERE u.email = :email
			AND u.id <> :excludeId
			""")
	boolean existsByEmailAndIdNot(String email, Long excludeId);
}