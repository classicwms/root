package com.tekclover.wms.api.enterprise.transaction.repository;//package com.tekclover.wms.api.enterprise.transaction.repository;
//
//import java.util.List;
//
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.stereotype.Repository;
//
//import com.tekclover.wms.api.enterprise.transaction.model.inbound.preinbound.InboundIntegrationHeader;
//
//@Repository
//public interface MongoInboundRepository extends MongoRepository<InboundIntegrationHeader, String> {
//	
//	public List<InboundIntegrationHeader> findTopByProcessedStatusIdOrderByOrderReceivedOn(Long processedStatusId);
//	
//	public InboundIntegrationHeader findByRefDocumentNo(String refDocumentNo);
//}