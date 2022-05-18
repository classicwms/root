package com.tekclover.wms.api.enterprise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.enterprise.model.strategies.Strategies;

@Repository
@Transactional
public interface StrategiesRepository extends JpaRepository<Strategies,Long>, JpaSpecificationExecutor<Strategies> {

	public List<Strategies> findAll();
	public Optional<Strategies> findByStrategyTypeId(Long strategyTypeId);
	
	public Optional<Strategies> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStrategyTypeIdAndSequenceIndicatorAndStrategyNoAndPriorityAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, Long strategyTypeId,
			Long sequenceIndicator, String strategyNo, Long priority, Long deletionIndicator);

}