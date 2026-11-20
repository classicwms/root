package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.roleaccess.RoleAccess;


@Repository
@Transactional
public interface RoleAccessRepository extends JpaRepository<RoleAccess,Long>,
		JpaSpecificationExecutor<RoleAccess> {
	
	public List<RoleAccess> findAll();
	
	/**
	 * 
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param userRoleId
	 * @param deletionIndicator
	 * @return
//	 */
//	public Optional<RoleAccess>
//		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndUserRoleIdAndMenuIdAndSubMenuIdAndDeletionIndicator(
//				String languageId, String companyCodeId, String plantId, String warehouseId, Long userRoleId, Long deletionIndicator);

	public List<RoleAccess> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRoleIdAndDeletionIndicator(
			String languageId, String companyCodeId, String plantId, String warehouseId, Long roleId, Long deletionIndicator);

	@Query(value = "select max(role_id)+1 roleId from tblroleaccess \n",nativeQuery = true)
	public Long getRoleId();

    List<RoleAccess> findByCompanyCodeIdAndDeletionIndicator(String companyCodeId, Long deletionIndicator);

	List<RoleAccess> findByLanguageIdAndCompanyCodeIdAndPlantIdAndDeletionIndicator(String languageId, String companyCodeId, String plantId, Long deletionIndicator);

	List<RoleAccess> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndDeletionIndicator(
			String languageId, String companyCodeId, String plantId, String warehouseId, Long deletionIndicator);


	@Query(value ="select top 1 tl.role_id AS roleId,tl.usr_role_nm AS roleDescription \n"+
			" from tblroleaccess tl \n" +
			"WHERE \n"+
			"tl.wh_id IN (:warehouseId) and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.role_id IN (:userRoleId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)
	public IKeyValuePair getRoleIdIdandDescription(@Param(value="warehouseId") String warehouseId,
													 @Param(value="languageId")String languageId,
													 @Param(value = "companyCodeId")String companyCodeId,
													 @Param(value = "plantId")String plantId,
													 @Param(value = "userRoleId")Long userRoleId);
}