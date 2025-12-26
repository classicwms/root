package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface InboundOrderV2Repository extends JpaRepository<InboundOrderV2, Long>,
        StreamableJpaSpecificationRepository<InboundOrderV2>, JpaSpecificationExecutor<InboundOrderV2> {
    public InboundOrderV2 findByRefDocumentNo(String orderId);
    public InboundOrderV2 findByRefDocumentNoAndInboundOrderTypeId(String orderId, Long inboundOrderTypeId);

    List<InboundOrderV2> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);

    InboundOrderV2 findByRefDocumentNoAndProcessedStatusIdOrderByOrderReceivedOn(String orderId, long l);

    public InboundOrderV2 findTopByRefDocumentNoOrderByOrderReceivedOnDesc(String orderId);

    @Modifying
    @Query(value = "update tbliborder2 set processed_status_id = 0 where " +
            " inbound_order_header_id = :inboundOrderHeaderId ", nativeQuery = true)
    void updateProcessStatusId(@Param("inboundOrderHeaderId") Long inboundOrderHeaderId);

    @Modifying
    @Query(value = "update tbliborder2 set processed_status_id = :statusId, order_processed_on = :updatedOn where " +
            " ref_document_no = :refDocNo ", nativeQuery = true)
    void updateProcessStatus(@Param("refDocNo") String refDocNo,
                             @Param("statusId") Long statusId,
                             @Param("updatedOn")Date updatedOn);



    @Query(value = "select top 1 * from tbliborder2 where inbound_order_header_id in (select max(inbound_order_header_id) from tbliborder2 where " +
            "processed_status_id = :statusId group by ref_document_no) order by order_received_on ", nativeQuery = true)
    List<InboundOrderV2> findInboundOrder(@Param("statusId") Long statusId);
}