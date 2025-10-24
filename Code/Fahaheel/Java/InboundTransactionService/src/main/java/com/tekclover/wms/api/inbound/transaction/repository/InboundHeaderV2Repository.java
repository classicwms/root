package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundHeaderV2;
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

    @Transactional
    @Procedure(procedureName = "header_status_update_ib_cnf_proc")
    void updateHeaderStatusInboundConfirmProcedure(
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

    @Transactional
    @Procedure(procedureName = "[pah_grl_stgl_pibl_status_update_ib_cnf_proc]")
    void updatePahGrlStglPiblStatusInboundConfirmProcedure(
            @Param("companyCodeId") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusId") Long statusId,
            @Param("statusId2") Long statusId2,
            @Param("statusDescription") String statusDescription,
            @Param("statusDescription2") String statusDescription2,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

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
    @Procedure(procedureName = "inboundline_status_update_ib_cnf_individual_proc")
    public void updateInboundLineStatusUpdateInboundConfirmIndividualItemProc(
            @Param("companyCodeId") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("itemCode") String itemCode,
            @Param("manufacturerName") String manufacturerName,
            @Param("lineNumber") Long lineNumber,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn
    );

    //----------------------------Added-by-Muru--------------------------------------------------------------
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE tblinboundheader\r\n"
            + "	SET STATUS_ID = :statusId, STATUS_TEXT = :statusDescription, \r\n"
            + "	UTD_BY = :updatedBy, utd_on = :updatedOn, IB_CNF_BY = :updatedBy, IB_CNF_ON = :updatedOn\r\n"
            + "	WHERE IS_DELETED = 0 AND \r\n"
            + "			C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND \r\n"
            + "			REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo", nativeQuery = true)
    void updateInboundHeaderStatusOnPartialConfirmation(
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


    @Modifying(clearAutomatically = true)
    @Query(value = "update tblinboundheader set is_deleted = 1 where REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo ",nativeQuery = true)
    void softDeleteByRefDocNo(@Param("refDocNumber") String refDocNumber,
                              @Param("preInboundNo") String preInboundNo);

    @Modifying
    @Query(value = "UPDATE tblinboundheader SET STATUS_ID = :statusId, STATUS_TEXT = :statusDescription, " +
            "UTD_BY = :updatedBy, UTD_ON = :updatedOn, IB_CNF_BY = :updatedBy, IB_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId " +
            "AND LANG_ID = :languageId AND WH_ID = :warehouseId " +
            "AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo " +
            "AND (select count(*) from tblinboundline where ref_doc_no = :refDocNumber and PRE_IB_NO = :preInboundNo) = " +
            "(select count(*) from tblinboundline where ref_doc_no = :refDocNumber and PRE_IB_NO = :preInboundNo and status_id = 24) ", nativeQuery = true)
    void updateInboundHeader(@Param("statusId") Long statusId, @Param("statusDescription") String statusDescription,
                             @Param("updatedBy") String updatedBy, @Param("updatedOn") Date updatedOn,
                             @Param("companyCodeId") String companyCodeId, @Param("plantId") String plantId,
                             @Param("languageId") String languageId, @Param("warehouseId") String warehouseId,
                             @Param("refDocNumber") String refDocNumber, @Param("preInboundNo") String preInboundNo);
}