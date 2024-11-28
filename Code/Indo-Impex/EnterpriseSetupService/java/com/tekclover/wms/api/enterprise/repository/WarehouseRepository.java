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

import com.tekclover.wms.api.enterprise.model.warehouse.Warehouse;

@Repository
@Transactional
public interface WarehouseRepository extends JpaRepository<Warehouse,Long>, JpaSpecificationExecutor<Warehouse> {

	public List<Warehouse> findAll();
	public Optional<Warehouse> findByWarehouseId(String warehouseId);
	
	public Optional<Warehouse> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndModeOfImplementationAndWarehouseTypeIdAndDeletionIndicator (
			String languageId, String companyId, String plantId, String warehouseId, String modeOfImplementation, 
			Long warehouseTypeId, Long deletionIndicator);

	@Query(value ="select  tl.wh_id AS warehouseId,tl.wh_text AS description\n"+
			" from tblwarehouseid tl \n" +
			"WHERE \n"+
			"tl.wh_id IN (:warehouseId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN(:plantId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)
	public IkeyValuePair getWarehouseIdAndDescription(@Param(value="warehouseId") String warehouseId,
													  @Param(value="languageId")String languageId,
													  @Param(value = "companyCodeId")String companyCodeId,
													  @Param(value = "plantId")String plantId);
	@Query(value ="select  tl.wh_typ_id AS warehouseTypeId,tl.wh_typ_text AS description\n"+
			" from tblwarehousetypeid tl \n" +
			"WHERE \n"+
			"tl.wh_typ_id IN (:warehouseTypeId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN(:plantId) and tl.wh_id IN (:warehouseId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IkeyValuePair getWarehouseTypeIdAndDescription(@Param(value="warehouseTypeId") String warehouseTypeId,
														  @Param(value="languageId")String languageId,
														  @Param(value = "companyCodeId")String companyCodeId,
														  @Param(value = "plantId")String plantId,
														  @Param(value = "warehouseId")String warehouseId);
}