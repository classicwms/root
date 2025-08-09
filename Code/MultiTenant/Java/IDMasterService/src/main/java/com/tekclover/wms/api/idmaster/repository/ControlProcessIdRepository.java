package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.controlprocessid.ControlProcessId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface ControlProcessIdRepository extends JpaRepository<ControlProcessId,Long>, JpaSpecificationExecutor<ControlProcessId> {
	
	public List<ControlProcessId> findAll();
	public Optional<ControlProcessId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndControlProcessIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String controlProcessId, String languageId, Long deletionIndicator);
}