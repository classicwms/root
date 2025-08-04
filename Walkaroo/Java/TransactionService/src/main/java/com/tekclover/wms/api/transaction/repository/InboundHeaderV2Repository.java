package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;

@Repository
@Transactional
public interface InboundHeaderV2Repository extends JpaRepository<InboundHeaderV2, Long>,
        JpaSpecificationExecutor<InboundHeaderV2>, StreamableJpaSpecificationRepository<InboundHeaderV2> {

    Optional<InboundHeaderV2> findByWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    List<InboundHeaderV2> findByRefDocNumberAndDeletionIndicator(String refDocNumber, Long deletionIndicator);

    Optional<InboundHeaderV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId,
            String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    @Query(value = "Select \r\n" +
            "CASE \r\n" +
            "When OrderLinesCount IS NOT NULL THEN OrderLinesCount \r\n" +
            "Else 0 \r\n" +
            "END as countOfOrderLines \r\n" +
            "From \r\n" +
            "(Select COUNT(*) as OrderLinesCount \r\n" +
            "From tblinboundline \r\n" +
            "Where ref_doc_no IN (:refDocNumber) AND is_deleted = 0 \r\n" +
            ") As CountsSubquery ", nativeQuery = true)
    Long getCountOfTheOrderLinesByRefDocNumber(@Param(value = "refDocNumber") String refDocNumber);

    @Query(value = "Select \r\n" +
            "CASE \r\n" +
            "When OrderLinesCount IS NOT NULL THEN OrderLinesCount \r\n" +
            "Else 0 \r\n" +
            "END as countOfOrderLines \r\n" +
            "From \r\n" +
            "(Select COUNT(*) as OrderLinesCount \r\n" +
            "From tblinboundline \r\n" +
            "Where ref_doc_no IN (:refDocNumber) AND is_deleted = 0 AND \r\n" +
            "PRE_IB_NO IN (:preInboundNo) AND C_ID = :companyCode AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId \r\n" +
            ") As CountsSubquery ", nativeQuery = true)
    Long getCountOfTheOrderLinesByRefDocNumber(@Param("refDocNumber") String refDocNumber,
                                               @Param("companyCode") String companyCode,
                                               @Param("preInboundNo") String preInboundNo,
                                               @Param("plantId") String plantId,
                                               @Param("languageId") String languageId,
                                               @Param("warehouseId") String warehouseId);

    @Query(value = "Select \r\n" +
            "CASE \r\n" +
            "When SUM(il.accept_qty + il.damage_qty) > 0 Then \r\n" +
            "(Select COUNT(*) \r\n" +
            "From tblinboundline \r\n" +
            "Where ref_doc_no IN (:refDocNumber) And is_deleted = 0 AND STATUS_ID = 20) \r\n" +
            "Else 0 \r\n" +
            "END as receivedLines \r\n" +
            "From tblinboundline il ", nativeQuery = true)
    Long getReceivedLinesByRefDocNumberOld(@Param(value = "refDocNumber") String refDocNumber);

    @Query(value =
            "Select COUNT(*) \r\n" +
            "From tblputawayline \r\n" +
            "Where ref_doc_no IN (:refDocNumber) And is_deleted = 0 AND STATUS_ID IN (20,24) \r\n"
           , nativeQuery = true)
    Long getReceivedLinesByRefDocNumber(@Param(value = "refDocNumber") String refDocNumber);
    
    // V3
    @Query(value =
            "Select COUNT(*) \r\n" +
            "From tblinboundline \r\n" +
            "Where PRE_IB_NO IN (:preInboundNo) And is_deleted = 0  \r\n"
           , nativeQuery = true)
    public Long findCountOfConfirmedInboundLines (@Param(value = "preInboundNo") String preInboundNo);

    // V3
    @Query(value =
            "Select COUNT(*) \r\n" +
            "From tblputawayline \r\n" +
            "Where PRE_IB_NO IN (:preInboundNo) And is_deleted = 0  \r\n"
           , nativeQuery = true)
    public Long findCountOfConfirmedPutAwayLines (@Param(value = "preInboundNo") String preInboundNo);

    @Query(value =
            "Select COUNT(*) \r\n" +
            "From tblputawayline \r\n" +
            "Where ref_doc_no IN (:refDocNumber) And is_deleted = 0 AND STATUS_ID IN (20,24) AND\r\n"+
            "PRE_IB_NO IN (:preInboundNo) AND C_ID = :companyCode AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId \r\n"
           , nativeQuery = true)
    Long getReceivedLinesByRefDocNumber(@Param("refDocNumber") String refDocNumber,
                                        @Param("companyCode") String companyCode,
                                        @Param("preInboundNo") String preInboundNo,
                                        @Param("plantId") String plantId,
                                        @Param("languageId") String languageId,
                                        @Param("warehouseId") String warehouseId);

    InboundHeaderV2 findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    InboundHeaderV2 findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE InboundHeaderV2 ib SET ib.statusId = :statusId, ib.confirmedBy = :confirmedBy, ib.confirmedOn = :confirmedOn, ib.statusDescription = :statusDescription \n" +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCode = :companyCode and ib.plantId = :plantId and ib.languageId = :languageId")
    void updateInboundHeaderStatus(@Param("warehouseId") String warehouseId,
                                   @Param("companyCode") String companyCode,
                                   @Param("plantId") String plantId,
                                   @Param("languageId") String languageId,
                                   @Param("refDocNumber") String refDocNumber,
                                   @Param("statusId") Long statusId,
                                   @Param("statusDescription") String statusDescription,
                                   @Param("confirmedBy") String confirmedBy,
                                   @Param("confirmedOn") Date confirmedOn);

    @Transactional
    @Procedure(procedureName = "ibheader_status_update_ib_cnf_proc")
    public void updateIbheaderStatusUpdateInboundConfirmProc(
            @Param("companyCodeId") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn
    );

    // V3
    @Query(value = "select sum(ord_qty) ordQty, sum(coalesce(accept_qty,0) + coalesce(damage_qty,0)) rxdQty \n" +
            "From tblinboundline \n" +
            "Where PRE_IB_NO IN (:preInboundNo) \n" +
            "AND C_ID = :companyCode AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId \n" +
            "And IS_DELETED = 0 GROUP BY PRE_IB_NO ", nativeQuery = true)
    public IKeyValuePair findSumOfConfirmedInboundLines(@Param("companyCode") String companyCode,
                                                        @Param("plantId") String plantId,
                                                        @Param("languageId") String languageId,
                                                        @Param("warehouseId") String warehouseId,
                                                        @Param("preInboundNo") String preInboundNo);

    @Transactional
    @Procedure(procedureName = "all_status_update_ib_cnf_proc")
    void updateAllStatusInboundConfirmProcedure(@Param("companyCodeId") String companyCodeId,
                                                @Param("plantId") String plantId,
                                                @Param("languageId") String languageId,
                                                @Param("warehouseId") String warehouseId,
                                                @Param("preInboundNo") String preInboundNo,
                                                @Param("refDocNumber") String refDocNumber,
                                                @Param("statusId") Long statusId,
                                                @Param("statusDescription") String statusDescription,
                                                @Param("updatedBy") String updatedBy,
                                                @Param("updatedOn") Date updatedOn);

    @Query(value = "SELECT CASE WHEN \n" +
            "(select sum(ord_qty) from tblinboundline where c_id = :companyCodeId and plant_id = :plantId \n" +
            " and lang_id = :languageId and wh_id = :warehouseId and REF_DOC_NO = :refDocNumber and is_deleted = 0 and status_id <> :statusId) = \n" +
            "(select sum(PA_CNF_QTY) from tblputawayline where c_id = :companyCodeId and plant_id = :plantId and \n" +
            "lang_id = :languageId and wh_id = :warehouseId and REF_DOC_NO = :refDocNumber and is_deleted = 0 and status_id <> :statusId) \n" +
            "THEN 1 else 0 END as Result ", nativeQuery = true)
    public Long checkAutoConfirmStatus(@Param("companyCodeId") String companyCodeId,
                                       @Param("plantId") String plantId,
                                       @Param("languageId") String languageId,
                                       @Param("warehouseId") String warehouseId,
                                       @Param("refDocNumber") String refDocNumber,
                                       @Param("statusId") Long statusId);

    @Modifying
    @Query(value = "UPDATE InboundHeaderV2 ib SET ib.statusId = :statusId, ib.statusDescription = :statusDescription \n" +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCode = :companyCode \n" +
            "and ib.plantId = :plantId and ib.languageId = :languageId")
    void updateInboundHeaderStatusPGI(@Param("warehouseId") String warehouseId,
                                   @Param("companyCode") String companyCode,
                                   @Param("plantId") String plantId,
                                   @Param("languageId") String languageId,
                                   @Param("refDocNumber") String refDocNumber,
                                   @Param("statusId") Long statusId,
                                   @Param("statusDescription") String statusDescription);
}