package com.tekclover.wms.api.transaction.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationHeader;

@Repository
public interface MongoInboundRepository extends MongoRepository<InboundIntegrationHeader, String> {
	
	public List<InboundIntegrationHeader> findByProcessedStatusIdOrderByOrderReceivedOn(Long processedStatusId);
	
	public InboundIntegrationHeader findByRefDocumentNo(String refDocumentNo);
}
