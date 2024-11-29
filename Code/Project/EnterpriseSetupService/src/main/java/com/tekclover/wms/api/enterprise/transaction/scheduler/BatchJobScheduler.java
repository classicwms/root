package com.tekclover.wms.api.enterprise.transaction.scheduler;

import com.tekclover.wms.api.enterprise.transaction.model.dto.OrderFailedInput;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.preoutbound.OutboundIntegrationHeader;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.preoutbound.OutboundIntegrationLine;
import com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound.OutboundOrder;
import com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound.OutboundOrderLine;
import com.tekclover.wms.api.enterprise.transaction.repository.OutboundOrderRepository;
import com.tekclover.wms.api.enterprise.transaction.service.IDMasterService;
import com.tekclover.wms.api.enterprise.transaction.service.OrderService;
import com.tekclover.wms.api.enterprise.transaction.service.PreOutboundHeaderService;
import com.tekclover.wms.api.enterprise.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class BatchJobScheduler {
	
	@Autowired
	PreOutboundHeaderService preOutboundHeaderService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	IDMasterService idMasterService;
	//-------------------------------------------------------------------------------------------
	
	@Autowired
	OutboundOrderRepository outboundOrderRepository;
	
	//-------------------------------------------------------------------------------------------
	
	List<OutboundIntegrationHeader> outboundList = null;
	static CopyOnWriteArrayList<OutboundIntegrationHeader> spOutboundList = null; 	// Outbound List
	
	//=======================================SQL-Processing==========================================================================
	// OutboundRecord
	@Scheduled(fixedDelay = 50000)
	public void processOutboundOrder() throws IllegalAccessException, InvocationTargetException, Exception {
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
					//send mail when order processing get failed
					sendMail(outbound, e);
				}
			}
		}
	}

	/**
	 *
	 * @param outbound
	 * @param e
	 */
	private void sendMail(OutboundIntegrationHeader outbound, Exception e) {
		//============================================================================================
		//Sending Failed Details through Mail
		OrderFailedInput orderFailedInput = new OrderFailedInput();
		orderFailedInput.setWarehouseId(outbound.getWarehouseID());
		orderFailedInput.setRefDocNumber(outbound.getRefDocumentNo());
		orderFailedInput.setReferenceField1(String.valueOf(outbound.getOutboundOrderTypeID()));
		String errorDesc = null;
		try {
			if (e.toString().contains("message")) {
				errorDesc = e.toString().substring(e.toString().indexOf("message") + 9);
				errorDesc = errorDesc.replaceAll("}]", "");
				}
			if (e.toString().contains("DataIntegrityViolationException") || e.toString().contains("ConstraintViolationException")) {
				errorDesc = "Null Pointer Exception";
			}
			if (e.toString().contains("CannotAcquireLockException") || e.toString().contains("LockAcquisitionException") ||
					e.toString().contains("SQLServerException") || e.toString().contains("UnexpectedRollbackException")) {
				errorDesc = "SQLServerException";
			}
			if (e.toString().contains("BadRequestException")) {
				errorDesc = e.toString().substring(e.toString().indexOf("BadRequestException:") + 20);
			}
			if (errorDesc == null) {
				errorDesc = e.toString();
			}
		} catch (Exception ex) {
			log.error("ErrorDesc Extract Error - outBound" + ex.toString());
		}
		orderFailedInput.setRemarks(errorDesc);
		sendMail(orderFailedInput);
	}

	/**
	 *
	 * @param orderFailedInput
	 */
	public void sendMail(OrderFailedInput orderFailedInput) {
        try {
			log.info("orderFailedInput : " + orderFailedInput);
            idMasterService.sendMail(orderFailedInput);
        } catch (Exception e) {
            log.error("Exception while sending Mail : " + e.toString());
		}
	}
}