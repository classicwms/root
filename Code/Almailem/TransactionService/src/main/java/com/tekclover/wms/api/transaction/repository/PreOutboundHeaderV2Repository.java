package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.dto.HHTUser;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface PreOutboundHeaderV2Repository extends JpaRepository<PreOutboundHeaderV2, Long>,
        JpaSpecificationExecutor<PreOutboundHeaderV2>, StreamableJpaSpecificationRepository<PreOutboundHeaderV2> {

    Optional<PreOutboundHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String refDocNumber, Long deletionIndicator);

    Optional<PreOutboundHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndPartnerCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId,
            String refDocNumber, String preOutboundNo, String partnerCode, Long deletionIndicator);

    PreOutboundHeaderV2 findByPreOutboundNoAndDeletionIndicator(String preOutboundNo, Long deletionIndicator);

    Optional<PreOutboundHeaderV2> findByRefDocNumberAndDeletionIndicator(String refDocumentNo, Long deletionIndicator);

    Optional<PreOutboundHeaderV2> findByRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(String refDocumentNo, Long outboundOrderTypeId, Long deletionIndicator);

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param statusId
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PreOutboundHeaderV2 ib SET ib.statusId = :statusId, REF_FIELD_10 = :refField10, STATUS_TEXT = :refField10 \n" +
            "WHERE ib.languageId = :languageId AND ib.companyCodeId = :companyCodeId AND ib.plantId = :plantId AND ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber")
    void updatePreOutboundHeaderStatus(@Param("companyCodeId") String companyCodeId, @Param("plantId") String plantId, @Param("languageId") String languageId,
                                       @Param("warehouseId") String warehouseId, @Param("refDocNumber") String refDocNumber, @Param("statusId") Long statusId, @Param("refField10") String refField10);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE tblpreoutboundheader SET STATUS_ID = :statusId, REF_FIELD_10 = :statusDescription, STATUS_TEXT = :statusDescription \n" +
            "WHERE LANG_ID = :languageId AND C_ID = :companyCodeId AND \n" +
            "PLANT_ID = :plantId AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_OB_NO = :preOutboundNo", nativeQuery = true)
    void updatePreOutboundHeaderStatusV2(@Param("companyCodeId") String companyCodeId,
                                         @Param("plantId") String plantId,
                                         @Param("languageId") String languageId,
                                         @Param("warehouseId") String warehouseId,
                                         @Param("refDocNumber") String refDocNumber,
                                         @Param("preOutboundNo") String preOutboundNo,
                                         @Param("statusId") Long statusId,
                                         @Param("statusDescription") String statusDescription);
//    @Transactional
//    @Procedure(procedureName = "preoutbound_header_update_proc")
//    void updatePreOutboundHeaderUpdateProc(
//                                @Param("companyCodeId") String companyCodeId,
//                                @Param("plantId") String plantId,
//                                @Param("languageId") String languageId,
//                                @Param("warehouseId") String warehouseId,
//                                @Param("refDocNumber") String refDocNumber,
//                                @Param("statusId") Long statusId,
//                                @Param("statusDescription") String statusDescription
//                                );

    @Query(value = "select ht.usr_id from tblhhtuser ht \n" +
            "join tblordertypeid ot on ot.usr_id=ht.usr_id \n" +
            "where ht.is_deleted = 0 and ot.ob_ord_typ_id in (:outboundOrderTypeId) ", nativeQuery = true)
    public List<String> getHHTUserList(
            @Param("outboundOrderTypeId") Long outboundOrderTypeId);

    @Query(value = "select ht.usr_id from tblhhtuser ht \n" +
            "join tblordertypeid ot on ot.usr_id=ht.usr_id \n" +
            "where ht.is_deleted = 0 and \n" +
            "ht.c_id in (:companyCodeId) and \n" +
            "ht.plant_id in (:plantId) and \n" +
            "ht.lang_id in (:languageId) and \n" +
            "ht.wh_id in (:warehouseId) and \n" +
            "ot.ob_ord_typ_id in (:outboundOrderTypeId) ", nativeQuery = true)
    public List<String> getHHTUserList(
            @Param("outboundOrderTypeId") Long outboundOrderTypeId,
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId);

    @Query(value = "select \n" +
            "ht.USR_ID userId,\n" +
            "ht.LANG_ID languageId,\n" +
            "ht.C_ID companyCodeId,\n" +
            "ht.PLANT_ID plantId,\n" +
            "ht.WH_ID warehouseId,\n" +
            "ht.LEVEL_ID levelId,\n" +
            "ht.PASSWORD password,\n" +
            "ht.USER_NM userName,\n" +
            "ht.START_DATE startDate,\n" +
            "ht.END_DATE endDate,\n" +
            "ht.USR_PRESENT userPresent,\n" +
            "ht.NO_OF_DAYS_LEAVE noOfDaysLeave \n" +
            "from tblhhtuser ht \n" +
            "join tblordertypeid ot on ot.usr_id=ht.usr_id \n" +
            "where ht.is_deleted = 0 and \n" +
            "ht.c_id in (:companyCodeId) and \n" +
            "ht.plant_id in (:plantId) and \n" +
            "ht.lang_id in (:languageId) and \n" +
            "ht.wh_id in (:warehouseId) and \n" +
            "ot.ob_ord_typ_id in (:outboundOrderTypeId) ", nativeQuery = true)
    public List<HHTUser> getHHTUserListNew(@Param("outboundOrderTypeId") Long outboundOrderTypeId,
                                            @Param("companyCodeId") String companyCodeId,
                                            @Param("plantId") String plantId,
                                            @Param("languageId") String languageId,
                                            @Param("warehouseId") String warehouseId);

    @Query(value = "select ht.usr_id from tblhhtuser ht \n" +
            "join tblordertypeid ot on ot.usr_id=ht.usr_id \n" +
            "where ht.is_deleted = 0 and \n" +
            "ht.c_id in (:companyCodeId) and \n" +
            "ht.plant_id in (:plantId) and \n" +
            "ht.lang_id in (:languageId) and \n" +
            "ht.wh_id in (:warehouseId) and \n" +
            "ht.USR_PRESENT = '1' and \n" +
            "ot.ob_ord_typ_id in (:outboundOrderTypeId) ", nativeQuery = true)
    public List<String> getHHTUserPresentList(
            @Param("outboundOrderTypeId") Long outboundOrderTypeId,
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId);

    @Query(value = "select ht.usr_id from tblhhtuser ht \n" +
            "where ht.is_deleted = 0 and ht.level_id in (:levelId) ", nativeQuery = true)
    public List<String> getHHTUserListByLevelId(
            @Param("levelId") Long levelId);

    @Query(value = "select ht.usr_id from tblhhtuser ht \n" +
            "where \n" +
            "ht.c_id in (:companyCodeId) and \n" +
            "ht.plant_id in (:plantId) and \n" +
            "ht.lang_id in (:languageId) and \n" +
            "ht.wh_id in (:warehouseId) and \n" +
            "ht.is_deleted = 0 \n" +
            "and ht.level_id in (:levelId) ", nativeQuery = true)
    public List<String> getHHTUserListByLevelId(
            @Param("levelId") Long levelId,
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId
    );

    @Query(value = "select \n" +
            "ht.USR_ID userId,\n" +
            "ht.LANG_ID languageId,\n" +
            "ht.C_ID companyCodeId,\n" +
            "ht.PLANT_ID plantId,\n" +
            "ht.WH_ID warehouseId,\n" +
            "ht.LEVEL_ID levelId,\n" +
            "ht.PASSWORD password,\n" +
            "ht.USER_NM userName,\n" +
            "ht.START_DATE startDate,\n" +
            "ht.END_DATE endDate,\n" +
            "ht.USR_PRESENT userPresent,\n" +
            "ht.NO_OF_DAYS_LEAVE noOfDaysLeave \n" +
            "from tblhhtuser ht \n" +
            "where \n" +
            "ht.c_id in (:companyCodeId) and \n" +
            "ht.plant_id in (:plantId) and \n" +
            "ht.lang_id in (:languageId) and \n" +
            "ht.wh_id in (:warehouseId) and \n" +
            "ht.is_deleted = 0 \n" +
            "and ht.level_id in (:levelId) ", nativeQuery = true)
    public List<HHTUser> getHHTUserListByLevelIdNew(
            @Param("levelId") Long levelId,
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId
    );

    @Query(value = "select ht.usr_id from tblhhtuser ht \n" +
            "where \n" +
            "ht.c_id in (:companyCodeId) and \n" +
            "ht.plant_id in (:plantId) and \n" +
            "ht.lang_id in (:languageId) and \n" +
            "ht.wh_id in (:warehouseId) and \n" +
            "ht.is_deleted = 0 and \n" +
            "ht.USR_PRESENT = '1' and \n" +
            "ht.level_id in (:levelId) ", nativeQuery = true)
    public List<String> getHHTUserByLevelIdPresentList(
            @Param("levelId") Long levelId,
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId
    );

    @Query(value ="select usr_id \n"+
            " from tblhhtuser \n" +
            " where \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:userId, null) IS NULL OR (USR_ID IN (:userId))) and \n" +
            "(getdate() between :startDate and :endDate) and \n" +
            " is_deleted = 0 ",nativeQuery = true)
    public List<String> getHhtUserAttendance(@Param("companyCodeId") String companyCodeId,
                                              @Param("languageId") String languageId,
                                              @Param("plantId") String plantId,
                                              @Param("warehouseId") String warehouseId,
                                              @Param("userId") String userId,
                                              @Param("startDate") Date startDate,
                                              @Param("endDate") Date endDate);

    PreOutboundHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    PreOutboundHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo, Long deletionIndicator);

}