package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.aisleid.AisleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface AisleIdRepository extends JpaRepository<AisleId,Long>, JpaSpecificationExecutor<AisleId> {
	
	public List<AisleId> findAll();
	public Optional<AisleId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndAisleIdAndFloorIdAndStorageSectionIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String aisleId,Long floorId,String storageSectionId, String languageId, Long deletionIndicator);

	@Query(value ="select  tl.aisle_id AS aisleId,tl.aisle_text AS description\n"+
			" from tblaisleid tl \n" +
			"WHERE \n"+
			"tl.aisle_id IN (:aisleId)and tl.lang_id IN (:languageId) and tl.wh_id IN (:warehouseId) and tl.c_id IN (:companyCodeId) and tl.st_sec_id IN (:storageSectionId) and tl.fl_id IN (:floorId) and tl.plant_id IN (:plantId) and\n"+
			"tl.is_deleted=0 ",nativeQuery = true)
//ST_SEC_ID
	public IKeyValuePair getAisleIdAndDescription(@Param(value="aisleId") String aisleId,
												  @Param(value="languageId") String languageId,
												  @Param(value = "warehouseId")String warehouseId,
												  @Param(value = "companyCodeId")String companyCodeId,
												  @Param(value = "storageSectionId")String storageSectionId,
												  @Param(value = "floorId")String floorId,
												  @Param(value = "plantId")String plantId);

}