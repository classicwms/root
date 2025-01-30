package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound.OutboundOrder;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface OutboundOrderRepository extends JpaRepository<OutboundOrder, Long>,
        StreamableJpaSpecificationRepository<OutboundOrder> {

    public OutboundOrder findByOrderId(String orderId);

    public OutboundOrder findByRefDocumentNo(String refDocumentNo);

    public List<OutboundOrder> findByOrderReceivedOnBetween(Date date1, Date date2);

    public List<OutboundOrder> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);

}