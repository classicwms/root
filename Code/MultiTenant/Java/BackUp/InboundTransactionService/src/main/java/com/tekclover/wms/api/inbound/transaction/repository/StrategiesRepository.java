package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.dto.Strategies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface StrategiesRepository extends JpaRepository<Strategies,Long>, JpaSpecificationExecutor<Strategies> {

	public Optional<Strategies> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStrategyTypeIdAndSequenceIndicatorAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, Long strategyTypeId, Long sequenceIndicator, Long deletionIndicator);


}