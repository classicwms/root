package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	public List<User> findAll();
	Optional<User> findByUsername(String username);
	Optional<User> findByIdAndDeletionIndicator(long id, long l);
}