package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.OutboundOrderLineV2;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface OutboundOrderLinesV2Repository extends JpaRepository<OutboundOrderLineV2, Long>,
        JpaSpecificationExecutor<OutboundOrderLineV2>,
        StreamableJpaSpecificationRepository<OutboundOrderLineV2> {
    List<OutboundOrderLineV2> findAllByOrderIdAndOutboundOrderTypeID(String orderId, Long outboundOrderTypeID);

}