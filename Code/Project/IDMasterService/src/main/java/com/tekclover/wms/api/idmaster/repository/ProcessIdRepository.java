package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.processid.ProcessId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface ProcessIdRepository extends JpaRepository<ProcessId,Long>, JpaSpecificationExecutor<ProcessId> {
	
	public List<ProcessId> findAll();
	public Optional<ProcessId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProcessIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String processId, String languageId, Long deletionIndicator);
}