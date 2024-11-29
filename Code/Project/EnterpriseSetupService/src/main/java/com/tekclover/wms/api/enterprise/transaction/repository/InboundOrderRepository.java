package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound.InboundOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface InboundOrderRepository extends JpaRepository<InboundOrder,Long> {

	public InboundOrder findByOrderId(String orderId);
	public List<InboundOrder> findByOrderReceivedOnBetween (Date date1, Date date2);
	public List<InboundOrder> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);
	public InboundOrder findByRefDocumentNo(String orderId);
}