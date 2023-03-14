package com.tekclover.wms.api.masters.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.masters.model.impl.ItemListImpl;
import com.tekclover.wms.api.masters.model.impl.StorageBinListImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

	public List<StorageBin> findByWarehouseIdAndStorageBinInAndStorageSectionIdInAndPutawayBlockAndPickingBlockAndDeletionIndicatorOrderByStorageBinDesc(
			String warehouseId, List<String> storageBin, List<String> storageSectionIds, int i, int j, long l);

	public List<StorageBin> findByWarehouseIdAndStorageSectionIdIn(String warehouseId, List<String> stSectionIds);

	@Query(value = "select TOP 50 st_bin as storageBin from tblstoragebin\n" +
					"where ( st_bin like :searchText1% or st_bin like %:searchText2 ) \n" +
					"group by st_bin ", nativeQuery = true)
	List<StorageBinListImpl> getStorageBinListBySearch(@Param("searchText1") String searchText1,
													   @Param("searchText2") String searchText2);
}


