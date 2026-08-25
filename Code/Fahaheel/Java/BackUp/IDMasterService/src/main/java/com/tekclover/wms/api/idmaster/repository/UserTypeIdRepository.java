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

import com.tekclover.wms.api.idmaster.model.usertypeid.UserTypeId;

@Repository
@Transactional
public interface UserTypeIdRepository extends JpaRepository<UserTypeId,Long>, JpaSpecificationExecutor<UserTypeId> {
	
	public List<UserTypeId> findAll();
	public Optional<UserTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndUserTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long userTypeId, String languageId, Long deletionIndicator);

	@Query(value ="select tl.usr_typ_id AS userTypeId,tl.usr_typ_text AS userTypeDescription \n"+
			" from tblusertypeid tl \n" +
			"WHERE \n"+
			"tl.wh_id IN (:warehouseId) and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.usr_typ_id IN (:userTypeId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)
	public IKeyValuePair getUserTypeIdandDescription(@Param(value="warehouseId") String warehouseId,
													  @Param(value="languageId")String languageId,
													  @Param(value = "companyCodeId")String companyCodeId,
													  @Param(value = "plantId")String plantId,
													  @Param(value = "userTypeId")Long userTypeId);
}