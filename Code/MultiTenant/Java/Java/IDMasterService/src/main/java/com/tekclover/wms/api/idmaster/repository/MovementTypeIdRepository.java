package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.movementtypeid.MovementTypeId;
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
public interface MovementTypeIdRepository extends JpaRepository<MovementTypeId,Long>, JpaSpecificationExecutor<MovementTypeId> {
	
	public List<MovementTypeId> findAll();
	public Optional<MovementTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndMovementTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String movementTypeId, String languageId, Long deletionIndicator);

		@Query(value ="select  tl.mvt_typ_id AS movementTypeId,tl.mvt_typ_text AS description\n"+
			" from tblmovementtypeid tl \n" +
			"WHERE \n"+
			"tl.mvt_typ_id IN (:movementTypeId)and tl.lang_id IN (:languageId) and tl.wh_id IN (:warehouseId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getMovementTypeIdAndDescription(@Param(value="movementTypeId") String movementTypeId,
														 @Param(value = "languageId")String languageId,
														 @Param(value = "warehouseId")String warehouseId,
														 @Param(value = "companyCodeId")String companyCodeId,
														 @Param(value = "plantId")String plantId);

}