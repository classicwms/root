package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.idmaster.model.state.State;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {

	List<State> findAll();
	Optional<State> findByStateId (String stateId);
}