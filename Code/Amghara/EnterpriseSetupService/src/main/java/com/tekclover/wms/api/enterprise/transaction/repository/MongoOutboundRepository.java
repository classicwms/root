package com.tekclover.wms.api.enterprise.transaction.repository;//package com.tekclover.wms.api.enterprise.transaction.repository;
//
//import java.util.List;
//
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.stereotype.Repository;
//
//import com.tekclover.wms.api.enterprise.transaction.model.outbound.preoutbound.OutboundIntegrationHeader;
//
//@Repository
//public interface MongoOutboundRepository extends MongoRepository<OutboundIntegrationHeader, String> {
//	
//	public List<OutboundIntegrationHeader> findTopByProcessedStatusIdOrderByOrderReceivedOn(Long processedStatusId);
//	
//	public OutboundIntegrationHeader findByRefDocumentNo(String refDocumentNo);
//}