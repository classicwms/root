package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.idmaster.model.vertical.Vertical;

@Repository
public interface VerticalRepository extends JpaRepository<Vertical, Long>{

	List<Vertical> findAll();
	Optional<Vertical> findByVerticalId (Long verticalId);
}