package com.tekclover.wms.api.masters.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.masters.model.impl.StorageBinListImpl;
import com.tekclover.wms.api.masters.model.storagebin.StorageBin;
import com.tekclover.wms.api.masters.repository.fragments.StreamableJpaSpecificationRepository;

@Repository
@Transactional
public interface StorageBinRepository extends JpaRepository<StorageBin,Long>,
												JpaSpecificationExecutor<StorageBin>,
												StreamableJpaSpecificationRepository<StorageBin> {

	Optional<StorageBin> findByStorageBinAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(String storageBin,String companyCodeId,String plantId,String warehouseId,String languageId,Long deletionIndicator);
	
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

	@Query(	value = "select TOP 50 st_bin as storageBin from tblstoragebin \n" +
			"where ( st_bin like :searchText1% or st_bin like %:searchText2 \n" +
			"or st_bin like %:searchText3% ) and \n" +
			"c_id in (:companyCodeId) and plant_id in (:plantId) and lang_id in (:languageId) and wh_id in (:warehouseId)\n" +
			"group by st_bin ",	nativeQuery = true)
	List<StorageBinListImpl> getStorageBinListBySearchNew(	@Param("searchText1") String searchText1,
															  @Param("searchText2") String searchText2,
															  @Param("searchText3") String searchText3,
															  @Param("companyCodeId") String companyCodeId,
															  @Param("plantId") String plantId,
															  @Param("languageId") String languageId,
															  @Param("warehouseId") String warehouseId  );

	@Query(	value = "select TOP 50 st_bin as storageBin from tblstoragebin \n" +
			"where ( st_bin like :searchText1% or st_bin like %:searchText2 \n" +
			"or st_bin like %:searchText3% ) and \n" +
			"(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
			"(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
			"(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
			"(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) \n" +
			"group by st_bin ",	nativeQuery = true)
	List<StorageBinListImpl> getStorageBinListBySearchV2(@Param("searchText1") String searchText1,
														 @Param("searchText2") String searchText2,
														 @Param("searchText3") String searchText3,
														 @Param("companyCodeId") String companyCodeId,
														 @Param("plantId") String plantId,
														 @Param("languageId") String languageId,
														 @Param("warehouseId") String warehouseId);

	// V3 
	Optional<StorageBin> findTopByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicatorAndReferenceField1ContainingAndReferenceField2(
			String companyCodeId, String plantId, String warehouseId, String languageId, long l, String referenceField1,
			String referenceField2);
}


