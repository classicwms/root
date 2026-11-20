package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.cyclecounttypeid.CycleCountTypeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface CycleCountTypeIdRepository extends JpaRepository<CycleCountTypeId,Long>, JpaSpecificationExecutor<CycleCountTypeId> {
	
	public List<CycleCountTypeId> findAll();
	public Optional<CycleCountTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String cycleCountTypeId, String languageId, Long deletionIndicator);
}