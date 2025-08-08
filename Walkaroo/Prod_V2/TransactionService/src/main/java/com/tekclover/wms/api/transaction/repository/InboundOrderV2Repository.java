package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
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

    List<InboundOrderV2> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);

    List<InboundOrderV2> findByProcessedStatusIdOrderByOrderReceivedOn(long l);

    InboundOrderV2 findByRefDocumentNoAndProcessedStatusIdOrderByOrderReceivedOn(String orderId, long l);

    public InboundOrderV2 findTopByRefDocumentNoOrderByOrderReceivedOnDesc(String orderId);

    @Modifying
    @Query(value = "update tbliborder2 set processed_status_id = 0 where " +
            " inbound_order_header_id = :inboundOrderHeaderId ", nativeQuery = true)
    void updateProcessStatusId(@Param("inboundOrderHeaderId") Long inboundOrderHeaderId);

    //=====================================Impex========================================================
    List<InboundOrderV2> findByInboundOrderHeaderId(long inboundOrderHeaderId);

    List<InboundOrderV2> findTopByProcessedStatusIdAndExecutedOrderByOrderReceivedOn(Long processStatusId, Long executed);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tbliborder2 set executed = 1 where " +
            " inbound_order_header_id = :inboundOrderHeaderId ", nativeQuery = true)
    void updateExecuted(@Param("inboundOrderHeaderId") Long inboundOrderHeaderId);

    List<InboundOrderV2> findByProcessedStatusIdAndExecutedOrderByOrderReceivedOn(Long processStatusId, Long executed);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tbliborder2 set executed = 1 where " +
            " inbound_order_header_id in :inboundOrderHeaderId ", nativeQuery = true)
    void updateBatchExecuted(@Param("inboundOrderHeaderId") List<Long> inboundOrderHeaderId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tbliborder2 set pre_inbound_header = 1, order_text = :text where " +
            "inbound_order_type_id = :inboundHeaderId and ref_document_no = :refDocNo", nativeQuery = true)
    void updateIbOrder(@Param("inboundHeaderId") Long inboundHeaderId,
                       @Param("refDocNo") String refDocNo,
                       @Param("text") String text);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tbliborder2 set inbound_header = 1, order_text = :text where " +
            "inbound_order_type_id = :inboundHeaderId and ref_document_no = :refDocNo", nativeQuery = true)
    void updateIbHeader(@Param("inboundHeaderId") Long inboundHeaderId,
                        @Param("refDocNo") String refDocNo,
                        @Param("text") String text);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tbliborder2 set staging_header = 1, order_text = :text where " +
            "inbound_order_type_id = :inboundHeaderId and ref_document_no = :refDocNo", nativeQuery = true)
    void updateStagingHeader(@Param("inboundHeaderId") Long inboundHeaderId,
                             @Param("refDocNo") String refDocNo,
                             @Param("text") String text);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tbliborder2 set gr_header = 1, order_text = :text where " +
            "inbound_order_type_id = :inboundHeaderId and ref_document_no = :refDocNo", nativeQuery = true)
    void updateGrHeader(@Param("inboundHeaderId") Long inboundHeaderId,
                        @Param("refDocNo") String refDocNo,
                        @Param("text") String text);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tbliborder2 set putaway_header = 1, order_text = :text where " +
            "inbound_order_type_id = :inboundHeaderId and ref_document_no = :refDocNo", nativeQuery = true)
    void updatePutawayHeader(@Param("inboundHeaderId") Long inboundHeaderId,
                        @Param("refDocNo") String refDocNo,
                        @Param("text") String text);


}