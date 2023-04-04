package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.processsequenceid.ProcessSequenceId;


@Repository
@Transactional
public interface ProcessSequenceIdRepository extends JpaRepository<ProcessSequenceId,Long>, JpaSpecificationExecutor<ProcessSequenceId> {
	
	public List<ProcessSequenceId> findAll();
	public Optional<ProcessSequenceId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProcessIdAndSubLevelIdAndLanguageIdAndProcessDescriptionAndSubProcessDescriptionAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long processId, Long subLevelId, String languageId, String processDescription, String subProcessDescription, Long deletionIndicator);

    Optional<ProcessSequenceId> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProcessIdAndSubLevelIdAndLanguageIdAndDeletionIndicator(
			String companyCode, String plantId, String warehouseId, Long processId, Long subLevelId, String languageId, Long deletionIndicator);
}