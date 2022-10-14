package com.tekclover.wms.api.masters.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.masters.model.storagebin.StorageBin;

@Repository
@Transactional
public interface StorageBinRepository extends JpaRepository<StorageBin,Long>, JpaSpecificationExecutor<StorageBin> {

	Optional<StorageBin> findByStorageBin(String storageBin);
	
	public StorageBin findByWarehouseIdAndBinClassIdAndDeletionIndicator(String warehouseId, Long binClassId, Long deletionIndicator);

	public List<StorageBin> findByStorageBinInAndStorageSectionIdInAndPutawayBlockAndPickingBlockAndDeletionIndicatorOrderByStorageBinDesc(
			List<String> storageBin, List<String> storageSectionId, Integer putawayBlock, Integer pickingBlock, Long deletionIndicator);
	
	public List<StorageBin> findByWarehouseIdAndStatusIdAndDeletionIndicator(String warehouseId, Long statusId, Long deletionIndicator);
	
	public List<StorageBin> findByWarehouseIdAndStatusIdNotAndDeletionIndicator(String warehouseId, Long statusId, Long deletionIndicator);

	public List<StorageBin> findByStorageBinInAndStorageSectionIdInAndDeletionIndicatorOrderByStorageBinDesc(
			List<String> storageBin, List<String> storageSectionIds, Long deletionIndicator);
	
	public List<StorageBin> findByStorageSectionIdIn(List<String> storageSectionId);

	public StorageBin findByWarehouseIdAndStorageBinAndDeletionIndicator(String warehouseId, String storageBin, long l);
}


