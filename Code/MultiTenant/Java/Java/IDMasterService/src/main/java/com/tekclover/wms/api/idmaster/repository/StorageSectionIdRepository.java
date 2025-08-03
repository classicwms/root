package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.storagesectionid.StorageSectionId;


@Repository
@Transactional
public interface StorageSectionIdRepository extends JpaRepository<StorageSectionId,Long>, JpaSpecificationExecutor<StorageSectionId> {
	
	public List<StorageSectionId> findAll();
	public Optional<StorageSectionId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long floorId, String storageSectionId, String languageId, Long deletionIndicator);


	@Query(value ="select  tl.st_sec_id AS storageSectionId,tl.st_sec AS description\n"+
			" from tblstoragesectionid tl \n" +
			"WHERE \n"+
			"tl.st_sec_id IN (:storageSectionId) and tl.lang_id IN (:languageId) and tl.wh_id IN (:warehouseId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.fl_id IN (:floorId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getStorageSectionIdAndDescription(@Param(value="storageSectionId") String storageSectionId,
														   @Param(value ="languageId") String languageId,
														   @Param(value="warehouseId") String warehouseId,
														   @Param(value ="companyCodeId")String companyCodeId,
														   @Param(value = "plantId")String plantId,
														   @Param(value = "floorId")String floorId);

//	IKeyValuePair getStorageSectionIdAndDescription(String storageSectionId, String languageId, String warehouseId, String companyCodeId, Long floorId);
}