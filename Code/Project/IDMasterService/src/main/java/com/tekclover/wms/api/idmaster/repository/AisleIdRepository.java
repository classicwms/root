package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.aisleid.AisleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface AisleIdRepository extends JpaRepository<AisleId,Long>, JpaSpecificationExecutor<AisleId> {
	
	public List<AisleId> findAll();
	public Optional<AisleId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndAisleIdAndFloorIdAndStorageSectionIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String aisleId,String floorId,String storageSectionId, String languageId, Long deletionIndicator);
}