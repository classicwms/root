package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.storagetypeid.StorageTypeId;

@Repository
@Transactional
public interface StorageTypeIdRepository extends JpaRepository<StorageTypeId,Long>, JpaSpecificationExecutor<StorageTypeId> {
	
	public List<StorageTypeId> findAll();
	
	/**
	 * 
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param storageClassId
	 * @param storageTypeId
	 * @param languageId
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<StorageTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageClassIdAndStorageTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long storageClassId, Long storageTypeId, String languageId, Long deletionIndicator);
}