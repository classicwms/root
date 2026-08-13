package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.storagesection.StorageSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface StorageSectionRepository extends JpaRepository<StorageSection,Long>, JpaSpecificationExecutor<StorageSection> {

	public List<StorageSection> findAll();
	public Optional<StorageSection> findByStorageSectionId(String storageSectionId);

	public Optional<StorageSection> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, Long floorId,
			String storageSectionId, long deletionIndicator);

	@Query(value ="select  tl.st_sec_id AS storageSectionId,tl.st_sec AS description\n"+
			" from tblstoragesectionid tl \n" +
			"WHERE \n"+
			"tl.st_sec_id IN (:storageSectionId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.wh_id IN (:warehouseId) and tl.fl_id IN (:floorId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getStorageSectionIdAndDescription(@Param(value="storageSectionId") String storageSectionId,
														 @Param(value = "languageId")String languageId,
														 @Param(value = "floorId")Long floorId,
														 @Param(value = "companyCodeId")String companyCodeId,
														 @Param(value = "plantId")String plantId,
														 @Param(value = "warehouseId")String warehouseId);


}