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

import com.tekclover.wms.api.idmaster.model.storageclassid.StorageClassId;


@Repository
@Transactional
public interface StorageClassIdRepository extends JpaRepository<StorageClassId,Long>, JpaSpecificationExecutor<StorageClassId> {
	
	public List<StorageClassId> findAll();
	public Optional<StorageClassId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageClassIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long storageClassId, String languageId, Long deletionIndicator);

	@Query(value ="select  tl.st_cl_id AS storageClassId,tl.st_cl_text AS description\n"+
			" from tblstorageclassid tl \n" +
			"WHERE \n"+
			"tl.st_cl_id IN (:storageClassId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.wh_id IN (:warehouseId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getStorageClassIdAndDescription(@Param(value="storageClassId") String storageClassId,
														 @Param(value = "languageId")String languageId,
														 @Param(value = "companyCodeId")String companyCodeId,
														 @Param(value = "plantId")String plantId,
														 @Param(value = "warehouseId")String warehouseId);
}