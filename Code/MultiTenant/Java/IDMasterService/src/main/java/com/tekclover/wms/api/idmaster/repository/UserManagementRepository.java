package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.idmaster.model.user.UserManagement;

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

	@Query(value = "select * \n" +
			" from tblusermanagement \n" +
			"WHERE \n" +
			"(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
			"(COALESCE(:userId, null) IS NULL OR (USR_ID IN (:userId))) and \n" +
			"is_deleted=0 ", nativeQuery = true)
	public List<UserManagement> getUserIdV8(@Param(value = "userId") String userId,
										  @Param(value = "warehouseId") String warehouseId);

	@Query(value = "select * \n" +
			" from tblusermanagement \n" +
			"WHERE \n" +
			"(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
			"(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
			"(COALESCE(:userId, null) IS NULL OR (USR_ID IN (:userId))) and \n" +
			"is_deleted=0 ", nativeQuery = true)
	public List<UserManagement> getUserIdWithPlant(@Param(value = "userId") String userId,
											@Param(value = "plantId") String plantId,
											@Param(value = "warehouseId") String warehouseId);


	@Query(value = "select top 1 * \n" +
			" from tblusermanagement \n" +
			"WHERE \n" +
			"(COALESCE(:userId, null) IS NULL OR (USR_ID IN (:userId))) and \n" +
			"is_deleted=0 ", nativeQuery = true)
	UserManagement getUserDetails(@Param(value = "userId") String userId);

	@Query(value = "select top 1 * \n" +
			" from tblusermanagement \n" +
			"WHERE \n" +
			"(COALESCE(:userId, null) IS NULL OR (USR_ID IN (:userId))) and \n" +
			"c_id = :companyId and plant_id = :plantId and wh_id = :warehouseId and \n" +
			"is_deleted=0 ", nativeQuery = true)
	UserManagement getUserDetails(@Param(value = "userId") String userId,
								  @Param(value = "companyId") String companyId,
								  @Param(value = "plantId") String plantId,
								  @Param(value = "warehouseId") String warehouseId);

	@Query(value = "SELECT COUNT(*) FROM tblusermanagement " +
			"WHERE C_ID = :companyCode AND PLANT_ID = :plantId " +
			"AND WH_ID = :warehouseId AND LANG_ID = :languageId " +
			"AND IS_DELETED = 0", nativeQuery = true)
	Long getUserCountForWarehouse(@Param("companyCode") String companyCode,
								  @Param("plantId") String plantId,
								  @Param("warehouseId") String warehouseId,
								  @Param("languageId") String languageId);


	@Query(value = "select * \n" +
			" from tblusermanagement \n" +
			"WHERE \n" +
			"(COALESCE(:userId, null) IS NULL OR (USR_ID IN (:userId))) and \n" +
			"is_deleted=0 ", nativeQuery = true)
	public List<UserManagement> getUserIdV6(@Param(value = "userId") String userId);

}
