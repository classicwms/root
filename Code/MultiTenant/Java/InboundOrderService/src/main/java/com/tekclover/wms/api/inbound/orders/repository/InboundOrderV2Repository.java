package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface InboundOrderV2Repository extends JpaRepository<InboundOrderV2, Long>,
        StreamableJpaSpecificationRepository<InboundOrderV2>, JpaSpecificationExecutor<InboundOrderV2> {
    public InboundOrderV2 findByRefDocumentNo(String orderId);
    public InboundOrderV2 findByRefDocumentNoAndInboundOrderTypeId(String orderId, Long inboundOrderTypeId);

    public InboundOrderV2 findByCompanyCodeAndBranchCodeAndWarehouseIDAndRefDocumentNoAndInboundOrderTypeId(
            String companyCode, String branchCode, String warehouseId, String orderId, Long inboundOrderTypeId);

    List<InboundOrderV2> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);

    InboundOrderV2 findByRefDocumentNoAndProcessedStatusIdOrderByOrderReceivedOn(String orderId, long l);

    public InboundOrderV2 findTopByRefDocumentNoOrderByOrderReceivedOnDesc(String orderId);

    @Modifying
    @Query(value = "update tbliborder2 set processed_status_id = 1 where " +
            " inbound_order_header_id = :inboundOrderHeaderId ", nativeQuery = true)
    void updateProcessStatusId(@Param("inboundOrderHeaderId") Long inboundOrderHeaderId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tbliborder2 set inbound_header = 1, order_text = :text where " +
            "inbound_order_type_id = :inboundHeaderId and ref_document_no = :refDocNo", nativeQuery = true)
    void updateIbHeader(@Param("inboundHeaderId") Long inboundHeaderId,
                        @Param("refDocNo") String refDocNo,
                        @Param("text") String text);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tbliborder2 set gr_header = 1, order_text = :text where " +
            "inbound_order_type_id = :inboundHeaderId and ref_document_no = :refDocNo", nativeQuery = true)
    void updateGrHeader(@Param("inboundHeaderId") Long inboundHeaderId,
                        @Param("refDocNo") String refDocNo,
                        @Param("text") String text);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tbliborder2 set staging_header = 1, order_text = :text where " +
            "inbound_order_type_id = :inboundHeaderId and ref_document_no = :refDocNo", nativeQuery = true)
    void updateStagingHeader(@Param("inboundHeaderId") Long inboundHeaderId,
                             @Param("refDocNo") String refDocNo,
                             @Param("text") String text);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tbliborder2 set pre_inbound_header = 1, order_text = :text where " +
            "inbound_order_type_id = :inboundHeaderId and ref_document_no = :refDocNo", nativeQuery = true)
    void updateIbOrder(@Param("inboundHeaderId") Long inboundHeaderId,
                       @Param("refDocNo") String refDocNo,
                       @Param("text") String text);

    @Modifying
    @Query(value = "UPDATE tbliborder2 set processed_status_id = :processedStatusId WHERE \n" +
            "company_code = :companyCodeId AND branch_code = :plantId \n" +
            "AND warehouse_id = :warehouseId AND ref_document_no = :refDocNo", nativeQuery = true)
    void updateIbOrderStatus(@Param("companyCodeId") String companyCodeId,
                             @Param("plantId") String plantId,
                             @Param("warehouseId") String warehouseId,
                             @Param("refDocNo") String refDocumentNo,
                             @Param("processedStatusId") Long processStatusId);

}