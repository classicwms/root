package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.transaction.model.warehouse.outbound.OutboundOrder;

@Repository
@Transactional
public interface OutboundOrderRepository extends JpaRepository<OutboundOrder,Long> {

	public OutboundOrder findByOrderId(String orderId);
	public OutboundOrder findByRefDocumentNo(String refDocumentNo);
	public List<OutboundOrder> findByOrderReceivedOnBetween (Date date1, Date date2);
	public List<OutboundOrder> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);
	
}