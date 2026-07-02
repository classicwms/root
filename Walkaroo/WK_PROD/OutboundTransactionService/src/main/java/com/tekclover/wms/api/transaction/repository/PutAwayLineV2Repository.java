package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayHeader;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.transaction.model.report.PickingProductivityImpl;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;


@Repository
@Transactional
public interface PutAwayLineV2Repository extends JpaRepository<PutAwayLineV2, Long>,
        JpaSpecificationExecutor<PutAwayLineV2>, StreamableJpaSpecificationRepository<PutAwayLineV2> {


    Optional<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndLineNoAndItemCodeAndProposedStorageBinAndConfirmedStorageBinInAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String goodsReceiptNo, String preInboundNo, String refDocNumber,
            String putAwayNumber, Long lineNo, String itemCode, String proposedStorageBin,
            List<String> confirmedStorageBin, Long deletionIndicator);

    List<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String preInboundNo, String refDocNumber, Long lineNo, String itemCode, Long deletionIndicator);

    /**
     * @param companyId
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    @Query(value = "SELECT COUNT(*) FROM tblputawayline WHERE LANG_ID = :languageId AND C_ID = :companyId AND PLANT_ID = :plantId AND WH_ID = :warehouseId \r\n"
            + "AND PRE_IB_NO = :preInboundNo AND REF_DOC_NO = :refDocNumber AND STATUS_ID IN (20, 22) AND IS_DELETED = 0", nativeQuery = true)
    public long getPutawayLineCountByStatusId(
            @Param("companyId") String companyId,
            @Param("plantId") String plantId,
            @Param("warehouseId") String warehouseId,
            @Param("languageId") String languageId,
            @Param("preInboundNo") String preInboundNo,
            @Param("refDocNumber") String refDocNumber);


    @Query(value = "SELECT SUM(PA_CNF_QTY) FROM tblputawayline WHERE LANG_ID = :languageId AND C_ID = :companyId AND PLANT_ID = :plantId AND WH_ID = :warehouseId \r\n"
            + "AND PRE_IB_NO = :preInboundNo AND REF_DOC_NO = :refDocNumber AND STATUS_ID IN (20) AND IS_DELETED = 0", nativeQuery = true)
    public double getSumOfPutawayLineQtyByStatusId20(
            @Param("companyId") String companyId,
            @Param("plantId") String plantId,
            @Param("warehouseId") String warehouseId,
            @Param("languageId") String languageId,
            @Param("preInboundNo") String preInboundNo,
            @Param("refDocNumber") String refDocNumber);

    /**
     * @param companyId
     * @param plantId
     * @param warehouseId
     * @param putAwayNumber
     * @param refDocNumber
     * @param statusId
     * @return
     */
    @Query(value = "SELECT COUNT(*) FROM tblputawayline WHERE LANG_ID = :languageId AND C_ID = :companyId AND PLANT_ID = :plantId AND WH_ID = :warehouseId \r\n"
            + "AND PA_NO = :putAwayNumber AND REF_DOC_NO = :refDocNumber AND STATUS_ID = :statusId AND IS_DELETED = 0", nativeQuery = true)
    public long getPutawayLineCountByStatusId(
            @Param("companyId") String companyId,
            @Param("plantId") String plantId,
            @Param("warehouseId") String warehouseId,
            @Param("languageId") String languageId,
            @Param("putAwayNumber") String putAwayNumber,
            @Param("refDocNumber") String refDocNumber,
            @Param("statusId") Long statusId);

    List<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String refDocNumber, String putAwayNumber, Long deletionIndicator);

    List<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String refDocNumber, String packBarcodes, Long deletionIndicator);

    List<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String refDocNumber, Long deletionIndicator);

    @Query(value = "SELECT * from tblputawayheader \n"
            + "where pa_no = :putAwayNumber and is_deleted = 0", nativeQuery = true)
    public PutAwayHeader getPutAwayHeader(@Param(value = "putAwayNumber") String putAwayNumber);

//    List<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndProposedStorageBinAndStatusIdAndCreatedOnBetweenAndDeletionIndicator(
//            String languageId, String companyCode, String plantId, String warehouseId, String itemCode,
//            String manufacturerName, String storageBin, Long statusId, Date stockCountDate, Date date, Long deletionIndicator);

    List<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndProposedStorageBinAndStatusIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String itemCode,
            String manufacturerName, String storageBin, Long statusId, Long deletionIndicator);

    List<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndStatusIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, Long statusId, Long deletionIndicator);

    PutAwayLineV2 findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndItemCodeAndPreInboundNoAndBarcodeIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,String itemCode,
            String preInboundNo, String barCodeId, Long deletionIndicator);

    PutAwayLineV2 findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndStatusIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
            String preInboundNo, String itemCode, String manufacturerName, Long lineNumber, Long statusId, Long deletionIndicator);

    List<PutAwayLineV2> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    List<PutAwayLineV2> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    List<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndStatusIdAndPackBarcodesAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
            String preInboundNo, String itemCode, String manufacturerName, Long lineNumber, Long statusId, String packBarcodes, Long deletionIndicator);

    List<PutAwayLineV2> findByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndStatusIdAndDeletionIndicator(
            String companyCode, String languageId, String plantId, String warehouseId, String refDocNumber, String preInboundNo,
            String itemCode, String manufacturerName, Long lineNumber, Long statusId, Long deletionIndicator);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PutAwayLineV2 ib SET ib.statusId = :statusId, ib.statusDescription = :statusDescription \n" +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCode = :companyCode and ib.plantId = :plantId and ib.languageId = :languageId and ib.statusId = 20")
    void updatePutawayLineStatus(@Param("warehouseId") String warehouseId,
                                 @Param("companyCode") String companyCode,
                                 @Param("plantId") String plantId,
                                 @Param("languageId") String languageId,
                                 @Param("refDocNumber") String refDocNumber,
                                 @Param("statusId") Long statusId,
                                 @Param("statusDescription") String statusDescription);

    @Query(value = "SELECT DATEDIFF(MINUTE, ib.PA_CTD_ON, :lDate) from tblputawayheader ib \n"
            + "where ib.pa_no = :putAwayNumber and ib.wh_id = :warehouseId and ib.c_id = :companyCode and ib.plant_Id = :plantId and ib.lang_Id = :languageId and ib.is_deleted = 0", nativeQuery = true)
    public String getleadtime(@Param("companyCode") String companyCode,
                              @Param("plantId") String plantId,
                              @Param("languageId") String languageId,
                              @Param("warehouseId") String warehouseId,
                              @Param(value = "putAwayNumber") String putAwayNumber,
                              @Param("lDate") Date lDate);

    @Query(value = "select sum(pa_cnf_qty) \n" +
            "from tblputawayline where c_id = :companyCode and plant_id = :plantId and lang_id = :languageId and \n" +
            "wh_id = :warehouseId and REF_DOC_NO = :refDocNumber and PRE_IB_NO = :preInboundNo and \n" +
            "itm_code = :itemCode and mfr_name = :manufacturerName and \n" +
            "is_deleted = 0 and ib_line_no = :lineNo \n" +
            "group by itm_code,mfr_name,pre_ib_no,ref_doc_no,ib_line_no,lang_id,wh_id,plant_id,c_id ", nativeQuery = true)
    public Double getPutawayCnfQuantity(@Param("companyCode") String companyCode,
                                        @Param("plantId") String plantId,
                                        @Param("languageId") String languageId,
                                        @Param("warehouseId") String warehouseId,
                                        @Param("refDocNumber") String refDocNumber,
                                        @Param("preInboundNo") String preInboundNo,
                                        @Param("itemCode") String itemCode,
                                        @Param("manufacturerName") String manufacturerName,
                                        @Param("lineNo") Long lineNo);

    @Transactional
    @Procedure(procedureName = "putawayline_status_update_ib_cnf_proc")
    public void updatePutawayLineStatusUpdateInboundConfirmProc(
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
    @Procedure(procedureName = "ibheader_pal_cnt_update_proc")
    public void updateInboundHeaderRxdLinesCountProc(
            @Param("companyCodeId") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo
    );

    @Query(value = "select \n" +
            "sum(datediff(MINUTE,pa_ctd_on,pa_utd_on)) leadTime, \n" +
            "round((sum(pa_cnf_qty)/NULLIF(sum(datediff(MINUTE,pa_ctd_on,pa_utd_on)),0)*60),2) partsPerHr, \n" +
            "round(sum(datediff(MINUTE,pa_ctd_on,pa_utd_on))/NULLIF(sum(pa_cnf_qty),0),2) avgLeadTime, \n" +
            "sum(pa_cnf_qty) parts, \n" +
            "count(pre_ib_no) orders, \n" +
            "c_id companyCodeId, \n" +
            "plant_id plantId, \n" +
            "lang_id languageId, \n" +
            "wh_id warehouseId, \n" +
            "c_text companyDescription, \n" +
            "plant_text plantDescription, \n" +
            "wh_text warehouseDescription, \n" +
            "pa_cnf_by assignedPickerId\n" +
            "into #pal \n" +
            "from tblputawayline where is_deleted = 0 and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:assignedPickerId, null) IS NULL OR (pa_cnf_by IN (:assignedPickerId))) and \n" +
            "(COALESCE(:refDocNo, null) IS NULL OR (ref_doc_no IN (:refDocNo))) and \n" +
            "(COALESCE(:preInboundNo, null) IS NULL OR (pre_ib_no IN (:preInboundNo))) and \n" +
            "(COALESCE(:orderType, null) IS NULL OR (ib_ord_typ_id IN (:orderType))) and \n" +
            "(COALESCE(:statusId, null) IS NULL OR (status_id IN (:statusId))) and \n" +
            "(COALESCE(CONVERT(VARCHAR(255), :startConfirmedOn), null) IS NULL OR (PA_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :startConfirmedOn), null) and COALESCE(CONVERT(VARCHAR(255), :endConfirmedOn), null)))\n" +
            "group by pa_cnf_by,plant_id,wh_id,c_id,lang_id,plant_text,wh_text,c_text \n" +

            "select *, \n" +
            "(select sum(partsPerHr) from #pal) totalPartsPerHr, \n" +
            "(select sum(avgLeadTime) from #pal) totalLeadTime, \n" +
            "(select avg(avgLeadTime) from #pal) avgTotalLeadTime, \n" +
            "(select sum(parts) from #pal) totalParts, \n" +
            "(select sum(orders) from #pal) totalOrders \n" +
            "from #pal", nativeQuery = true)
    List<PickingProductivityImpl> findBinningProductivityReport(@Param(value = "companyCodeId") List<String> companyCodeId,
                                                                @Param(value = "plantId") List<String> plantId,
                                                                @Param(value = "languageId") List<String> languageId,
                                                                @Param(value = "warehouseId") List<String> warehouseId,
                                                                @Param(value = "refDocNo") List<String> refDocNo,
                                                                @Param(value = "preInboundNo") List<String> preInboundNo,
                                                                @Param(value = "assignedPickerId") List<String> assignedPickerId,
                                                                @Param(value = "orderType") List<Long> orderType,
                                                                @Param(value = "statusId") List<Long> statusId,
                                                                @Param(value = "startConfirmedOn") Date startConfirmedOn,
                                                                @Param(value = "endConfirmedOn") Date endConfirmedOn);

    PutAwayLineV2 findTopByCompanyCodeAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndManufacturerNameAndStatusIdAndDeletionIndicatorOrderByCreatedOn(
            String companyCodeId, String plantId, String warehouseId, String languageId, String itemCode, String manufacturerName, Long statusId, Long deletionIndicator);

    List<PutAwayLineV2> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndStatusIdNotAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, String preInboundNo, Long statusId, Long deletionIndicator);

    PutAwayLineV2 findTopByCompanyCodeAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndStatusIdAndDeletionIndicatorOrderByCreatedOn(
            String companyCodeId, String plantId, String warehouseId, String languageId, String itemCode, Long statusId, Long deletionIndicator);

    @Query(value = "SELECT MAX(pa_cnf_by) assign, CONVERT(DATE, pa_utd_on) as pickDate, SUM(CAST(ref_field_1 AS INT)) leadTime \n" +
            "FROM tblputawayline \n" +
            "WHERE is_deleted = 0 AND \n" +
            "(COALESCE(:startConfirmedOn, null) IS NULL OR (pa_utd_on between :startConfirmedOn and :endConfirmedOn)) \n" +
            "GROUP BY CONVERT(DATE, pa_utd_on), pa_cnf_by", nativeQuery = true)
    List<Object[]> getLeadTime(@Param(value = "startConfirmedOn") Date startConfirmedOn,
                               @Param(value = "endConfirmedOn") Date endConfirmedOn);

    @Query(value = "SELECT MAX(pa_cnf_by) assign, CONVERT(DATE, pa_utd_on) as pickDate, SUM(PA_QTY) quantity \n" +
            "FROM tblputawayline \n" +
            "WHERE is_deleted = 0 AND \n" +
            "(COALESCE(:startConfirmedOn, null) IS NULL OR (pa_utd_on between :startConfirmedOn and :endConfirmedOn)) \n" +
            "GROUP BY CONVERT(DATE, pa_utd_on), pa_cnf_by", nativeQuery = true)
    List<Object[]> getQuantity(@Param(value = "startConfirmedOn") Date startConfirmedOn,
                               @Param(value = "endConfirmedOn") Date endConfirmedOn);

    @Query(value = "SELECT MAX(pa_cnf_by) assign, CONVERT(DATE, pa_utd_on) as pickDate, AVG(CAST(ref_field_1 AS INT)) leadTime \n" +
            "FROM tblputawayline \n" +
            "WHERE is_deleted = 0 AND \n" +
            "(COALESCE(:startConfirmedOn, null) IS NULL OR (pa_utd_on between :startConfirmedOn and :endConfirmedOn)) \n" +
            "GROUP BY CONVERT(DATE, pa_utd_on), pa_cnf_by", nativeQuery = true)
    List<Object[]> getAverageLeadTime(@Param(value = "startConfirmedOn") Date startConfirmedOn,
                               @Param(value = "endConfirmedOn") Date endConfirmedOn);

    @Query(value = "SELECT MAX(pa_cnf_by) assign, " +
            "CONVERT(DATE, pa_utd_on) as pickDate, " +
            "CASE WHEN SUM(CAST(ref_field_1 AS INT)) != 0 THEN ((SUM(PA_QTY) / SUM(CAST(ref_field_1 AS INT))) * 60) " +
            "ELSE 0 END as leadTime " +
            "FROM tblputawayline " +
            "WHERE is_deleted = 0 AND " +
            "(COALESCE(:startConfirmedOn, null) IS NULL OR (pa_utd_on BETWEEN :startConfirmedOn AND :endConfirmedOn)) " +
            "GROUP BY CONVERT(DATE, pa_utd_on), pa_cnf_by", nativeQuery = true)
    List<Object[]> getProductivity(@Param(value = "startConfirmedOn") Date startConfirmedOn,
                                   @Param(value = "endConfirmedOn") Date endConfirmedOn);

    @Query(value = "SELECT DATEDIFF(MINUTE, ib.PA_CTD_ON, :lDate) from tblputawayheader ib \n"
            + "where ib.PACK_BARCODE = :packBarcode and ib.wh_id = :warehouseId and ib.c_id = :companyCode and ib.plant_Id = :plantId and ib.lang_Id = :languageId and ib.is_deleted = 0", nativeQuery = true)
    public String getLeadTimeByPackBarCode(@Param("companyCode") String companyCode,
                                           @Param("plantId") String plantId,
                                           @Param("languageId") String languageId,
                                           @Param("warehouseId") String warehouseId,
                                           @Param("packBarcode") String packBarcode,
                                           @Param("lDate") Date lDate);

    boolean existsByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndStatusIdAndDeletionIndicator(
            String companyCode, String languageId, String plantId, String warehouseId, String refDocNumber, String preInboundNo,
            String itemCode, String manufacturerName, Long lineNumber, Long statusId, Long deletionIndicator);

    boolean existsByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndBarcodeIdAndPutAwayNumberAndStatusIdAndDeletionIndicator(
            String companyCode, String languageId, String plantId, String warehouseId, String refDocNumber, String barcodeId, String putawayNo, Long statusId, Long deletionIndicator);

    List<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndBarcodeIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String refDocNumber, String barcodeId, Long deletionIndicator);

    boolean existsByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndBarcodeIdAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String barcodeId, Long deletionIndicator);

    boolean existsByCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String companyCode, String plantId, String warehouseId, String refDocNo, String preInboundNo, Long deletionIndicator
    );

    boolean existsByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndBarcodeIdAndRefDocNumberAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String barcodeId, String refDocNumber, Long deletionIndicator);

    @Query(value = "SELECT DATEDIFF(MINUTE, :assignedOnDate, :lDate) ", nativeQuery = true)
    public String getLeadTimeV3(@Param("assignedOnDate") Date assignedOnDate,
                                @Param("lDate") Date lDate);

    @Modifying
    @Query(value = "UPDATE tblputawayline set status_id = :statusId, status_text = :statusText where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber AND pre_ib_no = :preInboundNo " +
            "AND itm_code = :itemCode AND ib_line_no = :lineNo and is_deleted = 0", nativeQuery = true)
    void updatePutAwayLinePGI(@Param("companyCodeId") String companyCodeId,
                         @Param("plantId") String plantId,
                         @Param("warehouseId") String warehouseId,
                         @Param("refDocNumber") String refDocNumber,
                         @Param("preInboundNo") String preInbound,
                         @Param("itemCode") String itemCode,
                         @Param("lineNo") Long lineNo,
                         @Param("statusId") Long statusId,
                         @Param("statusText") String statusText);
}