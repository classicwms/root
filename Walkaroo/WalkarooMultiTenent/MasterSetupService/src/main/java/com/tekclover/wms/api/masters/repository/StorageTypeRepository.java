package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.storagetype.StorageType;
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
public interface StorageTypeRepository extends JpaRepository<StorageType,Long>, JpaSpecificationExecutor<StorageType> {

	public List<StorageType> findAll();
	public Optional<StorageType> findByStorageTypeId(Long storageTypeId);
	public Optional<StorageType> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageClassIdAndStorageTypeIdAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, Long storageClassId,
			Long storageTypeId, Long l);

	@Query(value ="select  tl.st_typ_id AS storageTypeId,tl.st_typ_text AS description\n"+
			" from tblstoragetypeid tl \n" +
			"WHERE \n"+
			"tl.st_typ_id IN (:storageTypeId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.st_cl_id IN (:storageClassId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getStorageTypeIdAndDescription(@Param(value="storageTypeId") Long storageTypeId,
														@Param(value = "languageId")String languageId,
														@Param(value="companyCodeId")String companyCodeId,
														@Param(value = "plantId")String plantId,
														@Param(value = "storageClassId")Long storageClassId);
}