package com.tekclover.wms.core.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.tekclover.wms.core.model.user.NewUser;

public interface RegisterRepository extends CrudRepository<NewUser, Long> {

	Optional<NewUser> findByRegisterId(String id);
	Optional<NewUser> findByClientName(String clientName);
}