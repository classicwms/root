package com.tekclover.wms.api.transaction.scheduler;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tekclover.wms.api.transaction.model.inbound.InboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.OutboundIntegrationHeader;
import com.tekclover.wms.api.transaction.repository.MongoInboundRepository;
import com.tekclover.wms.api.transaction.repository.MongoOutboundRepository;
import com.tekclover.wms.api.transaction.service.PreInboundHeaderService;
import com.tekclover.wms.api.transaction.service.PreOutboundHeaderService;
import com.tekclover.wms.api.transaction.service.ReportsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BatchJobScheduler {
	
	@Autowired
	PreInboundHeaderService preinboundheaderService;
	
	@Autowired
	PreOutboundHeaderService preOutboundHeaderService;
	
	@Autowired
	MongoInboundRepository mongoInboundRepository;
	
	@Autowired
	MongoOutboundRepository mongoOutboundRepository;
	
	@Autowired
	ReportsService reportsService;
	
	List<InboundIntegrationHeader> inboundList = null;
	List<OutboundIntegrationHeader> outboundList = null;
	
	static CopyOnWriteArrayList<InboundIntegrationHeader> spList = null; // Inbound List
	static CopyOnWriteArrayList<OutboundIntegrationHeader> spOutboundList = null; // Outbound List
	
	// Schedule Report
	@Scheduled(cron = "0 0/5 6 * * *")
	public void scheduleInvReport() throws IllegalAccessException, InvocationTargetException {
		reportsService.exportXlsxFile();
	}
	
	@Scheduled(fixedDelay = 50000)
	public void processInboundRecord() throws IllegalAccessException, InvocationTargetException {
		if (inboundList == null || inboundList.isEmpty()) {
			inboundList = mongoInboundRepository.findTopByProcessedStatusIdOrderByOrderReceivedOn(0L);
			spList = new CopyOnWriteArrayList<InboundIntegrationHeader>(inboundList); 
			log.info("There is no record found to process...Waiting..");
		}
		
		if (inboundList != null) {
			log.info("Latest InboundIntegrationHeader : " + inboundList);
			for (InboundIntegrationHeader inbound : spList) {
				try {
					InboundHeader inboundheader = 
							preinboundheaderService.processInboundReceived(inbound.getRefDocumentNo(), inbound);
					if (inboundheader != null) {
						inbound.setProcessedStatusId(10L);
						mongoInboundRepository.save(inbound);
					}
				} catch (Exception e) {
					log.error("Error on inbound processing : " + e.toString());
					e.printStackTrace();
					inbound.setProcessedStatusId(10L);
					mongoInboundRepository.save(inbound);
					preinboundheaderService.createInboundIntegrationLog(inbound);
				}
				inboundList.remove(inbound);
			}
		}
	}
	
	// OutboundRecord
	@Scheduled(fixedDelay = 25000)
	public void processOutboundRecord() throws IllegalAccessException, InvocationTargetException {
//		log.info("The time is :" + new Date());
		
		if (outboundList == null || outboundList.isEmpty()) {
			outboundList = mongoOutboundRepository.findTopByProcessedStatusIdOrderByOrderReceivedOn(0L);
			spOutboundList = new CopyOnWriteArrayList<OutboundIntegrationHeader>(outboundList);
			log.info("There is no record found to process...Waiting..");
		}
		
		if (outboundList != null) {
			log.info("Latest OutboundIntegrationHeader : " + outboundList);
			for (OutboundIntegrationHeader outbound : spOutboundList) {
				try {
					OutboundHeader outboundHeader = preOutboundHeaderService.processOutboundReceived(outbound);
					if (outboundHeader != null) {
						outbound.setProcessedStatusId(10L);
						mongoOutboundRepository.save(outbound);
						outboundList.remove(outbound);
					}
				} catch (Exception e) {
					log.error("Error on outbound processing : " + e.toString());
					e.printStackTrace();
					outbound.setProcessedStatusId(10L);
					mongoOutboundRepository.save(outbound);
					preOutboundHeaderService.createOutboundIntegrationLog(outbound);
					outboundList.remove(outbound);
				}
			}
		}
	}
}