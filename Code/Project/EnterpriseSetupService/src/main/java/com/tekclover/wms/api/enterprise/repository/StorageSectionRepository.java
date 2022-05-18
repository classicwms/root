package com.tekclover.wms.api.enterprise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.enterprise.model.storagesection.StorageSection;

@Repository
@Transactional
public interface StorageSectionRepository extends JpaRepository<StorageSection,Long>, JpaSpecificationExecutor<StorageSection> {

	public List<StorageSection> findAll();
	public Optional<StorageSection> findByStorageSectionId(String storageSectionId);

	public Optional<StorageSection> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, Long floorId,
			String storageSectionId, long deletionIndicator);

}