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

import com.tekclover.wms.api.idmaster.model.levelid.LevelId;


@Repository
@Transactional
public interface LevelIdRepository extends JpaRepository<LevelId,Long>, JpaSpecificationExecutor<LevelId> {
	
	public List<LevelId> findAll();
	public Optional<LevelId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLevelIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long levelId, String languageId, Long deletionIndicator);
	@Query(value ="select  tl.level_id AS levelId,tl.level AS description\n"+
			" from tbllevelid tl \n" +
			"WHERE \n"+
			"tl.level_id IN (:levelId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.wh_id IN (:warehouseId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getLevelIdAndDescription(@Param(value="levelId") Long levelId,
												  @Param(value = "languageId")String languageId,
												  @Param(value = "companyCodeId")String companyCodeId,
												  @Param(value = "plantId")String plantId,
												  @Param(value = "warehouseId")String warehouseId);


}