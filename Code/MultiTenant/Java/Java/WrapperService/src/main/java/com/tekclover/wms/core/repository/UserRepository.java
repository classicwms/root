package com.tekclover.wms.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.core.model.idmaster.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	public List<User> findAll();
	Optional<User> findByEmail(String email);
	Optional<User> findByUsername(String username);
}