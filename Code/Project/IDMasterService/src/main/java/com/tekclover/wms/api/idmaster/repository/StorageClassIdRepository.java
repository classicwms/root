package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.storageclassid.StorageClassId;


@Repository
@Transactional
public interface StorageClassIdRepository extends JpaRepository<StorageClassId,Long>, JpaSpecificationExecutor<StorageClassId> {
	
	public List<StorageClassId> findAll();
	public Optional<StorageClassId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageClassIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long storageClassId, String languageId, Long deletionIndicator);
}