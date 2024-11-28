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

import com.tekclover.wms.api.idmaster.model.storagetypeid.StorageTypeId;

@Repository
@Transactional
public interface StorageTypeIdRepository extends JpaRepository<StorageTypeId,Long>, JpaSpecificationExecutor<StorageTypeId> {
	
	public List<StorageTypeId> findAll();
	
	/**
	 * 
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param storageClassId
	 * @param storageTypeId
	 * @param languageId
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<StorageTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageClassIdAndStorageTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long storageClassId, Long storageTypeId, String languageId, Long deletionIndicator);

	@Query(value ="select  tl.st_typ_id AS storageTypeId,tl.st_typ_text AS description\n"+
			" from tblstoragetypeid tl \n" +
			"WHERE \n"+
			"tl.st_typ_id IN (:storageTypeId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.st_cl_id IN (:storageClassId) and tl.wh_id IN (:warehouseId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getStorageTypeIdAndDescription(@Param(value="storageTypeId") String storageTypeId,
														@Param(value = "languageId")String languageId,
														@Param(value="companyCodeId")String companyCodeId,
														@Param(value = "plantId")String plantId,
														@Param(value = "storageClassId")String storageClassId,
														@Param(value = "warehouseId")String warehouseId);

}