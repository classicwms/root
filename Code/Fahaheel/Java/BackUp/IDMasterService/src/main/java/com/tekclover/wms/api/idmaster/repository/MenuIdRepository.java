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

import com.tekclover.wms.api.idmaster.model.menuid.MenuId;


@Repository
@Transactional
public interface MenuIdRepository extends JpaRepository<MenuId,Long>, JpaSpecificationExecutor<MenuId> {
	
	public List<MenuId> findAll();

	/**
	 * 
	 * @param companyCode
	 * @param plantId
	 * @param warehouseId
	 * @param menuId
	 * @param subMenuId
	 * @param authorizationObjectId
	 * @param languageId
	 * @param l
	 * @return
	 */
	public Optional<MenuId> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndMenuIdAndSubMenuIdAndAuthorizationObjectIdAndLanguageIdAndDeletionIndicator(
			String companyCode, String plantId, String warehouseId, Long menuId, Long subMenuId,
			Long authorizationObjectId, String languageId, long l);

	//public MenuId findByMenuId(Long menuId);
	public List<MenuId> findByMenuId(Long menuId);

	public List<MenuId> findBySubMenuId(Long subMenuId);

	//Description
	@Query(value ="select tc.c_id companyCodeId,tc.c_text companyDesc,\n"+
			"tp.plant_id plantId,tp.plant_text plantDesc,\n"+
			"tw.wh_id warehouseId,tw.wh_text warehouseDesc from \n"+
			"tblcompanyid tc\n"+
			"join tblplantid tp on tp.c_id = tc.c_id and tp.lang_id = tc.lang_id\n"+
			"join tblwarehouseid tw on tw.c_id = tc.c_id and tw.lang_id=tc.lang_id and tw.plant_id = tp.plant_id\n"+
			"where\n"+
			"tc.c_id IN (:companyCodeId) and \n"+
			"tc.lang_id IN (:languageId) and \n"+
			"tp.plant_id IN(:plantId) and \n"+
			"tw.wh_id IN (:warehouseId) and \n"+
			"tc.is_deleted=0 and \n"+
			"tp.is_deleted=0 and \n"+
			"tw.is_deleted=0",nativeQuery = true)
	public IKeyValuePair getDescription(@Param(value = "companyCodeId")String companyCodeId,
										@Param(value="languageId")String languageId,
										@Param(value = "plantId")String plantId,
										@Param(value="warehouseId") String warehouseId);
}