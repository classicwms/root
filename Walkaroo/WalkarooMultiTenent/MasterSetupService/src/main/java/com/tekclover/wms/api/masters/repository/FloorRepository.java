package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.floor.Floor;
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
public interface FloorRepository extends JpaRepository<Floor,Long>, JpaSpecificationExecutor<Floor> {
	public List<Floor> findAll();
	
	public Optional<Floor> findByFloorId(Long floorId);
	
	public Optional<Floor> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndFloorIdAndDeletionIndicator (
	String languageId, String companyId, String plantId, String warehouseId, Long floorId, Long deletionIndicator);

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
}