package com.tekclover.wms.api.enterprise.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.enterprise.model.storagebintype.StorageBinType;

@Repository
@Transactional
public interface StorageBinTypeRepository extends JpaRepository<StorageBinType,Long>, JpaSpecificationExecutor<StorageBinType> {

	public List<StorageBinType> findAll();
	public Optional<StorageBinType> findByStorageBinTypeId(Long storageBinTypeId);
	public Optional<StorageBinType> 
		findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageTypeIdAndStorageClassIdAndStorageBinTypeIdAndDeletionIndicator (
			String languageId, String companyId, String plantId, String warehouseId, Long storageTypeId,Long storageClassId,
				Long storageBinTypeId, Long deletionIndicator);

	@Query(value ="select  tl.st_bin_typ_id AS storageBinTypeId,tl.st_bin_typ_text AS description\n"+
			" from tblstoragebintypeid tl \n" +
			"WHERE \n"+
			"tl.st_bin_typ_id IN (:storageBinTypeId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.st_cl_id IN (:storageClassId) and tl.st_typ_id IN (:storageTypeId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IkeyValuePair getStorageBinTypeIdAndDescription(@Param(value="storageBinTypeId") Long storageBinTypeId,
														@Param(value = "languageId")String languageId,
														@Param(value="companyCodeId")String companyCodeId,
														@Param(value = "plantId")String plantId,
														@Param(value = "storageClassId")Long storageClassId,
														@Param(value = "storageTypeId")Long storageTypeId);

}