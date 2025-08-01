package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
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

    //====MT-Barcode get
    OrderManagementLineV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndItemCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String refDocNumber, String itemCode, Long deletionIndicator);
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

    //====================================================Namratha=============================================================

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicatorAndStatusIdNot(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber, Long deletionIndicator, Long statusId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OrderManagementLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription, ob.pickupUpdatedOn = :updatedOn, \r\n"
            + " ob.pickupNumber = :pickupNumber \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND \r\n "
            + " ob.itemCode = :itemCode AND ob.preOutboundNo = :preOutboundNo AND ob.lineNumber = :lineNumber")
    void updateOrderManagementLineV6(@Param("companyCodeId") String companyCodeId,
                                     @Param("plantId") String plantId,
                                     @Param("languageId") String languageId,
                                     @Param("warehouseId") String warehouseId,
                                     @Param("preOutboundNo") String preOutboundNo,
                                     @Param("lineNumber") Long lineNumber,
                                     @Param("itemCode") String itemCode,
                                     @Param("statusId") Long statusId,
                                     @Param("statusDescription") String statusDescription,
                                     @Param("pickupNumber") String pickupNumber,
                                     @Param("updatedOn") Date updatedOn);

    List<OrderManagementLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndStatusIdAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String itemCode, Long statusId, Long deletionIndicator);


//    List<OrderManagementLineV2> findByPlantIdAndCompanyCodeIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndLineNumberAndItemCodeAndBarcodeIdAndDeletionIndicator(
//            String plantId, String companyCodeId, String languageId, String warehouseId, String preOutboundNo,
//            String refDocNumber, Long lineNumber, String itemCode, String barcodeId, Long deletionIndicator);

    @Query(value = "select om.* from tblordermangementline om \n" +
            "where om.is_deleted =0 and om.status_id in (42,43,47,48) and EXISTS(select 1 from tblstagingline sl where sl.c_id = om.c_id and sl.plant_id = om.plant_id and \n" +
            "sl.lang_id = om.lang_id and sl.wh_id = om.wh_id and sl.itm_code= om.itm_code and sl.is_deleted = 0 and sl.CROSS_DOCK = 1 and sl.status_id in (14))", nativeQuery = true)
    List<OrderManagementLineV2> getOrderManagementLineV2();

    @Query(value = "SELECT TOP 1 * from tblordermangementline ob WHERE \n " +
            "ob.c_id = :companyCodeId AND ob.plant_id = :plantId AND ob.lang_id = :languageId AND ob.wh_id = :warehouseId AND \n " +
            "ob.itm_code = :itemCode AND ob.status_id in (42, 43, 47, 48) AND is_deleted = 0", nativeQuery = true)
    Optional<OrderManagementLineV2> getOrderManagementLineForCrossDock(@Param("companyCodeId") String companyCodeId,
                                                                       @Param("plantId") String plantId,
                                                                       @Param("languageId") String languageId,
                                                                       @Param("warehouseId") String warehouseId,
                                                                       @Param("itemCode") String itemCode);

}