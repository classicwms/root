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

import com.tekclover.wms.api.idmaster.model.itemgroupid.ItemGroupId;


@Repository
@Transactional
public interface ItemGroupIdRepository extends JpaRepository<ItemGroupId,Long>, JpaSpecificationExecutor<ItemGroupId> {
	
	public List<ItemGroupId> findAll();
	public Optional<ItemGroupId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemTypeIdAndItemGroupIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long itemTypeId, Long itemGroupId,String languageId,Long deletionIndicator);

	@Query(value ="select  tl.itm_grp_id AS itemGroupId,tl.imt_grp AS description\n"+
			" from tblitemgroupid tl \n" +
			"WHERE \n"+
			"tl.itm_grp_id IN (:itemGroupId)and tl.lang_id IN (:languageId) and tl.itm_type_id IN (:itemTypeId) and tl.c_id IN (:companyCodeId) and tl.wh_id IN (:warehouseId) and tl.plant_id IN (:plantId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getItemGroupIdAndDescription(@Param(value="itemGroupId") String itemGroupId,
													  @Param(value = "languageId")String languageId,
													  @Param(value = "itemTypeId")String itemTypeId,
													  @Param(value = "companyCodeId")String companyCodeId,
													  @Param(value = "warehouseId")String warehouseId,
													  @Param(value = "plantId")String plantId);


}