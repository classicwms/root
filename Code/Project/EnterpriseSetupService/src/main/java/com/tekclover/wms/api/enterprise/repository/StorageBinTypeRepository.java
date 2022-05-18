package com.tekclover.wms.api.enterprise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.enterprise.model.storagebintype.StorageBinType;

@Repository
@Transactional
public interface StorageBinTypeRepository extends JpaRepository<StorageBinType,Long>, JpaSpecificationExecutor<StorageBinType> {

	public List<StorageBinType> findAll();
	public Optional<StorageBinType> findByStorageBinTypeId(Long storageBinTypeId);
	public Optional<StorageBinType> 
		findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageTypeIdAndStorageBinTypeIdAndDeletionIndicator (
			String languageId, String companyId, String plantId, String warehouseId, Long storageTypeId, 
				Long storageBinTypeId, Long deletionIndicator);
}