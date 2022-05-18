package com.tekclover.wms.api.masters.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.masters.model.imstrategies.ImStrategies;

@Repository
@Transactional
public interface ImStrategiesRepository extends JpaRepository<ImStrategies,Long>, JpaSpecificationExecutor<ImStrategies> {

	Optional<ImStrategies> findByStrategeyTypeId(String strategeyTypeId);
}