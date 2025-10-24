package com.tekclover.wms.api.inbound.orders.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.OutboundOrder;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface OutboundOrderRepository extends JpaRepository<OutboundOrder, Long>,
        StreamableJpaSpecificationRepository<OutboundOrder> {

    public OutboundOrder findByOrderId(String orderId);

    public OutboundOrder findByRefDocumentNo(String refDocumentNo);

    public List<OutboundOrder> findByOrderReceivedOnBetween(Date date1, Date date2);

    public List<OutboundOrder> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);

}