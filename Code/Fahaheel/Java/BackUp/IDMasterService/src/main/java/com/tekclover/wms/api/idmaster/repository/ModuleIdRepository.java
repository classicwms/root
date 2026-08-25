package com.tekclover.wms.api.idmaster.repository;

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
public interface ModuleIdRepository extends JpaRepository<ModuleId,Long>, JpaSpecificationExecutor<ModuleId> {
	
	public List<ModuleId> findAll();
	public List<ModuleId>
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String moduleId, String languageId, Long deletionIndicator);

	@Query(value ="select top 1 tl.mod_id AS moduleId,tl.module_text AS description \n"+
			" from tblmoduleid tl \n" +
			"WHERE \n"+
			"tl.mod_id IN (:moduleId) and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN(:plantId) and \n"+
			"tl.wh_id IN (:warehouseId) and tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getModuleIdAndDescription(@Param(value="moduleId") String moduleId,
												   @Param(value = "languageId")String languageId,
												   @Param(value = "companyCodeId")String companyCodeId,
												   @Param(value = "plantId")String plantId,
												   @Param(value = "warehouseId")String warehouseId);

	Optional<ModuleId> findTop1ByModuleIdAndLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndDeletionIndicator(
			String moduleId, String languageId, String companyCodeId, String plantId, String warehouseId, Long deletionIndicator);

    ModuleId findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndLanguageIdAndMenuIdAndSubMenuIdAndDeletionIndicator(
			String companyCodeId, String plantId, String warehouseId, String moduleId, String languageId, Long menuId, Long subMenuId, Long deletionIndicator);

	Optional<ModuleId> findByModuleIdAndMenuIdAndSubMenuIdAndDeletionIndicator(
			String moduleId, Long menuId, Long subMenuId, Long deletionIndicator);

	@Query(value ="select max(mod_id)+1 \n"+
			" from tblmoduleid tl ",nativeQuery = true)
	public String getModuleId();

	Optional<ModuleId> findByMenuIdAndSubMenuIdAndDeletionIndicator(Long menuId, Long subMenuId, Long deletionIndicator);

    List<ModuleId> findByCompanyCodeIdAndDeletionIndicator(String companyCodeId, Long deletionIndicator);

	List<ModuleId> findByLanguageIdAndCompanyCodeIdAndPlantIdAndDeletionIndicator(String languageId, String companyCodeId, String plantId, Long deletionIndicator);

	List<ModuleId> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndDeletionIndicator(String languageId, String companyCodeId, String plantId, String warehouseId, Long deletionIndicator);

    Optional<ModuleId> findByMenuIdAndSubMenuIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndModuleIdAndDeletionIndicator(Long menuId, Long subMenuId, String companyCodeId, String plantId, String warehouseId, String languageId, String moduleId, Long deletionIndicator);
}