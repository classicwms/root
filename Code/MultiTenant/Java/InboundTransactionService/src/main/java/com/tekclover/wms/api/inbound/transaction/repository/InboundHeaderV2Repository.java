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
    @Query(value = " UPDATE PIBL SET PIBL.STATUS_ID = :statusId, PIBL.STATUS_TEXT = :statusDescription, \n " +
            " PIBL.UTD_BY = :updatedBy, PIBL.UTD_ON = :updatedOn\n " +
            " FROM tblpreinboundline PIBL INNER JOIN\n " +
            " (SELECT C_ID,PLANT_ID,LANG_ID,WH_ID,REF_DOC_NO,PRE_IB_NO,IB_LINE_NO,ITM_CODE,MFR_NAME FROM tblinboundline\n " +
            " WHERE \n " +
            " IS_DELETED = 0 AND REF_FIELD_2 = 'TRUE' AND status_id = :statusId AND \n " +
            " C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND \n " +
            " REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo\n ) X ON\n " +
            " PIBL.C_ID = X.C_ID AND PIBL.PLANT_ID = X.PLANT_ID AND PIBL.LANG_ID = X.LANG_ID AND PIBL.WH_ID = X.WH_ID AND \n " +
            " PIBL.REF_DOC_NO = X.REF_DOC_NO AND PIBL.PRE_IB_NO = X.PRE_IB_NO AND PIBL.ITM_CODE = X.ITM_CODE AND \n " +
            " PIBL.MFR_NAME = X.MFR_NAME AND PIBL.IB_LINE_NO = X.IB_LINE_NO AND PIBL.IS_DELETED = 0 ", nativeQuery = true)
    void updatePiblStatusInboundConfirmV10(
            @Param("companyCodeId") String companyCodeId,
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
    @Query(value = "  UPDATE STGL SET STGL.STATUS_ID = :statusId2, STGL.STATUS_TEXT = :statusDescription2, \n " +
            " STGL.ST_CNF_BY = :updatedBy, STGL.ST_CNF_ON = :updatedOn\n " +
            " FROM tblstagingline STGL INNER JOIN\n " +
            " (SELECT C_ID,PLANT_ID,LANG_ID,WH_ID,REF_DOC_NO,PRE_IB_NO,IB_LINE_NO,ITM_CODE,MFR_NAME FROM tblinboundline\n " +
            " WHERE \n " +
            " IS_DELETED = 0 AND REF_FIELD_2 = 'TRUE' AND status_id = 24 AND \n " +
            " C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND \n " +
            " REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo ) X ON \n " +
            " STGL.C_ID = X.C_ID AND STGL.PLANT_ID = X.PLANT_ID AND STGL.LANG_ID = X.LANG_ID AND STGL.WH_ID = X.WH_ID AND \n " +
            " STGL.REF_DOC_NO = X.REF_DOC_NO AND STGL.PRE_IB_NO = X.PRE_IB_NO AND STGL.ITM_CODE = X.ITM_CODE AND \n " +
            " STGL.MFR_NAME = X.MFR_NAME AND STGL.IB_LINE_NO = X.IB_LINE_NO AND STGL.IS_DELETED = 0 ", nativeQuery = true)
    void updateStgStatusInboundConfirmProcedureV10(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusId2") Long statusId2,
            @Param("statusDescription2") String statusDescription2,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "  UPDATE GRL SET GRL.STATUS_ID = :statusId, GRL.STATUS_TEXT = :statusDescription, \n " +
            " GRL.GR_CNF_BY = :updatedBy, GRL.GR_CNF_ON = :updatedOn \n " +
            " FROM tblgrline GRL INNER JOIN \n " +
            " (SELECT C_ID,PLANT_ID,LANG_ID,WH_ID,REF_DOC_NO,PRE_IB_NO,IB_LINE_NO,ITM_CODE,MFR_NAME FROM tblinboundline \n " +
            " WHERE \n " +
            " IS_DELETED = 0 AND REF_FIELD_2 = 'TRUE' AND status_id = :statusId AND \n " +
            " C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND \n " +
            " REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo) X ON \n " +
            " GRL.C_ID = X.C_ID AND GRL.PLANT_ID = X.PLANT_ID AND GRL.LANG_ID = X.LANG_ID AND GRL.WH_ID = X.WH_ID AND \n " +
            " GRL.REF_DOC_NO = X.REF_DOC_NO AND GRL.PRE_IB_NO = X.PRE_IB_NO AND GRL.ITM_CODE = X.ITM_CODE AND \n " +
            " GRL.MFR_NAME = X.MFR_NAME AND GRL.IB_LINE_NO = X.IB_LINE_NO AND GRL.IS_DELETED = 0 AND GRL.STATUS_ID <> :statusId ", nativeQuery = true)
    void updateGrlStatusInboundConfirmProcedureV10(
            @Param("companyCodeId") String companyCodeId,
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
    @Query(value = "  UPDATE PAH SET PAH.STATUS_ID = :statusId, PAH.STATUS_TEXT = :statusDescription, \n " +
            " PAH.PA_CNF_BY =:updatedBy, PAH.PA_CNF_ON = :updatedOn \n " +
            " FROM tblputawayheader PAH INNER JOIN \n " +
            " (SELECT C_ID,PLANT_ID,LANG_ID,WH_ID,REF_DOC_NO,PRE_IB_NO,IB_LINE_NO,ITM_CODE,MFR_NAME FROM tblinboundline \n " +
            " WHERE \n " +
            " IS_DELETED = 0 AND REF_FIELD_2 = 'TRUE' AND status_id = :statusId AND \n " +
            " C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND \n " +
            " REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo ) X ON\n" +
            " PAH.C_ID = X.C_ID AND PAH.PLANT_ID = X.PLANT_ID AND PAH.LANG_ID = X.LANG_ID AND PAH.WH_ID = X.WH_ID AND \n " +
            " PAH.REF_DOC_NO = X.REF_DOC_NO AND PAH.PRE_IB_NO = X.PRE_IB_NO AND PAH.ref_field_5 = X.ITM_CODE AND \n " +
            " PAH.MFR_NAME = X.MFR_NAME AND PAH.REF_FIELD_9 = X.IB_LINE_NO AND PAH.IS_DELETED = 0 ", nativeQuery = true)
    void updatePahStatusInboundConfirmProcedureV10(
            @Param("companyCodeId") String companyCodeId,
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
    @Query(value = "UPDATE tblinboundheader\r\n"
            + "	SET STATUS_ID = :statusId, STATUS_TEXT = :statusDescription, \r\n"
            + "	UTD_BY = :updatedBy, utd_on = :updatedOn, IB_CNF_BY = :updatedBy, IB_CNF_ON = :updatedOn\r\n"
            + "	WHERE IS_DELETED = 0 AND \r\n"
            + "			C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND \r\n"
            + "			REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo", nativeQuery = true)
    void updateInboundHeaderStatusOnPartialConfirmationV10(
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

    void deleteByCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String companyCode, String plantId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);
    @Modifying
    @Query(value = "UPDATE tblpreinboundheader " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, UTD_BY = :updatedBy, UTD_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updatePreInboundHeaderV10(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblinboundheader " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, IB_CNF_BY = :updatedBy, IB_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updateInboundHeaderV10(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblstagingheader " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, ST_CNF_BY = :updatedBy, ST_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updateStagingHeaderV10(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblgrheader " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, GR_CNF_BY = :updatedBy, GR_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updateGrHeaderV10(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblputawayheader " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, PA_CNF_BY = :updatedBy, PA_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updatePutawayHeaderV10(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblpreinboundline " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, UTD_BY = :updatedBy, UTD_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updatePreInboundLineV10(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblinboundline " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, IB_CNF_BY = :updatedBy, IB_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updateInboundLineV10(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblstagingline " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, ST_CNF_BY = :updatedBy, ST_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updateStagingLineV10(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblgrline " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, GR_CNF_BY = :updatedBy, GR_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updateGrLineV10(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);
    @Modifying
    @Query(value = "UPDATE tblputawayline " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, PA_CNF_BY = :updatedBy, PA_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updatePutawayLineV10(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    //=================================================Auto Inbound Confirm==========================================

    @Modifying
    @Query(value = "UPDATE tblpreinboundheader " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, UTD_BY = :updatedBy, UTD_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updatePreInboundHeader(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblinboundheader " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, IB_CNF_BY = :updatedBy, IB_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updateInboundHeader(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblstagingheader " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, ST_CNF_BY = :updatedBy, ST_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updateStagingHeader(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblgrheader " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, GR_CNF_BY = :updatedBy, GR_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updateGrHeader(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblputawayheader " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, PA_CNF_BY = :updatedBy, PA_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updatePutawayHeader(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblpreinboundline " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, UTD_BY = :updatedBy, UTD_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updatePreInboundLine(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblinboundline " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, IB_CNF_BY = :updatedBy, IB_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updateInboundLine(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblstagingline " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, ST_CNF_BY = :updatedBy, ST_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updateStagingLine(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblgrline " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, GR_CNF_BY = :updatedBy, GR_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updateGrLine(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tblputawayline " +
            "SET STATUS_ID = 24, STATUS_TEXT = :statusDescription, PA_CNF_BY = :updatedBy, PA_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24", nativeQuery = true)
    void updatePutawayLine(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

}