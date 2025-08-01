package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.OutboundOrderV2;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface OutboundOrderV2Repository extends JpaRepository<OutboundOrderV2, Long>,
        JpaSpecificationExecutor<OutboundOrderV2>,
        StreamableJpaSpecificationRepository<OutboundOrderV2> {


    OutboundOrderV2 findByRefDocumentNo(String orderId);
    OutboundOrderV2 findByRefDocumentNoAndOutboundOrderTypeID(String orderId, Long outboundOrderTypeID);

    OutboundOrderV2 findByRefDocumentNoAndOutboundOrderTypeIDAndProcessedStatusId(String orderId, Long outboundOrderTypeID, Long statusId);
    OutboundOrderV2 findByRefDocumentNoAndProcessedStatusIdOrderByOrderReceivedOn(String orderId, Long deletionIndicator);

    @Modifying
    @Query(value = "update tbloborder2 set order_text = :text, pre_outbound_header = 1 where " +
            " outbound_order_typeid = :typeId and ref_document_no = :refDocNo ", nativeQuery = true)
    void updatePreOutBoundOrderText(@Param("typeId") Long typeId,
                                    @Param("refDocNo") String refDocNo,
                                    @Param("text") String text);

    List<OutboundOrderV2> findTopByProcessedStatusIdOrderByOrderReceivedOn(Long processedStatusId);

    @Query(value = "select \n"
            + "tc.c_text AS companyDesc,\n"
            + "tp.plant_text AS plantDesc,\n"
            + "tw.wh_text AS warehouseDesc \n"
            + "from tblcompanyid tc \n"
            + "join tblplantid tp on tp.c_id = tc.c_id and tp.lang_id = tc.lang_id \n"
            + "join tblwarehouseid tw on tw.c_id = tc.c_id and tw.lang_id = tc.lang_id and tw.plant_id = tp.plant_id \n"
            + "where \n"
            + "tc.c_id IN (:companyCodeId) and \n"
            + "tp.plant_id IN (:plantId) and \n"
            + "tw.wh_id IN (:warehouseId) \n", nativeQuery = true)
    IKeyValuePair getV2Description(@Param(value = "companyCodeId") String companyCodeId,
                                   @Param(value = "plantId") String plantId,
                                   @Param(value = "warehouseId") String warehouseId);

    @Modifying
    @Query(value = "update tbloborder2 set processed_status_id = 1 where " +
            " outbound_order_header_id = :outboundOrderHeaderId ", nativeQuery = true)
    void updateProcessStatusId(@Param("outboundOrderHeaderId") Long outboundOrderHeaderId);

    @Modifying
    @Query(value = "update tbloborder2 set order_text = :text, order_management_header = 1 where " +
            " outbound_order_typeid = :typeId and ref_document_no = :refDocNo ", nativeQuery = true)
    void updateOrderManagementText(@Param("typeId") Long typeId,
                                   @Param("refDocNo") String refDocNo,
                                   @Param("text") String text);

    @Modifying
    @Query(value = "update tbloborder2 set order_text = :text, outbound_header = 1 where " +
            " outbound_order_typeid = :typeId and ref_document_no = :refDocNo ", nativeQuery = true)
    void updateOutboundHeaderText(@Param("typeId") Long typeId,
                                  @Param("refDocNo") String refDocNo,
                                  @Param("text") String text);

    @Modifying
    @Query(value = "UPDATE tbloborder2 set processed_status_id = :processedStatusId WHERE \n" +
            "company_code = :companyCodeId AND branch_code = :plantId \n" +
            "AND warehouse_id = :warehouseId AND ref_document_no = :refDocNo", nativeQuery = true)
    void updateObOrderStatus(@Param("companyCodeId") String companyCodeId,
                             @Param("plantId") String plantId,
                             @Param("warehouseId") String warehouseId,
                             @Param("refDocNo") String refDocumentNo,
                             @Param("processedStatusId") Long processStatusId);

}