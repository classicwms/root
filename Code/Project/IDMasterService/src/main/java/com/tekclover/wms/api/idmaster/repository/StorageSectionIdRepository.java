package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.storagesectionid.StorageSectionId;


@Repository
@Transactional
public interface StorageSectionIdRepository extends JpaRepository<StorageSectionId,Long>, JpaSpecificationExecutor<StorageSectionId> {
	
	public List<StorageSectionId> findAll();
	public Optional<StorageSectionId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndStorageSectionAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long floorId, String storageSectionId, String storageSection, String languageId, Long deletionIndicator);
}