package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.adhocmoduleid.AdhocModuleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface AdhocModuleIdRepository extends JpaRepository<AdhocModuleId,Long>, JpaSpecificationExecutor<AdhocModuleId> {
	
	public List<AdhocModuleId> findAll();
	public Optional<AdhocModuleId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndAdhocModuleIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String moduleId, String adhocModuleId,  String languageId, Long deletionIndicator);
}