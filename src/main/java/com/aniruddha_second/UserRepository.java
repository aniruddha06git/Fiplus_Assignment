package com.aniruddha_second;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aniruddha_first.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	  Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByEmail1(String email);

}
