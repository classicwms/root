package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.idmaster.repository.Specification.VerticalSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.idmaster.model.vertical.Vertical;

@Repository
public interface VerticalRepository extends JpaRepository<Vertical, Long>,JpaSpecificationExecutor<Vertical> {

	List<Vertical> findAll();
	Optional<Vertical> findByVerticalIdAndLanguageId (Long verticalId,String languageId);

}