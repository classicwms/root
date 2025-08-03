package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.outbound.outboundreversal.v2.OutboundReversalV2;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface OutboundReversalV2Repository extends JpaRepository<OutboundReversalV2, Long>,
        JpaSpecificationExecutor<OutboundReversalV2>, StreamableJpaSpecificationRepository<OutboundReversalV2> {

    OutboundReversalV2 findByOutboundReversalNo(String outboundReversalNo);
}