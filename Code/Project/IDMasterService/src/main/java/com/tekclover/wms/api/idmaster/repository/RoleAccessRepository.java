package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.roleaccess.RoleAccess;


@Repository
@Transactional
public interface RoleAccessRepository extends JpaRepository<RoleAccess,Long>, JpaSpecificationExecutor<RoleAccess> {
	
	public List<RoleAccess> findAll();
	
	/**
	 * 
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param userRoleId
	 * @param menuId
	 * @param subMenuId
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<RoleAccess> 
		findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndUserRoleIdAndMenuIdAndSubMenuIdAndDeletionIndicator(
				String languageId, String companyCodeId, String plantId, String warehouseId, Long userRoleId, 
				Long menuId, Long subMenuId, Long deletionIndicator);

	public List<RoleAccess> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndUserRoleIdAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, Long userRoleId, Long deletionIndicator);

}