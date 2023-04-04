package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.moduleid.ModuleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface ModuleIdRepository extends JpaRepository<ModuleId,Long>, JpaSpecificationExecutor<ModuleId> {
	
	public List<ModuleId> findAll();
	public Optional<ModuleId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String moduleId, String languageId, Long deletionIndicator);
}