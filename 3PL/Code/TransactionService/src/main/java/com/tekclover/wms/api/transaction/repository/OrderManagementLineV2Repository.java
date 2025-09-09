package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
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


    void deleteByCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String refDocNumber, String preOutboundNo, Long deletionIndicator);

}