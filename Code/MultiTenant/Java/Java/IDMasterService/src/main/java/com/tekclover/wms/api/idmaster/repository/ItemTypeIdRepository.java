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

import com.tekclover.wms.api.idmaster.model.itemtypeid.ItemTypeId;


@Repository
@Transactional
public interface ItemTypeIdRepository extends JpaRepository<ItemTypeId,Long>, JpaSpecificationExecutor<ItemTypeId> {
	
	public List<ItemTypeId> findAll();
	public Optional<ItemTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long itemTypeId,String languageId,Long deletionIndicator);

	@Query(value ="select  tl.itm_type_id AS itemTypeId,tl.itm_typ AS description\n"+
			" from tblitemtypeid tl \n" +
			"WHERE \n"+
			"tl.itm_type_id IN (:itemTypeId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_Id IN (:plantId) and tl.wh_id IN (:warehouseId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getItemTypeIdAndDescription(@Param(value="itemTypeId") Long itemTypeId,
													 @Param(value = "languageId")String languageId,
													 @Param(value = "companyCodeId")String companyCodeId,
													 @Param(value = "plantId")String plantId,
													 @Param(value = "warehouseId")String warehouseId);

}