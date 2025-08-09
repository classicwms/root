package com.tekclover.wms.api.idmaster.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.user.UserManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.hhtuser.HhtUser;

@Repository
@Transactional
public interface HhtUserRepository extends JpaRepository<HhtUser, Long>, JpaSpecificationExecutor<HhtUser> {

    public List<HhtUser> findAll();

    public Optional<HhtUser>
    findByWarehouseIdAndUserIdAndDeletionIndicator(
            String warehouseId, String userId, Long deletionIndicator);

    public Optional<HhtUser> findByUserIdAndWarehouseIdAndCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
            String userId, String warehouseId, String companyCodeId, String plantId, String languageId, Long deletionIndicator);

    public List<HhtUser> findByWarehouseIdAndDeletionIndicator(String warehouseId, Long deletionIndicator);

//    public Optional<HhtUser> findByUserIdAndWarehouseIdAndCompanyCodeIdAndPlantIdAndLanguageIdAndLevelIdAndDeletionIndicator(
//            String userId, String warehouseId, String companyCodeId, String plantId, String languageId, Long levelId, Long deletionIndicator);

//    public Optional<HhtUser> findByUserIdAndWarehouseIdAndCompanyCodeIdAndPlantIdAndLanguageIdAndLevelIdAndDeletionIndicator(
//            String userId, String warehouseId, String companyCodeId, String plantId, String languageId, Long deletionIndicator);


    @Query(value ="select * \n"+
            " from tblhhtuser \n" +
            " where \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:userId, null) IS NULL OR (USR_ID IN (:userId))) and \n" +
            "(COALESCE(:levelId, null) IS NULL OR (LEVEL_ID IN (:levelId))) and \n" +
            "(COALESCE(:userPresent, null) IS NULL OR (USR_PRESENT IN (:userPresent))) and \n" +
            "(COALESCE(:noOfDaysLeave, null) IS NULL OR (NO_OF_DAYS_LEAVE IN (:noOfDaysLeave))) and \n" +
            " is_deleted = 0 ",nativeQuery = true)
    public List<HhtUser> getHhtUser(@Param("companyCodeId") List<String> companyCodeId,
                                    @Param("languageId") List<String> languageId,
                                    @Param("plantId") List<String> plantId,
                                    @Param("warehouseId") List<String> warehouseId,
                                    @Param("userId") List<String> userId,
                                    @Param("levelId") List<Long> levelId,
                                    @Param("userPresent") List<String> userPresent,
                                    @Param("noOfDaysLeave") List<String> noOfDaysLeave);

    @Query(value ="select * \n"+
            " from tblhhtuser \n" +
            " where \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:userId, null) IS NULL OR (USR_ID IN (:userId))) and \n" +
            "(COALESCE(:levelId, null) IS NULL OR (LEVEL_ID IN (:levelId))) and \n" +
            "(COALESCE(:userPresent, null) IS NULL OR (USR_PRESENT IN (:userPresent))) and \n" +
            "(COALESCE(:noOfDaysLeave, null) IS NULL OR (NO_OF_DAYS_LEAVE IN (:noOfDaysLeave))) and \n" +
            " is_deleted = 0 ",nativeQuery = true)
    public List<HhtUser> getHhtUserV4(@Param("companyCodeId") String companyCodeId,
                                    @Param("languageId") String languageId,
                                    @Param("plantId") String plantId,
                                    @Param("warehouseId") String warehouseId,
                                    @Param("userId") String userId,
                                    @Param("levelId") List<Long> levelId,
                                    @Param("userPresent") List<String> userPresent,
                                    @Param("noOfDaysLeave") List<String> noOfDaysLeave);

    @Query(value ="select * \n"+
            " from tblhhtuser \n" +
            " where \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:userId, null) IS NULL OR (USR_ID IN (:userId))) and \n" +
            "(getdate() between :startDate and :endDate) and \n" +
            " is_deleted = 0 ",nativeQuery = true)
    public List<HhtUser> getHhtUserAttendance(@Param("companyCodeId") String companyCodeId,
                                              @Param("languageId") String languageId,
                                              @Param("plantId") String plantId,
                                              @Param("warehouseId") String warehouseId,
                                              @Param("userId") String userId,
                                              @Param("startDate") Date startDate,
                                              @Param("endDate") Date endDate);

    @Query(value = "select top 1 * \n" +
            " from tblhhtuser \n" +
            "WHERE \n" +
            "(COALESCE(:userId, null) IS NULL OR (USR_ID IN (:userId))) and \n" +
            "is_deleted=0 ", nativeQuery = true)
    HhtUser getUserDetails(@Param(value = "userId") String userId);

}