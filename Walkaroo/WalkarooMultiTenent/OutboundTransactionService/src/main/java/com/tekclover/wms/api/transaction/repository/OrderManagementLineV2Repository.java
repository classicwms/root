package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;

@Repository
@Transactional
public interface OrderManagementLineV2Repository extends JpaRepository<OrderManagementLineV2, Long>,
        JpaSpecificationExecutor<OrderManagementLineV2>,
        StreamableJpaSpecificationRepository<OrderManagementLineV2> {

    OrderManagementLineV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber,
            String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode, Long deletionIndicator);

    List<OrderManagementLineV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber,
            String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode, Long deletionIndicator);

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, Long lineNumber, String itemCode, Long deletionIndicator);

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

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndSalesOrderNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String salesOrderNumber, Long deletionIndicator);

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
    
    OrderManagementLineV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndLineNumberAndItemCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String refDocNumber, Long lineNumber, String itemCode, Long deletionIndicator);

    OrderManagementLineV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndBarcodeIdAndStatusIdNotInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String barcodeId, List<Long> statusIds, Long deletionIndicator);

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndItemCodeAndLineNumberAndStatusIdNotInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String itemCode, Long lineNumber, List<Long> statusIds, Long deletionIndicator);

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

    //====================================================Walkaroo-V3=============================================================

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, Long deletionIndicator);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OrderManagementLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription, ob.pickupUpdatedOn = :updatedOn, \r\n"
            + " ob.assignedPickerId = :assignedPickerId, ob.pickupNumber = :pickupNumber \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND \r\n "
            + " ob.itemCode = :itemCode AND ob.preOutboundNo = :preOutboundNo AND ob.lineNumber = :lineNumber")
    void updateOrderManagementLineV3(@Param("companyCodeId") String companyCodeId,
                                     @Param("plantId") String plantId,
                                     @Param("languageId") String languageId,
                                     @Param("warehouseId") String warehouseId,
                                     @Param("preOutboundNo") String preOutboundNo,
                                     @Param("lineNumber") Long lineNumber,
                                     @Param("itemCode") String itemCode,
                                     @Param("statusId") Long statusId,
                                     @Param("statusDescription") String statusDescription,
                                     @Param("assignedPickerId") String assignedPickerId,
                                     @Param("pickupNumber") String pickupNumber,
                                     @Param("updatedOn") Date updatedOn);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OrderManagementLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription, \n" +
//            "ob.assignedPickerId = :assignedPickerId, \n" +
            "ob.pickupNumber = :pickupNumber, ob.pickupUpdatedOn = :pickupUpdatedOn \n" +
            "WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId \n" +
            "AND ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo AND ob.lineNumber = :lineNumber AND ob.itemCode = :itemCode")
    void updateOrderManagementLineV3(@Param("companyCodeId") String companyCodeId,
                                     @Param("plantId") String plantId,
                                     @Param("languageId") String languageId,
                                     @Param("warehouseId") String warehouseId,
                                     @Param("refDocNumber") String refDocNumber,
                                     @Param("preOutboundNo") String preOutboundNo,
                                     @Param("pickupNumber") String pickupNumber,
//                                     @Param("assignedPickerId") String assignedPickerId,
                                     @Param("lineNumber") Long lineNumber,
                                     @Param("itemCode") String itemCode,
                                     @Param("statusId") Long statusId,
                                     @Param("statusDescription") String statusDescription,
                                     @Param("pickupUpdatedOn") Date pickupUpdatedOn);

    boolean existsByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndStatusIdInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo, List<Long> statusId,  Long deletionIndicator);

    boolean existsByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndLineNumberAndItemCodeAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, Long lineNumber, String itemCode, Long statusId, Long deletionIndicator);

    boolean existsByBarcodeIdAndStatusIdInAndDeletionIndicator(String barcodeId, List<Long> statusId, Long deletionIndicator);

    OrderManagementLineV2 findTopByPlantIdAndCompanyCodeIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndLineNumberAndItemCodeAndBarcodeIdAndDeletionIndicator(
            String plantId, String companyCodeId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, Long lineNumber, String itemCode, String barcodeId, Long deletionIndicator);

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndStatusIdInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo, List<Long> statusId,  Long deletionIndicator);

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndSalesOrderNumberAndCustomerIdAndStatusIdNotAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String salesOrderNumber, String customerId, Long statusId, Long deletionIndicator);

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndSalesOrderNumberAndShipToPartyAndStatusIdNotAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String salesOrderNumber, String shipToParty, Long statusId, Long deletionIndicator);

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndCustomerIdAndStatusIdNotAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String customerId, Long statusId, Long deletionIndicator);

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndLineNumberAndItemCodeAndBarcodeIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo,
            Long lineNumber, String itemCode, String barcodeId, Long deletionIndicator);

    @Query(value = "SELECT CASE WHEN \n" +
            "(select sum(ord_qty) from tblordermangementline where c_id = :companyCodeId and plant_id = :plantId \n" +
            " and lang_id = :languageId and wh_id = :warehouseId and REF_DOC_NO = :refDocNumber and OB_LINE_NO = :lineNumber and ITM_CODE = :itemCode and is_deleted = 0) = \n" +
            "(select sum(alloc_qty) from tblordermangementline where c_id = :companyCodeId and plant_id = :plantId and \n" +
            "lang_id = :languageId and wh_id = :warehouseId and REF_DOC_NO = :refDocNumber and OB_LINE_NO = :lineNumber and ITM_CODE = :itemCode and is_deleted = 0) \n" +
            "THEN 1 else 0 END as Result ", nativeQuery = true)
    public Long checkOrderManagementLineCreate(@Param("companyCodeId") String companyCodeId,
                                               @Param("plantId") String plantId,
                                               @Param("languageId") String languageId,
                                               @Param("warehouseId") String warehouseId,
                                               @Param("refDocNumber") String refDocNumber,
                                               @Param("itemCode") String itemCode,
                                               @Param("lineNumber") Long lineNumber);

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, Long deletionIndicator);
 
    
    //============================PGIReversal================================================================
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE tblordermangementline SET STATUS_ID = :statusId, REF_FIELD_10 = :statusDescription, STATUS_TEXT = :statusDescription \n" +
            "WHERE LANG_ID = :languageId AND C_ID = :companyCodeId AND \n" +
            "PLANT_ID = :plantId AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND ITM_CODE = :itemCode", nativeQuery = true)
    public void updateOrderManagementLineStatusV3(@Param("companyCodeId") String companyCodeId,
                                         @Param("plantId") String plantId,
                                         @Param("languageId") String languageId,
                                         @Param("warehouseId") String warehouseId,
                                         @Param("refDocNumber") String refDocNumber,
                                         @Param("itemCode") String itemCode,
                                         @Param("statusId") Long statusId,
                                         @Param("statusDescription") String statusDescription);
    
    @Modifying
    @Query(value = "UPDATE tblordermangementline SET is_deleted = 1 where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber AND pre_ob_no = :preOutboundNo " +
            "AND is_deleted = 0", nativeQuery = true)
    void deleteOrderManagementLine (@Param("companyCodeId") String companyCodeId,
                                   @Param("plantId") String plantId,
                                   @Param("warehouseId") String warehouseId,
                                   @Param("refDocNumber") String refDocNumber,
                                   @Param("preOutboundNo") String preOutboundNo);

    @Modifying
    @Query(value = "DELETE tblordermangementline where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber " +
            "AND is_deleted = 0", nativeQuery = true)
    void deleteOrderManagementLineV2 (@Param("companyCodeId") String companyCodeId,
                                    @Param("plantId") String plantId,
                                    @Param("warehouseId") String warehouseId,
                                    @Param("refDocNumber") String refDocNumber);

    @Modifying
    @Transactional
    @Query(value = "update tblordermangementline set SALES_ORDER_NUMBER = :salesOrderNo where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber " +
            "AND is_deleted = 0", nativeQuery = true)
    int updateSalesOrderNo(@Param("companyCodeId") String companyCodeId,
                            @Param("plantId") String plantId,
                            @Param("warehouseId") String warehouseId,
                            @Param("refDocNumber") String refDocNumber,
                            @Param("salesOrderNo") String salesOrderNo);

    boolean existsByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndBarcodeIdAndDeletionIndicator(
            String companyCodeId, String plantId, String langugeId, String warehouseId, String refDocNumber, String barcodeId, Long deletionIndicator);

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndStatusIdInAndDeletionIndicatorOrderByPickupCreatedOnAsc(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, List<Long> statusIds, Long deletionIndicator);

}