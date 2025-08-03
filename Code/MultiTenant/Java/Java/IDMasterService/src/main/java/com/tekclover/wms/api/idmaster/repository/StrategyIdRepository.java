package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.strategyid.StrategyId;


@Repository
@Transactional
public interface StrategyIdRepository extends JpaRepository<StrategyId,Long>, JpaSpecificationExecutor<StrategyId> {
	
	public List<StrategyId> findAll();
	public Optional<StrategyId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStrategyTypeIdAndStrategyNoAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long strategyTypeId, String strategyNo, String languageId, Long deletionIndicator);
}