package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.impl.StockMovementReportImpl;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.transaction.model.report.PickingProductivityImpl;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;

@Repository
@Transactional
public interface PickupLineV2Repository extends JpaRepository<PickupLineV2, Long>,
        JpaSpecificationExecutor<PickupLineV2>, StreamableJpaSpecificationRepository<PickupLineV2> {

    @Query(value = "SELECT SUM(PICK_CNF_QTY) FROM tblpickupline WHERE \n"
            + "C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND \r\n"
            + "WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND\r\n"
            + "PRE_OB_NO = :preOutboundNo AND OB_LINE_NO = :obLineNumber AND ITM_CODE = :itemCode AND IS_DELETED = 0 \r\n"
            + "GROUP BY REF_DOC_NO", nativeQuery = true)
    public Double getPickupLineCountV2(@Param("companyCodeId") String companyCodeId,
                                       @Param("plantId") String plantId,
                                       @Param("languageId") String languageId,
                                       @Param("warehouseId") String warehouseId,
                                       @Param("refDocNumber") String refDocNumber,
                                       @Param("preOutboundNo") String preOutboundNo,
                                       @Param("obLineNumber") Long obLineNumber,
                                       @Param("itemCode") String itemCode);

    @Query(value = "SELECT COUNT(OB_LINE_NO) AS lineCount FROM tblpickupline WHERE \r\n"
            + " C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND \r\n"
            + "	WH_ID = :warehouseId AND REF_DOC_NO IN :refDocNumber AND PRE_OB_NO = :preOutboundNo\r\n"
            + "	AND STATUS_ID=50 AND IS_DELETED = 0 GROUP BY REF_DOC_NO", nativeQuery = true)
    public Double getCountByWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicatorV2(@Param("companyCodeId") String companyCodeId,
                                                                                             @Param("plantId") String plantId,
                                                                                             @Param("languageId") String languageId,
                                                                                             @Param("warehouseId") String warehouseId,
                                                                                             @Param("preOutboundNo") String preOutboundNo,
                                                                                             @Param("refDocNumber") List<String> refDocNumber);

    @Query(value = "select top 1 ass_picker_id from ( \n" +
            "select count(ass_picker_id) pickerassignedCount,ass_picker_id from tblpickupline where \n" +
            "ass_picker_id in (:assignPickerId) and \n" +
            "is_deleted = 0 and \n" +
            "c_id in (:companyCodeId) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
            "wh_id in (:warehouseId) \n" +
            "group by ass_picker_id ) x order by pickerassignedCount", nativeQuery = true)
    public String getHHTUser(
            @Param("assignPickerId") List<String> assignPickerId,
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId);

    PickupLineV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, Long lineNumber, String itemCode, Long deletionIndicator);

    List<PickupLineV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, Long lineNumber, String itemCode, Long deletionIndicator);

    PickupLineV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndActualHeNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, Long lineNumber, String pickupNumber, String itemCode,
            String pickedStorageBin, String pickedPackCode, String actualHeNo, Long deletionIndicator);

    List<PickupLineV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberInAndItemCodeInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, List<Long> lineNumbers, List<String> itemCodes, Long deletionIndicator);

    List<PickupLineV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoInAndRefDocNumberInAndPartnerCodeAndStatusIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, List<String> preOutboundNo,
            List<String> refDocNumber, String partnerCode, Long statusId, Long deletionIndicator);

    List<PickupLineV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoInAndRefDocNumberInAndOutboundOrderTypeIdAndStatusIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, List<String> preOutboundNo,
            List<String> refDocNumber, Long outboundOrderTypeId, Long statusId, Long deletionIndicator);

    PickupLineV2 findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndActualHeNoAndPickedStorageBinAndPickedPackCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, Long lineNumber, String pickupNumber, String itemCode,
            String actualHeNo, String pickedStorageBin, String pickedPackCode, Long deletionIndicator);
    
    List<PickupLineV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, Long lineNumber, String pickupNumber, String itemCode,
            String pickedStorageBin, String pickedPackCode, Long deletionIndicator);

    PickupLineV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndActualHeNoAndPickupNumberAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String actualHeNo,
            String pickupNumber, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
            String itemCode, Long deletionIndicator);

    List<PickupLineV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndAssignedPickerIdInAndDeletionIndicatorOrderByPickupCreatedOn(
            String companyCodeId, String plantId, String languageId, String warehouseId, Long StatusId, List<String> assignedPickerId, Long deletionIndicator );

//    List<PickupLineV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPickedStorageBinAndStatusIdAndPickupCreatedOnBetweenAndDeletionIndicator(
//            String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode,
//            String manufacturerName, String storageBin, Long statusId, Date stockCountDate, Date date, Long deletionIndicator);
    
    List<PickupLineV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPickedStorageBinAndStatusIdAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode,
            String manufacturerName, String storageBin, Long statusId, Long deletionIndicator);

    PickupLineV2 findByPickupNumber(String pickupNumber);

    List<PickupLineV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndAssignedPickerIdInAndDeletionIndicatorOrderByPickupConfirmedOn(
            String companyCodeId, String plantId, String languageId, String warehouseId, Long StatusId, List<String> assignedPickerId, Long deletionIndicator);

    List<PickupLineV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicatorOrderByPickupConfirmedOnDesc(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, Long deletionIndicator);

    List<PickupLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    List<PickupLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo, Long deletionIndicator);

    List<PickupLineV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndManufacturerNameAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, Long lineNumber, String itemCode, String manufacturerName, Long deletionIndicator);

    @Query(value = "select ol.wh_id as warehouseId,ol.c_id as companyCodeId,ol.plant_id as plantId,ol.lang_id as languageId, ol.itm_code as itemCode , \n" +
            " ol.wh_text as warehouseDescription,ol.c_text as companyDescription,ol.plant_text as plantDescription,ol.status_text as statusDescription,\n" +
            " 'OutBound' as documentType , ol.ref_doc_no as documentNumber, ol.partner_code as customerCode,\n" +
            " ol.PICK_CNF_ON as confirmedOn, ol.pick_cnf_qty as movementQty, ol.item_text as itemText,ol.mfr_name as manufacturerSKU \n" +
            " from tblpickupline ol\n" +
            " WHERE ol.ITM_CODE in (:itemCode) and ol.is_deleted = 0 and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (ol.MFR_NAME IN (:manufacturerName))) \n" +
            " AND ol.C_ID in (:companyCodeId) AND ol.PLANT_ID in (:plantId) AND ol.LANG_ID in (:languageId) AND ol.WH_ID in (:warehouseId) AND ol.status_id = :statusId \n" +
            " AND ol.PICK_CNF_ON between :fromDate and :toDate ", nativeQuery = true)
    public List<StockMovementReportImpl> findPickupLineForStockMovement(@Param("itemCode") List<String> itemCode,
                                                                        @Param("manufacturerName") List<String> manufacturerName,
                                                                        @Param("warehouseId") List<String> warehouseId,
                                                                        @Param("companyCodeId") List<String> companyCodeId,
                                                                        @Param("plantId") List<String> plantId,
                                                                        @Param("languageId") List<String> languageId,
                                                                        @Param("statusId") Long statusId,
                                                                        @Param("fromDate") Date fromDate,
                                                                        @Param("toDate") Date toDate);

    List<PickupLineV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPickedStorageBinAndStatusIdAndPickupCreatedOnBetweenAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode, String manufacturerName,
            String storageBin, Long statusId, Date stockCountDate, Date date, Long deletionIndicator);

    @Query(value = "SELECT DATEDIFF(MINUTE, ib.PICK_CTD_ON, :lDate) from tblpickupheader ib \n"
            + "where ib.pu_no = :pickupNumber and ib.wh_id = :warehouseId and ib.PARTNER_ITEM_BARCODE = :barcodeId and \n"
            + "ib.c_id = :companyCodeId and ib.plant_Id = :plantId and ib.lang_Id = :languageId and ib.is_deleted = 0", nativeQuery = true)
    public String getleadtime(@Param("companyCodeId") String companyCodeId,
                              @Param("plantId") String plantId,
                              @Param("languageId") String languageId,
                              @Param("warehouseId") String warehouseId,
                              @Param("pickupNumber") String pickupNumber,
                              @Param("barcodeId") String barcodeId,
                              @Param("lDate") Date lDate);

    List<PickupLineV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndAssignedPickerIdAndDeletionIndicatorAndPickupConfirmedOnBetweenOrderByPickupConfirmedOn(
            String companyCodeId, String plantId, String languageId, String warehouseId, Long StatusId,
            String assignedPickerId, Long deletionIndicator, Date startTime, Date endTime);

    List<PickupLineV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String itemCode, String manufacturerName, Long statusId, Long deletionIndicator);

    List<PickupLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndManufacturerNameAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, Long lineNumber, String itemCode, String manufacturerName, Long deletionIndicator);

    List<PickupLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndItemCodeAndManufacturerNameAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
            String itemCode, String manufacturerName, Long deletionIndicator);

    PickupLineV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndAssignedPickerIdInAndDeletionIndicatorAndPickupConfirmedOnBetweenOrderByPickupConfirmedOn(
            String companyCodeId, String plantId, String languageId, String warehouseId, Long StatusId,
            List<String> assignedPickerId, Long deletionIndicator, Date startTime, Date endTime);

    @Query(value = "SELECT SUM(PICK_CNF_QTY) FROM tblpickupline WHERE \r\n"
            + "C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND \r\n"
            + "WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PU_NO = :pickupNumber AND STATUS_ID = :statusId AND\r\n"
            + "PRE_OB_NO = :preOutboundNo AND MFR_NAME = :manufacturerName AND ITM_CODE = :itemCode AND IS_DELETED = 0 \r\n"
            + "GROUP BY ITM_CODE, MFR_NAME, PU_NO, REF_DOC_NO, LANG_ID, PLANT_ID, WH_ID, C_ID", nativeQuery = true)
    public Double getPickupLineSumV2(@Param("companyCodeId") String companyCodeId,
                                     @Param("plantId") String plantId,
                                     @Param("languageId") String languageId,
                                     @Param("warehouseId") String warehouseId,
                                     @Param("refDocNumber") String refDocNumber,
                                     @Param("preOutboundNo") String preOutboundNo,
                                     @Param("pickupNumber") String pickupNumber,
                                     @Param("statusId") Long statusId,
                                     @Param("itemCode") String itemCode,
                                     @Param("manufacturerName") String manufacturerName);

    PickupLineV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicatorOrderByPickupConfirmedOnDesc(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, Long deletionIndicator);

    PickupLineV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicatorAndPickedStorageBinNotOrderByPickupConfirmedOnDesc(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, Long deletionIndicator, String storageBin);

    @Query(value = "select \n" +
            "sum(datediff(MINUTE,pick_ctd_on,pick_cnf_on)) leadTime, \n" +
            "round((sum(pick_cnf_qty)/NULLIF(sum(datediff(MINUTE,pick_ctd_on,pick_cnf_on)),0)*60),2) partsPerHr, \n" +
            "round(sum(datediff(MINUTE,pick_ctd_on,pick_cnf_on))/NULLIF(sum(pick_cnf_qty),0),2) avgLeadTime, \n" +
            "sum(pick_cnf_qty) parts, \n" +
            "count(pre_ob_no) orders, \n" +
            "c_id companyCodeId, \n" +
            "plant_id plantId, \n" +
            "lang_id languageId, \n" +
            "wh_id warehouseId, \n" +
            "c_text companyDescription, \n" +
            "plant_text plantDescription, \n" +
            "wh_text warehouseDescription, \n" +
            "ass_picker_id assignedPickerId \n" +
            "into #tpl \n" +
            "from tblpickupline where is_deleted = 0 and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:assignedPickerId, null) IS NULL OR (ass_picker_id IN (:assignedPickerId))) and \n" +
            "(COALESCE(:levelId, null) IS NULL OR (level_id IN (:levelId))) and \n" +
            "(COALESCE(:refDocNo, null) IS NULL OR (ref_doc_no IN (:refDocNo))) and \n" +
            "(COALESCE(:preOutboundNo, null) IS NULL OR (pre_ob_no IN (:preOutboundNo))) and \n" +
            "(COALESCE(:outboundOrderTypeId, null) IS NULL OR (ob_ord_typ_id IN (:outboundOrderTypeId))) and \n" +
            "(COALESCE(:statusId, null) IS NULL OR (status_id IN (:statusId))) and \n" +
            "(COALESCE(CONVERT(VARCHAR(255), :startConfirmedOn), null) IS NULL OR (PICK_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :startConfirmedOn), null) and COALESCE(CONVERT(VARCHAR(255), :endConfirmedOn), null)))\n"+
            "group by ass_picker_id,plant_id,wh_id,c_id,lang_id,plant_text,wh_text,c_text \n"+

            "select *, \n" +
            "(select sum(partsPerHr) from #tpl) totalPartsPerHr, \n" +
            "(select sum(avgLeadTime) from #tpl) totalLeadTime, \n" +
            "(select avg(avgLeadTime) from #tpl) avgTotalLeadTime, \n" +
            "(select sum(parts) from #tpl) totalParts, \n" +
            "(select sum(orders) from #tpl) totalOrders \n" +
            "from #tpl", nativeQuery = true)
    List<PickingProductivityImpl> findPickingProductivityReport(@Param(value = "companyCodeId") List<String> companyCodeId,
                                                                @Param(value = "plantId") List<String> plantId,
                                                                @Param(value = "languageId") List<String> languageId,
                                                                @Param(value = "warehouseId") List<String> warehouseId,
                                                                @Param(value = "refDocNo") List<String> refDocNo,
                                                                @Param(value = "preOutboundNo") List<String> preOutboundNo,
                                                                @Param(value = "assignedPickerId") List<String> assignedPickerId,
                                                                @Param(value = "levelId") List<Long> levelId,
                                                                @Param(value = "outboundOrderTypeId") List<Long> outboundOrderTypeId,
                                                                @Param(value = "statusId") List<Long> statusId,
                                                                @Param(value = "startConfirmedOn") Date startConfirmedOn,
                                                                @Param(value = "endConfirmedOn") Date endConfirmedOn);

    // V3 - WALKAROO Changes
	public List<PickupLineV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndItemCodeAndDeletionIndicator(
			String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
			String refDocNumber, String partnerCode, String itemCode, long l);

	public List<PickupLineV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndBarcodeIdAndDeletionIndicator(
			String languageId, String companyCodeId, String plantId, String warehouseId, String preOutboundNo,
			String refDocNumber, String partnerCode, Long lineNumber, String pickupNumber, String itemCode,
			String pickedStorageBin, String pickedPackCode, String barcodeId, long l);

    PickupLineV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndBarcodeIdAndStatusIdNotAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String barcodeId, Long statusId, Long deletionIndicator);

    List<PickupLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndItemCodeAndLineNumberAndStatusIdNotAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String itemCode, Long lineNumber, Long statusId, Long deletionIndicator);

    PickupLineV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndItemCodeAndBarcodeIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String itemCode, String barcodeId, Long deletionIndicator);

    @Query(value = "SELECT MAX(ass_picker_id) assign, CONVERT(DATE, PICK_CNF_ON) as pickDate, SUM(CAST(ref_field_1 AS INT)) leadTime \n" +
            "FROM tblpickupline \n" +
            "WHERE is_deleted = 0 AND \n" +
            "(COALESCE(:startConfirmedOn, null) IS NULL OR (PICK_CNF_ON between :startConfirmedOn and :endConfirmedOn)) \n" +
            "GROUP BY CONVERT(DATE, PICK_CNF_ON), ass_picker_id", nativeQuery = true)
    List<Object[]> getLeadTime(@Param(value = "startConfirmedOn") Date startConfirmedOn,
                               @Param(value = "endConfirmedOn") Date endConfirmedOn);

    @Query(value = "SELECT MAX(ass_picker_id) assign, CONVERT(DATE, PICK_CNF_ON) as pickDate, SUM(PICK_CNF_QTY) quantity \n" +
            "FROM tblpickupline \n" +
            "WHERE is_deleted = 0 AND \n" +
            "(COALESCE(:startConfirmedOn, null) IS NULL OR (PICK_CNF_ON between :startConfirmedOn and :endConfirmedOn)) \n" +
            "GROUP BY CONVERT(DATE, PICK_CNF_ON), ass_picker_id", nativeQuery = true)
    List<Object[]> getQuantity(@Param(value = "startConfirmedOn") Date startConfirmedOn,
                               @Param(value = "endConfirmedOn") Date endConfirmedOn);

    @Query(value = "SELECT MAX(ass_picker_id) assign, CONVERT(DATE, PICK_CNF_ON) as pickDate, AVG(CAST(ref_field_1 AS INT)) leadTime \n" +
            "FROM tblpickupline \n" +
            "WHERE is_deleted = 0 AND \n" +
            "(COALESCE(:startConfirmedOn, null) IS NULL OR (PICK_CNF_ON between :startConfirmedOn and :endConfirmedOn)) \n" +
            "GROUP BY CONVERT(DATE, PICK_CNF_ON), ass_picker_id", nativeQuery = true)
    List<Object[]> getAverageLeadTime(@Param(value = "startConfirmedOn") Date startConfirmedOn,
                               @Param(value = "endConfirmedOn") Date endConfirmedOn);

    @Query(value = "SELECT MAX(ass_picker_id) assign, CONVERT(DATE, PICK_CNF_ON) as pickDate, " +
            "CASE WHEN SUM(CAST(ref_field_1 AS INT)) != 0 THEN ((SUM(PICK_CNF_QTY) / SUM(CAST(ref_field_1 AS INT))) * 60) " +
            "ELSE 0 END as leadTime " +
            "FROM tblpickupline \n" +
            "WHERE is_deleted = 0 AND \n" +
            "(COALESCE(:startConfirmedOn, null) IS NULL OR (PICK_CNF_ON between :startConfirmedOn and :endConfirmedOn)) \n" +
            "GROUP BY CONVERT(DATE, PICK_CNF_ON), ass_picker_id", nativeQuery = true)
    List<Object[]> getProductivity(@Param(value = "startConfirmedOn") Date startConfirmedOn,
                                   @Param(value = "endConfirmedOn") Date endConfirmedOn);
    
    //============================PGIReversal================================================================
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE tblpickupline SET STATUS_ID = :statusId, REF_FIELD_10 = :statusDescription, STATUS_TEXT = :statusDescription \n" +
            "WHERE LANG_ID = :languageId AND C_ID = :companyCodeId AND \n" +
            "PLANT_ID = :plantId AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND ITM_CODE = :itemCode", nativeQuery = true)
    void updatePickupLineStatusV3(@Param("companyCodeId") String companyCodeId,
                                         @Param("plantId") String plantId,
                                         @Param("languageId") String languageId,
                                         @Param("warehouseId") String warehouseId,
                                         @Param("refDocNumber") String refDocNumber,
                                         @Param("itemCode") String itemCode,
                                         @Param("statusId") Long statusId,
                                         @Param("statusDescription") String statusDescription);

    boolean existsByCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String refDocNumber, Long deletionIndicator);
}