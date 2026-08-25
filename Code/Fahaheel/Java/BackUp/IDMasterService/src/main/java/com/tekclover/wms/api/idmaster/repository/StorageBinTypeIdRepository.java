package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.storagebintypeid.StorageBinTypeId;


@Repository
@Transactional
public interface StorageBinTypeIdRepository extends JpaRepository<StorageBinTypeId,Long>, JpaSpecificationExecutor<StorageBinTypeId> {
	
	public List<StorageBinTypeId> findAll();
	public Optional<StorageBinTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageClassIdAndStorageTypeIdAndStorageBinTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long storageClassId, Long storageTypeId, Long storageBinTypeId, String languageId, Long deletionIndicator);
}