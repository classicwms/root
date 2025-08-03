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

import com.tekclover.wms.api.idmaster.model.plantid.PlantId;


@Repository
@Transactional
public interface PlantIdRepository extends JpaRepository<PlantId,Long>, JpaSpecificationExecutor<PlantId> {
	
	public List<PlantId> findAll();
	public Optional<PlantId> 
		findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String languageId, Long deletionIndicator);


	@Query(value ="select  tl.plant_id AS plantId,tl.plant_text AS description\n"+
			" from tblplantid tl \n" +
			"WHERE \n"+
			"tl.plant_id IN (:plantId)and tl.lang_id IN (:languageId) and tl.c_id IN(:companyCodeId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getPlantIdAndDescription(@Param(value="plantId") String plantId,
												  @Param(value="languageId") String languageId,
												  @Param(value = "companyCodeId")String companyCodeId);

    List<PlantId> findByCompanyCodeIdAndLanguageIdAndDeletionIndicator(String companyCodeId, String languageId, Long deletionIndicator);
}
