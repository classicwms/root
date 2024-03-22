package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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

    InboundOrderV2 findByRefDocumentNoAndProcessedStatusIdOrderByOrderReceivedOn(String orderId, long l);

    public InboundOrderV2 findTopByRefDocumentNoOrderByOrderReceivedOnDesc(String orderId);

}