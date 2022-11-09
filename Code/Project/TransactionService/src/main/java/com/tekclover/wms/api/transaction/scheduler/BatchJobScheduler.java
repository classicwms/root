package com.tekclover.wms.api.transaction.scheduler;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tekclover.wms.api.transaction.model.inbound.InboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationLine;
import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.OutboundIntegrationHeader;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.OutboundIntegrationLine;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.InboundOrder;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.InboundOrderLines;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.OutboundOrder;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.OutboundOrderLine;
import com.tekclover.wms.api.transaction.repository.InboundOrderRepository;
import com.tekclover.wms.api.transaction.repository.OutboundOrderRepository;
import com.tekclover.wms.api.transaction.service.OrderService;
import com.tekclover.wms.api.transaction.service.PreInboundHeaderService;
import com.tekclover.wms.api.transaction.service.PreOutboundHeaderService;
import com.tekclover.wms.api.transaction.service.ReportsService;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BatchJobScheduler {
	
	@Autowired
	PreInboundHeaderService preinboundheaderService;
	
	@Autowired
	PreOutboundHeaderService preOutboundHeaderService;
	
	@Autowired
	ReportsService reportsService;
	
	@Autowired
	OrderService orderService;
	
	//-------------------------------------------------------------------------------------------
	
	@Autowired
	InboundOrderRepository inboundOrderRepository;
	
	@Autowired
	OutboundOrderRepository outboundOrderRepository;
	
	//-------------------------------------------------------------------------------------------
	
	List<InboundIntegrationHeader> inboundList = null;
	List<OutboundIntegrationHeader> outboundList = null;
	static CopyOnWriteArrayList<InboundIntegrationHeader> spList = null; 			// Inbound List
	static CopyOnWriteArrayList<OutboundIntegrationHeader> spOutboundList = null; 	// Outbound List
	
	// Schedule Report
//	@Scheduled(cron = "0 0/5 6 * * *") // 0 0 */2 * * ?
//	@Scheduled(cron = "0 0 */1 * * ?")
//	public void scheduleInvReport() throws IllegalAccessException, InvocationTargetException {
//		reportsService.exportXlsxFile();
//	}
	
	//=======================================SQL-Processing==========================================================================
	
	@Scheduled(fixedDelay = 50000)
	public void processInboundOrder() throws IllegalAccessException, InvocationTargetException {
		if (inboundList == null || inboundList.isEmpty()) {
			List<InboundOrder> sqlInboundList = inboundOrderRepository.findTopByProcessedStatusIdOrderByOrderReceivedOn(0L);
			inboundList = new ArrayList<>();
			for (InboundOrder dbOBOrder : sqlInboundList) {
				InboundIntegrationHeader inboundIntegrationHeader = new InboundIntegrationHeader();
				BeanUtils.copyProperties(dbOBOrder, inboundIntegrationHeader, CommonUtils.getNullPropertyNames(dbOBOrder));
				inboundIntegrationHeader.setId(dbOBOrder.getOrderId());
				List<InboundIntegrationLine> inboundIntegrationLineList = new ArrayList<>();
				for (InboundOrderLines line : dbOBOrder.getLines()) {
					InboundIntegrationLine inboundIntegrationLine = new InboundIntegrationLine();
					BeanUtils.copyProperties(line, inboundIntegrationLine, CommonUtils.getNullPropertyNames(line));
					inboundIntegrationLineList.add(inboundIntegrationLine);
				}
				inboundIntegrationHeader.setInboundIntegrationLine (inboundIntegrationLineList);
				inboundList.add(inboundIntegrationHeader);
			}
			spList = new CopyOnWriteArrayList<InboundIntegrationHeader>(inboundList);
			log.info("There is no record found to process (sql) ...Waiting..");
		}
		
		if (inboundList != null) {
			log.info("Latest InboundOrder found: " + inboundList);
			for (InboundIntegrationHeader inbound : spList) {
				try {
					log.info("InboundOrder ID : " + inbound.getRefDocumentNo());
					InboundHeader inboundHeader = preinboundheaderService.processInboundReceived(inbound.getRefDocumentNo(), inbound);
					if (inboundHeader != null) {
						// Updating the Processed Status
						orderService.updateProcessedInboundOrder(inbound.getRefDocumentNo());
						inboundList.remove(inbound);
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error("Error on inbound processing : " + e.toString());
					// Updating the Processed Status
					orderService.updateProcessedInboundOrder(inbound.getRefDocumentNo());
					preinboundheaderService.createInboundIntegrationLog(inbound);
					inboundList.remove(inbound);
				}
			}
		}
	}
	
	// OutboundRecord
	@Scheduled(fixedDelay = 50000)
	public void processOutboundOrder() throws IllegalAccessException, InvocationTargetException, ParseException {
		if (outboundList == null || outboundList.isEmpty()) {
			List<OutboundOrder> sqlOutboundList = outboundOrderRepository.findTopByProcessedStatusIdOrderByOrderReceivedOn(0L);
			outboundList = new ArrayList<>();
			for (OutboundOrder dbOBOrder : sqlOutboundList) {
				OutboundIntegrationHeader outboundIntegrationHeader = new OutboundIntegrationHeader();
				BeanUtils.copyProperties(dbOBOrder, outboundIntegrationHeader, CommonUtils.getNullPropertyNames(dbOBOrder));
				outboundIntegrationHeader.setId(dbOBOrder.getOrderId());
				List<OutboundIntegrationLine> outboundIntegrationLineList = new ArrayList<>();
				for (OutboundOrderLine line : dbOBOrder.getLines()) {
					OutboundIntegrationLine outboundIntegrationLine = new OutboundIntegrationLine();
					BeanUtils.copyProperties(line, outboundIntegrationLine, CommonUtils.getNullPropertyNames(line));
					outboundIntegrationLineList.add(outboundIntegrationLine);
				}
				outboundIntegrationHeader.setOutboundIntegrationLine (outboundIntegrationLineList);
				outboundList.add(outboundIntegrationHeader);
			}
			spOutboundList = new CopyOnWriteArrayList<OutboundIntegrationHeader>(outboundList);
			log.info("There is no record found to process (sql) ...Waiting..");
		}
		
		if (outboundList != null) {
			log.info("Latest OutboundOrder found: " + outboundList);
			for (OutboundIntegrationHeader outbound : spOutboundList) {
				try {
					log.info("OutboundOrder ID : " + outbound.getRefDocumentNo());
					OutboundHeader outboundHeader = preOutboundHeaderService.processOutboundReceived(outbound);
					if (outboundHeader != null) {
						// Updating the Processed Status
						orderService.updateProcessedOrder(outbound.getRefDocumentNo());
						outboundList.remove(outbound);
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error("Error on outbound processing : " + e.toString());
					// Updating the Processed Status
					orderService.updateProcessedOrder(outbound.getRefDocumentNo());
					preOutboundHeaderService.createOutboundIntegrationLog(outbound);
					outboundList.remove(outbound);
				}
			}
		}
	}
}