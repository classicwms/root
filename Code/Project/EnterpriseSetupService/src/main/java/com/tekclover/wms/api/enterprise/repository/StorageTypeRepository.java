package com.tekclover.wms.api.enterprise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.enterprise.model.storagetype.StorageType;

@Repository
@Transactional
public interface StorageTypeRepository extends JpaRepository<StorageType,Long>, JpaSpecificationExecutor<StorageType> {

	public List<StorageType> findAll();
	public Optional<StorageType> findByStorageTypeId(Long storageTypeId);
	public Optional<StorageType> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageClassIdAndStorageTypeIdAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, Long storageClassId,
			Long storageTypeId, Long l);


}