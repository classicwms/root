package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.warehouse.Warehouse;
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
public interface WarehouseRepository extends JpaRepository<Warehouse,Long>, JpaSpecificationExecutor<Warehouse> {

	public List<Warehouse> findAll();
	public Optional<Warehouse> findByWarehouseId(String warehouseId);
	
	public Optional<Warehouse> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndDeletionIndicator (
			String languageId, String companyId, String plantId, String warehouseId, Long deletionIndicator);

	@Query(value ="select  tl.wh_id AS warehouseId,tl.wh_text AS description\n"+
			" from tblwarehouseid tl \n" +
			"WHERE \n"+
			"tl.wh_id IN (:warehouseId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN(:plantId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)
	public IKeyValuePair getWarehouseIdAndDescription(@Param(value="warehouseId") String warehouseId,
													  @Param(value="languageId")String languageId,
													  @Param(value = "companyCodeId")String companyCodeId,
													  @Param(value = "plantId")String plantId);
	@Query(value ="select  tl.wh_typ_id AS warehouseTypeId,tl.wh_typ_text AS description\n"+
			" from tblwarehousetypeid tl \n" +
			"WHERE \n"+
			"tl.wh_typ_id IN (:warehouseTypeId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN(:plantId) and tl.wh_id IN (:warehouseId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getWarehouseTypeIdAndDescription(@Param(value="warehouseTypeId") String warehouseTypeId,
														  @Param(value="languageId")String languageId,
														  @Param(value = "companyCodeId")String companyCodeId,
														  @Param(value = "plantId")String plantId,
														  @Param(value = "warehouseId")String warehouseId);
	public Optional<Warehouse> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndModeOfImplementationAndWarehouseTypeIdAndDeletionIndicator(
			String languageId, String companyId, String plantId, String warehouseId, String modeOfImplementation,
			Long warehouseTypeId, long l);

	Optional<Warehouse> findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(String companyCodeId, String plantId, String en, long l);

}