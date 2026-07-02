package com.tekclover.wms.api.enterprise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.enterprise.model.user.User;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {

	public List<User> findAll();
	public Optional<User> findByEmail(String email);
	public Optional<User> findByUsername(String username);
}