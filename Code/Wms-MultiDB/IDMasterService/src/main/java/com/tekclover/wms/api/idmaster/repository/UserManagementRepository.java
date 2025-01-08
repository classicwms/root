package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.user.UserManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserManagementRepository extends JpaRepository<UserManagement, Long>, JpaSpecificationExecutor<UserManagement> {

	public Optional<UserManagement> findByUserId(String userId);
	
	public List<UserManagement> findByUserIdAndDeletionIndicator(String userId, Long deletionIndicator);

	public UserManagement findByWarehouseIdAndUserIdAndDeletionIndicator(String warehouseId, String userId,
			Long deletionIndicator);

	public UserManagement findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndUserIdAndUserRoleIdAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId,
			String userId, Long userRoleId,Long deletionIndicator);

	@Query(value = "select * \n" +
			" from tblusermanagement \n" +
			"WHERE \n" +
			"(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
			"(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
			"(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
			"(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
			"(COALESCE(:userId, null) IS NULL OR (USR_ID IN (:userId))) and \n" +
			"is_deleted=0 ", nativeQuery = true)
	public List<UserManagement> getUserId(@Param(value = "userId") String userId,
										  @Param(value = "companyCodeId") String companyCodeId,
										  @Param(value = "plantId") String plantId,
										  @Param(value = "languageId") String languageId,
										  @Param(value = "warehouseId") String warehouseId);
}