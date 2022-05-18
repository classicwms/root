package com.tekclover.wms.api.enterprise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.enterprise.model.storageclass.StorageClass;

@Repository
@Transactional
public interface StorageClassRepository extends JpaRepository<StorageClass,Long>, JpaSpecificationExecutor<StorageClass> {

	public List<StorageClass> findAll();
	public Optional<StorageClass> findByStorageClassId(Long storageClassId);
	public Optional<StorageClass> 
		findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageClassIdAndDeletionIndicator (
				String languageId, String companyId, String plantId, String warehouseId, Long storageClassId, 
					Long deletionIndicator);
}