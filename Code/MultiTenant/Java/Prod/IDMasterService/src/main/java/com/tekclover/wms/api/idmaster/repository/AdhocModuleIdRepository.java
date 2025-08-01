package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.adhocmoduleid.AdhocModuleId;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.moduleid.ModuleId;
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
public interface AdhocModuleIdRepository extends JpaRepository<AdhocModuleId,Long>, JpaSpecificationExecutor<AdhocModuleId> {
	
	public List<AdhocModuleId> findAll();
	public Optional<AdhocModuleId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndAdhocModuleIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String moduleId, String adhocModuleId,  String languageId, Long deletionIndicator);

	@Query(value ="select top 1 * \n"+
			" from tblmoduleid tl \n" +
			"WHERE \n"+
			"tl.mod_id IN (:moduleId) and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN(:plantId) and \n"+
			"tl.wh_id IN (:warehouseId) and tl.is_deleted=0 ",nativeQuery = true)

	public ModuleId getModuleIdAndDescription(@Param(value="moduleId") String moduleId,
											  @Param(value = "languageId")String languageId,
											  @Param(value = "companyCodeId")String companyCodeId,
											  @Param(value = "plantId")String plantId,
											  @Param(value = "warehouseId")String warehouseId);

	Optional<ModuleId> findTop1ByModuleIdAndLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndDeletionIndicator(
			String moduleId, String languageId, String companyCodeId, String plantId, String warehouseId, Long deletionIndicator);

}