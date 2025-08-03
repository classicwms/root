package com.tekclover.wms.api.outbound.transaction.repository;

import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.v2.OrderManagementLineImpl;
import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface OrderManagementLineV2Repository extends JpaRepository<OrderManagementLineV2, Long>,
        JpaSpecificationExecutor<OrderManagementLineV2>,
        StreamableJpaSpecificationRepository<OrderManagementLineV2> {



    OrderManagementLineV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber,
            String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode, Long deletionIndicator);



    OrderManagementLineV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndBarcodeIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber,
            String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode, String barcodeId, Long deletionIndicator);

    List<OrderManagementLineV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber,
            String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode, Long deletionIndicator);

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, Long lineNumber, String itemCode, Long deletionIndicator);


//    @Query(value = "SELECT * FROM tblordermangementline WHERE C_ID = :companyCodeId " +
//            "AND PLANT_ID = :plantId " +
//            "AND LANG_ID = :languageId " +
//            "AND WH_ID = :warehouseId " +
//            "AND PRE_OB_NO = :preOutboundNo " +
//            "AND REF_DOC_NO = :refDocNumber " +
//            "AND PARTNER_CODE = :partnerCode " +
//            "AND OB_LINE_NO = :lineNumber " +
//            "AND ITM_CODE = :itemCode " +
//            "AND IS_DELETED = :deletionIndicator", nativeQuery = true)
//    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
//            @Param("companyCodeId") String companyCodeId, @Param("plantId") String plantId, @Param("languageId") String languageId, @Param("warehouseId") String warehouseId,
//            @Param("preOutboundNo") String preOutboundNo, @Param("refDocNumber") String refDocNumber, @Param("partnerCode") String partnerCode,
//            @Param("lineNumber") Long lineNumber, @Param("itemCode") String itemCode, @Param("deletionIndicator") Long deletionIndicator);


    List<OrderManagementLineV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, Long lineNumber, String itemCode, Long deletionIndicator);

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndLineNumberAndItemCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            Long lineNumber, String itemCode, Long deletionIndicator);

    @Query("Select count(ob) from OrderManagementLine ob where ob.companyCodeId=:companyCodeId and ob.plantId=:plantId and ob.languageId=:languageId and ob.warehouseId=:warehouseId and ob.refDocNumber=:refDocNumber \r\n"
            + " and ob.preOutboundNo=:preOutboundNo and ob.statusId in :statusId and ob.deletionIndicator=:deletionIndicator")
    public long getByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAndRefDocNumberAndPreOutboundNoAndStatusIdInAndDeletionIndicator(
            @Param("companyCodeId") String companyCodeId, @Param("plantId") String plantId, @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId, @Param("refDocNumber") String refDocNumber, @Param("preOutboundNo") String preOutboundNo,
            @Param("statusId") List<Long> statusId, @Param("deletionIndicator") Long deletionIndicator);

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdIn(
            String companyCodeId, String plantId, String languageId, String warehouseId, List<Long> list);

    OrderManagementLineV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndLineNumberAndItemCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String preOutboundNo, String refDocNumber, Long lineNumber, String itemCode, Long deletionIndicator);

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber, Long deletionIndicator);

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo, Long deletionIndicator);

    List<OrderManagementLineV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, Long deletionIndicator);

    List<OrderManagementLineV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndStatusIdInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode,
            String manufacturerName, List<Long> statusIdList, Long deletionIndicator);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE OrderManagementLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription \n" +
            "WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId \n" +
            "AND ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo")
    void updateOrderManagementLineStatus(@Param("companyCodeId") String companyCodeId,
                                         @Param("plantId") String plantId,
                                         @Param("languageId") String languageId,
                                         @Param("warehouseId") String warehouseId,
                                         @Param("refDocNumber") String refDocNumber,
                                         @Param("preOutboundNo") String preOutboundNo,
                                         @Param("statusId") Long statusId,
                                         @Param("statusDescription") String statusDescription);

    OrderManagementLineV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndLineNumberAndItemCodeAndManufacturerNameAndProposedStorageBinAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, Long lineNumber, String itemCode, String manufacturerName, String storageBin, Long deletionIndicator);

    List<OrderManagementLineV2> findByPlantIdAndCompanyCodeIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndLineNumberAndItemCodeAndDeletionIndicator(
            String plantId, String companyCodeId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, Long lineNumber, String itemCode, Long deletionIndicator);

    OrderManagementLineV2 findByPlantIdAndCompanyCodeIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndBarcodeIdAndItemCodeAndDeletionIndicator(
            String plantId, String companyCodeId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String barcodeId, String itemCode, Long deletionIndicator);

    @Query(value = "select \n" +
            "case when exists( \n" +
            "            select count(ob_line_no) lines from tblordermangementline where \n" +
            "            ref_doc_no = :refDocNumber and c_id = :companyCodeId and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId and is_deleted = 0 \n" +
            "            intersect \n" +
            "            select count(ob_line_no) noStkLines from tblordermangementline where status_id=47 and \n" +
            "            ref_doc_no = :refDocNumber and c_id = :companyCodeId and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId and is_deleted = 0 \n" +
            "           )\n" +
            "     then 'true' \n" +
            "     else 'false'\n" +
            "end;", nativeQuery = true)
    public boolean getNoStockStatusOrderManagementLine(@Param("companyCodeId") String companyCodeId,
                                                       @Param("plantId") String plantId,
                                                       @Param("languageId") String languageId,
                                                       @Param("warehouseId") String warehouseId,
                                                       @Param("refDocNumber") String refDocumentNo);

    @Transactional
    @Procedure(procedureName = "nostock_status_update_new_proc")
    public void updateNostockStatusUpdateProc(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preOutboundNo") String preOutboundNo,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OrderManagementLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription, ob.pickupUpdatedOn = :updatedOn, \r\n"
            + " ob.assignedPickerId = :assignedPickerId, ob.pickupNumber = :pickupNumber \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND \r\n "
            + " ob.partnerCode = :partnerCode AND ob.itemCode = :itemCode AND \r\n "
            + " ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo AND ob.lineNumber = :lineNumber")
    void updateOrderManagementLineV2(@Param("companyCodeId") String companyCodeId,
                                     @Param("plantId") String plantId,
                                     @Param("languageId") String languageId,
                                     @Param("warehouseId") String warehouseId,
                                     @Param("preOutboundNo") String preOutboundNo,
                                     @Param("refDocNumber") String refDocNumber,
                                     @Param("partnerCode") String partnerCode,
                                     @Param("lineNumber") Long lineNumber,
                                     @Param("itemCode") String itemCode,
                                     @Param("statusId") Long statusId,
                                     @Param("statusDescription") String statusDescription,
                                     @Param("assignedPickerId") String assignedPickerId,
                                     @Param("pickupNumber") String pickupNumber,
                                     @Param("updatedOn") Date updatedOn);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OrderManagementLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription, \r\n"
            + " ob.allocatedQty = 0, ob.deletionIndicator = :deletionIndicator, ob.pickupUpdatedBy = :pickupUpdatedBy, ob.pickupUpdatedOn = :pickupUpdatedOn \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND \r\n "
            + " ob.partnerCode = :partnerCode AND ob.itemCode = :itemCode AND \r\n "
            + " ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo AND ob.lineNumber = :lineNumber")
    void updateOrderManagementLineUnAllocateV2(@Param("companyCodeId") String companyCodeId,
                                               @Param("plantId") String plantId,
                                               @Param("languageId") String languageId,
                                               @Param("warehouseId") String warehouseId,
                                               @Param("preOutboundNo") String preOutboundNo,
                                               @Param("refDocNumber") String refDocNumber,
                                               @Param("partnerCode") String partnerCode,
                                               @Param("lineNumber") Long lineNumber,
                                               @Param("itemCode") String itemCode,
                                               @Param("statusId") Long statusId,
                                               @Param("statusDescription") String statusDescription,
                                               @Param("deletionIndicator") Long deletionIndicator,
                                               @Param("pickupUpdatedBy") String pickupUpdatedBy,
                                               @Param("pickupUpdatedOn") Date pickupUpdatedOn);

    List<OrderManagementLineV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNo, Long outboundOrderTypeId, Long deletionIndicator);

    @Transactional
    @Procedure(procedureName = "outbound_process_delete_proc")
    public void deleteOutboundProcessingProc(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("outboundOrderTypeId") Long outboundOrderTypeId);

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndItemCodeAndManufacturerNameAndLineNumberAndOutboundOrderTypeIdAndStatusIdNotInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo,
            String itemCode, String manufacturerName, Long lineNumber, Long orderTypeId, List<Long> statusIds, Long deletionIndicator);

    @Query(value =
            "WITH Table1_Count AS ( \n" +
            "    SELECT pre_ob_no,COUNT(ob_line_no) AS line_count \n" +
            "    FROM tblpreoutboundline where is_deleted = 0 GROUP BY pre_ob_no), \n" +
            "Table2_Count AS ( \n" +
            "   SELECT pre_ob_no, \n" +
            "   (select count(*) from (select COUNT(ob_line_no) cnt from tblordermangementline oml where oml.pre_ob_no = ol.pre_ob_no group by oml.ob_line_no) x) AS line_count \n" +
            "   FROM tblordermangementline ol where ol.is_deleted = 0 and " +
            "   (COALESCE(:statusId, null) IS NULL OR (ol.status_id IN (:statusId))) \n" +
            "   GROUP BY pre_ob_no) \n" +
            "SELECT t1.pre_ob_no into #orderId FROM tblordermangementline t1 \n" +
            "JOIN Table1_Count t1c ON t1.pre_ob_no = t1c.pre_ob_no \n" +
            "JOIN Table2_Count t2c ON t1c.pre_ob_no = t2c.pre_ob_no AND t1c.line_count = t2c.line_count \n" +
            "where t1.is_deleted = 0 and" +
            "(COALESCE(:statusId, null) IS NULL OR (t1.status_id IN (:statusId))) \n" +
            "group by t1.pre_ob_no \n"

            + "Select \n"
            + "C_ID companyCodeId, \n"
            + "PLANT_ID plantId, \n"
            + "LANG_ID languageId, \n"
            + "WH_ID warehouseId, \n"
            + "PRE_OB_NO preOutboundNo, \n"
            + "REF_DOC_NO refDocNumber, \n"
            + "PARTNER_CODE partnerCode, \n"
            + "OB_LINE_NO lineNumber, \n"
            + "ITM_CODE itemCode, \n"
            + "PROP_ST_BIN proposedStorageBin, \n"
            + "PROP_PACK_BARCODE proposedPackBarCode, \n"
            + "PU_NO pickupNumber, \n"
            + "VAR_ID variantCode, \n"
            + "VAR_SUB_ID variantSubCode, \n"
            + "OB_ORD_TYP_ID outboundOrderTypeId, \n"
            + "STATUS_ID statusId, \n"
            + "STCK_TYP_ID stockTypeId, \n"
            + "SP_ST_IND_ID specialStockIndicatorId, \n"
            + "ITEM_TEXT description, \n"
            + "MFR_PART manufacturerPartNo, \n"
            + "HSN_CODE hsnCode, \n"
            + "ITM_BARCODE itemBarcode, \n"
            + "ORD_QTY orderQty, \n"
            + "ORD_UOM orderUom, \n"
            + "INV_QTY inventoryQty, \n"
            + "ALLOC_QTY allocatedQty, \n"
            + "RE_ALLOC_QTY reAllocatedQty, \n"
            + "STR_TYP_ID  strategyTypeId, \n"
            + "ST_NO strategyNo, \n"
            + "REQ_DEL_DATE requiredDeliveryDate, \n"
            + "PROP_STR_NO proposedBatchSerialNumber, \n"
            + "PROP_PAL_CODE proposedPalletCode, \n"
            + "PROP_CASE_CODE proposedCaseCode, \n"
            + "PROP_HE_NO proposedHeNo, \n"
            + "PROP_PICKER_ID proposedPicker, \n"
            + "ASS_PICKER_ID assignedPickerId, \n"
            + "REASS_PICKER_ID reassignedPickerId, \n"
            + "IS_DELETED deletionIndicator, \n"
            + "REF_FIELD_1 referenceField1, \n"
            + "REF_FIELD_2 referenceField2, \n"
            + "REF_FIELD_3 referenceField3, \n"
            + "REF_FIELD_4 referenceField4, \n"
            + "REF_FIELD_5 referenceField5, \n"
            + "REF_FIELD_6 referenceField6, \n"
            + "REF_FIELD_7 referenceField7, \n"
            + "REF_FIELD_8 referenceField8, \n"
            + "REF_FIELD_9 referenceField9, \n"
            + "REF_FIELD_10 referenceField10, \n"
            + "RE_ALLOC_BY reAllocatedBy, \n"
            + "RE_ALLOC_ON reAllocatedOn, \n"
            + "PICK_UP_CTD_BY pickupCreatedBy, \n"
            + "PICK_UP_CTD_ON pickupCreatedOn, \n"
            + "PICK_UP_UTD_BY pickupUpdatedBy, \n"
            + "PICK_UP_UTD_ON pickupUpdatedOn, \n"
            + "PICKER_ASSIGN_BY pickerAssignedBy, \n"
            + "PICKER_ASSIGN_ON pickerAssignedOn, \n"
            + "PICKER_REASSIGN_BY pickerReassignedBy, \n"
            + "PICKER_REASSIGN_ON pickerReassignedOn, \n"
            + "MFR_CODE manufacturerCode, \n"
            + "MFR_NAME manufacturerName, \n"
            + "ORIGIN origin, \n"
            + "BRAND brand, \n"
            + "PARTNER_ITEM_BARCODE barcodeId, \n"
            + "LEVEL_ID levelId, \n"
            + "C_TEXT companyDescription, \n"
            + "PLANT_TEXT plantDescription, \n"
            + "WH_TEXT warehouseDescription, \n"
            + "STATUS_TEXT statusDescription, \n"
            + "MIDDLEWARE_ID middlewareId, \n"
            + "MIDDLEWARE_HEADER_ID middlewareHeaderId, \n"
            + "MIDDLEWARE_TABLE middlewareTable, \n"
            + "REF_DOC_TYPE referenceDocumentType, \n"
            + "SALES_INVOICE_NUMBER salesInvoiceNumber, \n"
            + "SUPPLIER_INVOICE_NO supplierInvoiceNo, \n"
            + "SALES_ORDER_NUMBER salesOrderNumber, \n"
            + "PICK_LIST_NUMBER pickListNumber, \n"
            + "TOKEN_NUMBER tokenNumber, \n"
            + "MANUFACTURER_FULL_NAME manufacturerFullName, \n"
            + "TRANSFER_ORDER_NO transferOrderNo, \n"
            + "RET_ORDER_NO returnOrderNo, \n"
            + "IS_COMPLETED isCompleted, \n"
            + "IS_CANCELLED isCancelled, \n"
            + "TARGET_BRANCH_CODE targetBranchCode, \n"
            + "IMS_SALE_TYP_CODE imsSaleTypeCode \n"
            + "from tblordermangementline where is_deleted = 0 and PRE_OB_NO in (select * from #orderId) and \n"
            + "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n"
            + "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n"
            + "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n"
            + "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n"
            + "(COALESCE(:refDocNo, null) IS NULL OR (ref_doc_no IN (:refDocNo))) and \n"
            + "(COALESCE(:preOutboundNo, null) IS NULL OR (pre_ob_no IN (:preOutboundNo))) and \n"
            + "(COALESCE(:partnerCode, null) IS NULL OR (partner_code IN (:partnerCode))) and \n"
            + "(COALESCE(:itemCode, null) IS NULL OR (ITM_CODE IN (:itemCode))) and \n"
            + "(COALESCE(:manufacturerName, null) IS NULL OR (MFR_NAME IN (:manufacturerName))) and \n"
            + "(COALESCE(:outboundOrderTypeId, null) IS NULL OR (ob_ord_typ_id IN (:outboundOrderTypeId))) and \n"
            + "(COALESCE(:statusId, null) IS NULL OR (status_id IN (:statusId))) and \n"
            + "(COALESCE(:description, null) IS NULL OR (ITEM_TEXT IN (:description))) and\n"
            + "(COALESCE(:soType, null) IS NULL OR (ref_field_1 IN (:soType))) and\n"
            + "(COALESCE(CONVERT(VARCHAR(255), :startRequiredDeliveryDate), null) IS NULL OR (REQ_DEL_DATE between COALESCE(CONVERT(VARCHAR(255), :startRequiredDeliveryDate), null) and COALESCE(CONVERT(VARCHAR(255), :endRequiredDeliveryDate), null))) and\n"
            + "(COALESCE(CONVERT(VARCHAR(255), :startOrderDate), null) IS NULL OR (PICK_UP_CTD_ON between COALESCE(CONVERT(VARCHAR(255), :startOrderDate), null) and COALESCE(CONVERT(VARCHAR(255), :endOrderDate), null)))", nativeQuery = true)
    public List<OrderManagementLineImpl> findOrderManagementLine(@Param("companyCodeId") List<String> companyCodeId,
                                                                 @Param("plantId") List<String> plantId,
                                                                 @Param("languageId") List<String> languageId,
                                                                 @Param("warehouseId") List<String> warehouseId,
                                                                 @Param("refDocNo") List<String> refDocNo,
                                                                 @Param("preOutboundNo") List<String> preOutboundNo,
                                                                 @Param("partnerCode") List<String> partnerCode,
                                                                 @Param("itemCode") List<String> itemCode,
                                                                 @Param("manufacturerName") List<String> manufacturerName,
                                                                 @Param("description") List<String> description,
                                                                 @Param("outboundOrderTypeId") List<Long> outboundOrderTypeId,
                                                                 @Param("statusId") List<Long> statusId,
                                                                 @Param("soType") List<String> soType,
                                                                 @Param("startRequiredDeliveryDate") Date startRequiredDeliveryDate,
                                                                 @Param("endRequiredDeliveryDate") Date endRequiredDeliveryDate,
                                                                 @Param("startOrderDate") Date startOrderDate,
                                                                 @Param("endOrderDate") Date endOrderDate);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OrderManagementLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription, ob.pickupUpdatedOn = :updatedOn, ob.pickupUpdatedBy = :pickupUpdatedBy, \r\n"
            + " ob.assignedPickerId = :assignedPickerId, ob.pickupNumber = :pickupNumber \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND \r\n "
            + " ob.partnerCode = :partnerCode AND ob.itemCode = :itemCode AND  ob.proposedStorageBin = :proposedStorageBin AND \r\n "   //26_02_2025_update uniqueOrderManagementLine stBin added
            + " ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo AND ob.lineNumber = :lineNumber")
    void updateOrderManagementLineV2(@Param("companyCodeId") String companyCodeId,
                                     @Param("plantId") String plantId,
                                     @Param("languageId") String languageId,
                                     @Param("warehouseId") String warehouseId,
                                     @Param("preOutboundNo") String preOutboundNo,
                                     @Param("refDocNumber") String refDocNumber,
                                     @Param("partnerCode") String partnerCode,
                                     @Param("lineNumber") Long lineNumber,
                                     @Param("itemCode") String itemCode,
                                     @Param("statusId") Long statusId,
                                     @Param("statusDescription") String statusDescription,
                                     @Param("assignedPickerId") String assignedPickerId,
                                     @Param("pickupNumber") String pickupNumber,
                                     @Param("pickupUpdatedBy") String pickupUpdatedBy,
                                     @Param("proposedStorageBin") String proposedStorageBin,        //26_02_2025_update uniqueOrderManagementLine stBin added
                                     @Param("updatedOn") Date updatedOn);


    @Query(value = "SELECT * FROM tblordermangementline WHERE " +
            "C_ID = ?1 AND PLANT_ID = ?2 AND LANG_ID = ?3 AND WH_ID = ?4 AND PRE_OB_NO = ?5 AND REF_DOC_NO = ?6 AND " +
            "PARTNER_CODE = ?7 AND OB_LINE_NO = ?8 AND ITM_CODE = ?9 AND PROP_ST_BIN = ?10 AND PROP_PACK_BARCODE = ?11 AND IS_DELETED = ?12",
            nativeQuery = true)
    OrderManagementLineV2 findMatchingLine(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber,
            String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode, Long deletionIndicator);


    @Modifying
    @Transactional
    @Query(value = "UPDATE tblordermangementline SET STATUS_ID = :statusId, PICK_UP_UTD_BY = :updatedBy, " +
            "PICK_UP_UTD_ON = :updatedOn, ASS_PICKER_ID = :userId, PU_NO = :pickupNo " +
            "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND " +
            "WH_ID = :warehouseId AND PRE_OB_NO = :preOutboundNo AND REF_DOC_NO = :refDocNumber AND " +
            "PARTNER_CODE = :partnerCode AND OB_LINE_NO = :lineNumber AND ITM_CODE = :itemCode AND " +
            "PROP_ST_BIN = :proposedStorageBin AND PROP_PACK_BARCODE = :proposedPackBarCode AND " +
            "IS_DELETED = :deletionIndicator", nativeQuery = true)
    void updateOrderManagementLine(@Param("statusId") Long statusId,
                                  @Param("updatedBy") String updatedBy,
                                  @Param("updatedOn") Date updatedOn,
                                  @Param("userId") String userId,
                                  @Param("pickupNo") String pickupNo,
                                  @Param("companyCodeId") String companyCodeId,
                                  @Param("plantId") String plantId,
                                  @Param("languageId") String languageId,
                                  @Param("warehouseId") String warehouseId,
                                  @Param("preOutboundNo") String preOutboundNo,
                                  @Param("refDocNumber") String refDocNumber,
                                  @Param("partnerCode") String partnerCode,
                                  @Param("lineNumber") Long lineNumber,
                                  @Param("itemCode") String itemCode,
                                  @Param("proposedStorageBin") String proposedStorageBin,
                                  @Param("proposedPackBarCode") String proposedPackBarCode,
                                  @Param("deletionIndicator") Long deletionIndicator);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tblordermangementline SET STATUS_ID = :statusId, PICK_UP_UTD_BY = :updatedBy, " +
            "PICK_UP_UTD_ON = :updatedOn, ASS_PICKER_ID = :userId, PU_NO = :pickupNo " +
            "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND " +
            "WH_ID = :warehouseId AND PRE_OB_NO = :preOutboundNo AND REF_DOC_NO = :refDocNumber AND " +
            "PARTNER_CODE = :partnerCode AND OB_LINE_NO = :lineNumber AND ITM_CODE = :itemCode AND " +
            "PROP_ST_BIN = :proposedStorageBin AND PROP_PACK_BARCODE = :proposedPackBarCode AND " +
            "IS_DELETED = :deletionIndicator AND partner_item_barcode = :barcodeId", nativeQuery = true)
    void updateOrderManagementLineV7(@Param("statusId") Long statusId,
                                   @Param("updatedBy") String updatedBy,
                                   @Param("updatedOn") Date updatedOn,
                                   @Param("userId") String userId,
                                   @Param("pickupNo") String pickupNo,
                                   @Param("companyCodeId") String companyCodeId,
                                   @Param("plantId") String plantId,
                                   @Param("languageId") String languageId,
                                   @Param("warehouseId") String warehouseId,
                                   @Param("preOutboundNo") String preOutboundNo,
                                   @Param("refDocNumber") String refDocNumber,
                                   @Param("partnerCode") String partnerCode,
                                   @Param("lineNumber") Long lineNumber,
                                   @Param("itemCode") String itemCode,
                                   @Param("proposedStorageBin") String proposedStorageBin,
                                   @Param("proposedPackBarCode") String proposedPackBarCode,
                                   @Param("deletionIndicator") Long deletionIndicator,
                                   @Param("barcodeId") String barcodeId);

    @Query(value = "SELECT * from tblordermangementline WHERE C_ID = :companyCode " +
            "AND PLANT_ID = :plantId AND WH_ID = :warehouseId AND LANG_ID = :languageId " +
            "AND REF_DOC_NO = :refDocNo AND PRE_OB_NO = :preOutboundNo AND IS_DELETED = 0 AND status_id = 48", nativeQuery = true)
    List<OrderManagementLineV2> getOrderManagementForPickup(@Param("companyCode") String companyCodeId,
                                                            @Param("plantId") String plantId,
                                                            @Param("warehouseId") String warehouseId,
                                                            @Param("languageId") String languageId,
                                                            @Param("refDocNo") String refDocNo,
                                                            @Param("preOutboundNo") String preOutboundNo);

    //---------------------------------------KNOWELL------------------------------------------------------//

    @Query(value = "SELECT * FROM tblordermangementline " +
            "WHERE c_id = :companyCodeId " +
            "AND plant_id = :plantId " +
            "AND lang_id = :languageId " +
            "AND wh_id = :warehouseId " +
            "AND pre_ob_no = :preOutboundNo " +
            "AND ref_doc_no = :refDocNumber " +
            "AND partner_code = :partnerCode " +
            "AND ob_line_no = :lineNumber " +
            "AND itm_code = :itemCode " +
            "AND prop_st_bin = :proposedStorageBin " +
            "AND prop_pack_barcode = :proposedPackCode " +
            "AND partner_item_barcode = :barcodeId " +
            "AND is_deleted = 0", nativeQuery = true)
    OrderManagementLineV2 findOrderManagementLineV7(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("preOutboundNo") String preOutboundNo,
            @Param("refDocNumber") String refDocNumber,
            @Param("partnerCode") String partnerCode,
            @Param("lineNumber") Long lineNumber,
            @Param("itemCode") String itemCode,
            @Param("proposedStorageBin") String proposedStorageBin,
            @Param("proposedPackCode") String proposedPackCode,
            @Param("barcodeId") String barcodeId
    );

    @Modifying
    @Query(value = "UPDATE tblordermangementline SET \n" +
            "PARTNER_ITEM_BARCODE = :barcodeId, PICK_UP_UTD_BY = :pickUpdatedBy, PICK_UP_UTD_ON = :pickUpdatedOn \n" +
            "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND WH_ID = :warehouseId \n" +
            "AND ITM_CODE = :itemCode AND MFR_NAME = :manufacturerName AND STATUS_ID IN (:statusId) \n" +
            "AND IS_DELETED = 0", nativeQuery = true)
    public void updateOrderManagementBarcodeId(@Param("companyCodeId") String companyCodeId,
                                            @Param("plantId") String plantId,
                                            @Param("warehouseId") String warehouseId,
                                            @Param("itemCode") String itemCode,
                                            @Param("manufacturerName") String manufacturerName,
                                            @Param("statusId") List<Long> statusId,
                                            @Param("barcodeId") String barcodeId,
                                            @Param("pickUpdatedBy") String pickUpdatedBy,
                                            @Param("pickUpdatedOn") Date pickUpdatedOn);

    @Modifying
    @Query(value = "DELETE tblordermangementline WHERE \n" +
            "C_ID = :companyCodeId AND PLANT_ID = :plantId AND WH_ID = :warehouseId AND PRE_OB_NO = :preOutboundNo \n" +
            "AND ITM_CODE = :itemCode AND MFR_NAME = :manufacturerName AND STATUS_ID IN (:statusId) \n" +
            "AND IS_DELETED = 0", nativeQuery = true)
    public void deleteOrderManagementBarcodeId(@Param("companyCodeId") String companyCodeId,
                                            @Param("plantId") String plantId,
                                            @Param("warehouseId") String warehouseId,
                                            @Param("itemCode") String itemCode,
                                            @Param("manufacturerName") String manufacturerName,
                                            @Param("statusId") List<Long> statusId,
                                            @Param("preOutboundNo") String preOutboundNo);

    //-------------------------------Fahaheel------------------------------------------------------//

    @Query(value = "SELECT STATUS_TEXT from tblstatusid where status_id = :statusId", nativeQuery = true)
    String getStatusDesc(@Param("statusId") Long statusId);

    void deleteByCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String refDocNumber, String preOutboundNo, Long deletionIndicator);

}