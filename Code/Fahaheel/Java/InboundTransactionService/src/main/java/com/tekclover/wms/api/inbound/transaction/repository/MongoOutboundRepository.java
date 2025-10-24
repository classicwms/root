//package com.tekclover.wms.api.transaction.repository;
//
//import java.util.List;
//
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.stereotype.Repository;
//
//import com.tekclover.wms.api.transaction.model.outbound.preoutbound.OutboundIntegrationHeader;
//
//@Repository
//public interface MongoOutboundRepository extends MongoRepository<OutboundIntegrationHeader, String> {
//	
//	public List<OutboundIntegrationHeader> findTopByProcessedStatusIdOrderByOrderReceivedOn(Long processedStatusId);
//	
//	public OutboundIntegrationHeader findByRefDocumentNo(String refDocumentNo);
//}
