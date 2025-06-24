package com.tekclover.wms.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.core.model.transaction.InboundIntegrationHeader;

@Repository
public interface MongoTransactionRepository extends MongoRepository<InboundIntegrationHeader, String> {
	
	public InboundIntegrationHeader findTopByOrderByOrderReceivedOnDesc();
	
	public List<InboundIntegrationHeader> findAllByWarehouseIDAndProcessedStatusIdAndOrderReceivedOn (
			String warehouseID, Long statusId, Date date);
	
	public List<InboundIntegrationHeader> findAllByWarehouseIDAndProcessedStatusId (
			String warehouseID, Long statusId);
}
