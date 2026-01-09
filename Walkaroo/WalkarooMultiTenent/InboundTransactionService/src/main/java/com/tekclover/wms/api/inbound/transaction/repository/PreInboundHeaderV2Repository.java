package com.tekclover.wms.api.inbound.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.inbound.transaction.model.dto.HHTUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;

@Repository
@Transactional
public interface PreInboundHeaderV2Repository extends JpaRepository<PreInboundHeaderEntityV2, Long>,
        JpaSpecificationExecutor<PreInboundHeaderEntityV2>, StreamableJpaSpecificationRepository<PreInboundHeaderEntityV2> {

    public List<PreInboundHeaderEntityV2> findAll();

    public Optional<PreInboundHeaderEntityV2> findByPreInboundNoAndWarehouseIdAndDeletionIndicator(
            String preInboundNo, String warehouseId, Long deletionIndicator);

    public PreInboundHeaderEntityV2 findByWarehouseId(String warehouseId);

    public Optional<PreInboundHeaderEntityV2>
    findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String preInboundNo, String refDocNumber, Long deletionIndicator);

    // Pass WH_ID in PREINBOUNDHEADER table and fetch the Count of values where STATUS_ID=06,07 and Autopopulate
    public long countByWarehouseIdAndStatusIdIn(String warehouseId, List<Long> statusId);


    public List<PreInboundHeaderEntityV2> findByWarehouseIdAndStatusIdAndDeletionIndicator(
            String warehouseId, Long statusId, Long deletionIndicator);

    public Optional<PreInboundHeaderEntityV2> findByWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
            String warehouseId, String preInboundNo, String refDocNumner, Long deletionIndicator);

    public Optional<PreInboundHeaderEntityV2> findByPreInboundNoAndDeletionIndicator(String preInboundNo, Long deletionIndicator);

    public Optional<PreInboundHeaderEntityV2> findByRefDocNumberAndDeletionIndicator(String refDocNumber, Long deletionIndicator);
    public Optional<PreInboundHeaderEntityV2> findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(String refDocNumber, Long inboundOrderTypeId, Long deletionIndicator);

    @Query(value = "Select REF_DOC_TYP from tblpreinboundheader where WH_ID = :warehouseId and ref_doc_no = :refDocNumber \n" +
            " and PRE_IB_NO = :preInboundNo and IS_DELETED = :delete ", nativeQuery = true)
    public String getReferenceDocumentTypeFromPreInboundHeader(@Param("warehouseId") String warehouseId,
                                                               @Param("preInboundNo") String preInboundNo,
                                                               @Param("refDocNumber") String refDocNumber,
                                                               @Param("delete") Long delete);

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param statusId
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PreInboundHeaderEntityV2 ib SET ib.statusId = :statusId WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber")
    void updatePreInboundHeaderEntityV2Status(@Param("warehouseId") String warehouseId,
                                              @Param("refDocNumber") String refDocNumber,
                                              @Param("statusId") Long statusId);

    Optional<PreInboundHeaderEntityV2> findByPreInboundNoAndWarehouseIdAndCompanyCodeAndPlantIdAndLanguageIdAndDeletionIndicator(
            String preInboundNo, String warehouseId, String companyCode,
            String plantId, String languageId, Long deletionIndicator);

    List<PreInboundHeaderEntityV2> findByWarehouseIdAndCompanyCodeAndPlantIdAndLanguageIdAndStatusIdAndDeletionIndicator(
            String warehouseId, String companyCode, String plantId, String languageId,
            Long statusId, long deletionIndicator);

    Optional<PreInboundHeaderEntityV2> findByCompanyCodeAndPlantIdAndLanguageIdAndPreInboundNoAndWarehouseIdAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String preInboundNo, String warehouseId, Long deletionIndicator);

    Optional<PreInboundHeaderEntityV2> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String preInboundNo, String refDocNumner, Long deletionIndicator);

    PreInboundHeaderEntityV2 findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    PreInboundHeaderEntityV2 findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    boolean existsByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(String refDocNumber, Long inboundOrderTypeId, Long deletionIndicator);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PreInboundHeaderEntityV2 ib SET ib.statusId = :statusId, ib.statusDescription = :statusDescription " +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCode = :companyCode and ib.plantId = :plantId and ib.languageId = :languageId")
    void updatePreInboundHeaderEntityStatus(@Param("warehouseId") String warehouseId,
                                            @Param("companyCode") String companyCode,
                                            @Param("plantId") String plantId,
                                            @Param("languageId") String languageId,
                                            @Param("refDocNumber") String refDocNumber,
                                            @Param("statusId") Long statusId,
                                            @Param("statusDescription") String statusDescription);

    @Transactional
    @Procedure(procedureName = "pibheader_status_update_ib_cnf_proc")
    public void updatePreIbheaderStatusUpdateInboundConfirmProc(
            @Param("companyCodeId") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblpreinboundheader SET status_id = 0, status_text = '' where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber AND pre_ib_no = :preInboundNo " +
            "AND is_deleted = 0", nativeQuery = true)
    void updatePreInboundHeaderPGI(@Param("companyCodeId") String companyCodeId,
                                   @Param("plantId") String plantId,
                                   @Param("warehouseId") String warehouseId,
                                   @Param("refDocNumber") String refDocNumber,
                                   @Param("preInboundNo") String preInboundNo);


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
            "join tblusermanagement um on um.usr_id=ht.usr_id and um.c_id=ht.c_id and um.plant_id=ht.plant_id and um.wh_id=ht.wh_id \n" +
            "where \n" +
            "ht.c_id in (:companyCodeId) and \n" +
            "ht.plant_id in (:plantId) and \n" +
            "ht.lang_id in (:languageId) and \n" +
            "ht.wh_id in (:warehouseId) and \n" +
            "um.usr_typ_id in (:userTypeId) and \n" +
            "ht.is_deleted = 0 ", nativeQuery = true)
    public List<HHTUser> getHHTUserListV3(@Param("companyCodeId") String companyCodeId,
                                          @Param("plantId") String plantId,
                                          @Param("languageId") String languageId,
                                          @Param("warehouseId") String warehouseId,
                                          @Param("userTypeId") Long userTypeId);
}