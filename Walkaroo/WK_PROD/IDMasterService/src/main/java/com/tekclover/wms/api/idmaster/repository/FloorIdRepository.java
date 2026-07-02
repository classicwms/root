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

import com.tekclover.wms.api.idmaster.model.floorid.FloorId;


@Repository
@Transactional
public interface FloorIdRepository extends JpaRepository<FloorId,Long>, JpaSpecificationExecutor<FloorId> {
	
	public List<FloorId> findAll();
	public Optional<FloorId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndFloorIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long floorId, String languageId, Long deletionIndicator);

	@Query(value ="select  tl.fl_id AS floorId,tl.fl_text AS description\n"+
			" from tblfloorid tl \n" +
			"WHERE \n"+
			"tl.fl_id IN (:floorId) and tl.lang_id IN (:languageId) and tl.wh_id IN (:warehouseId) and tl.plant_id IN (:plantId) and tl.c_id IN (:companyCodeId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getFloorIdAndDescription(@Param(value="floorId") String floorId,
												  @Param(value = "languageId")String languageId,
												  @Param(value = "warehouseId")String warehouseId,
												  @Param(value = "plantId")String plantId,
												  @Param(value = "companyCodeId")String companyCodeId);

	//Description
	@Query(value = "select CONCAT(tl.fl_id,'-',tl.fl_text) \n" +
			"from tblfloorid tl\n" +
			"where\n" +
			"tl.fl_id IN (:floorId) and tl.lang_id IN (:languageId) and tl.wh_id IN (:warehouseId) and tl.plant_id IN (:plantId) and tl.c_id IN (:companyCodeId) and \n" +
			"tl.is_deleted=0 ", nativeQuery = true)
	public String getDescription(@Param("companyCodeId") String companyCodeId,
								 @Param("plantId") String plantId,
								 @Param("languageId") String languageId,
								 @Param("warehouseId") String warehouseId,
								 @Param("floorId") Long floorId);


}