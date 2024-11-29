package com.tekclover.wms.api.enterprise.transaction.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.transaction.model.inbound.preinbound.InboundIntegrationLog;
import com.tekclover.wms.api.enterprise.transaction.model.integration.IntegrationApiResponse;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.preoutbound.OutboundIntegrationLog;
import com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound.InboundOrder;
import com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound.*;
import com.tekclover.wms.api.enterprise.transaction.repository.*;
import com.tekclover.wms.api.enterprise.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class OrderService {
	
	@Autowired
	InboundOrderRepository inboundOrderRepository;
	
	@Autowired
	OutboundOrderRepository outboundOrderRepository;
	
	@Autowired
	OutboundOrderLinesRepository outboundOrderLinesRepository;
	
	@Autowired
	IntegrationApiResponseRepository integrationApiResponseRepository;
	
	@Autowired
	InboundIntegrationLogRepository inboundIntegrationLogRepository;

	@Autowired
	OutboundIntegrationLogRepository outboundIntegrationLogRepository;
	
	@Autowired
	WarehouseIdService warehouseIdService;
	
	/**
	 * 
	 * @return
	 */
	public List<InboundOrder> getInboundOrders() {
		return inboundOrderRepository.findAll();
	}

	/**
	 * 
	 * @param orderId
	 * @return
	 */
	public InboundOrder getOrderById(String orderId) {
		return inboundOrderRepository.findByRefDocumentNo (orderId);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<InboundIntegrationLog> getFailedInboundOrders() {
		return inboundIntegrationLogRepository.findByIntegrationStatus("FAILED");
	}
	
	/**
	 * 
	 * @param sdate
	 * @return
	 * @throws ParseException
	 */
	public List<InboundOrder> getOrderByDate(String sdate) throws ParseException {
		Date date1 = DateUtils.convertStringToDate_start(sdate);
		Date date2 = DateUtils.convertStringToDate_end(sdate);
		return inboundOrderRepository.findByOrderReceivedOnBetween(date1, date2);
	}
	
	/**
	 * 
	 * @param newInboundOrder
	 * @return
	 */
	public InboundOrder createInboundOrders(InboundOrder newInboundOrder) {
		InboundOrder inboundOrder = inboundOrderRepository.save(newInboundOrder);
		return inboundOrder;
	}
	
	/**
	 * 
	 * @param orderId
	 * @return
	 */
	public InboundOrder updateProcessedInboundOrder(String orderId) {
		InboundOrder dbInboundOrder = getOrderById (orderId);
		log.info("orderId : " + orderId);
		log.info("dbInboundOrder : " + dbInboundOrder);
		if (dbInboundOrder != null) {
			dbInboundOrder.setProcessedStatusId(10L);
			dbInboundOrder.setOrderProcessedOn(new Date());
			InboundOrder inboundOrder = inboundOrderRepository.save(dbInboundOrder);
			return inboundOrder;
		}
		return dbInboundOrder;
	}
	
	//-----------------------------Outbound-------------------------------------------
	
	/**
	 * 
	 * @return
	 */
	public List<OutboundOrder> getOBOrders() {
		return outboundOrderRepository.findAll();
	}

	/**
	 * 
	 * @param orderId
	 * @return
	 */
	public OutboundOrder getOBOrderById(String orderId) {
//		return outboundOrderRepository.findByOrderId(orderId);
		return outboundOrderRepository.findByRefDocumentNo (orderId);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<OutboundIntegrationLog> getFailedOutboundOrders() {
		return outboundIntegrationLogRepository.findByIntegrationStatus("FAILED");
	}
	
	/**
	 * 
	 * @param sdate
	 * @return
	 * @throws ParseException
	 */
	public List<OutboundOrder> getOBOrderByDate(String startDate, String endDate) throws ParseException {
		Date date1 = DateUtils.convertStringToDate_start(startDate);
		Date date2 = DateUtils.convertStringToDate_end(endDate);
		return outboundOrderRepository.findByOrderReceivedOnBetween(date1, date2);
	}
	
	/**
	 * 
	 * @param newInboundOrder
	 * @return
	 */
	public OutboundOrder createOutboundOrders(OutboundOrder newOutboundOrder) {
		OutboundOrder outboundOrder = outboundOrderRepository.save(newOutboundOrder);
		return outboundOrder;
	}
	
	/**
	 * 
	 * @param orderId
	 * @param newOutboundOrder
	 * @return
	 */
	public OutboundOrder updateProcessedOrder(String orderId) {
		OutboundOrder dbOutboundOrder = getOBOrderById(orderId);
		log.info("orderId : " + orderId);
		log.info("dbOutboundOrder : " + dbOutboundOrder);
		if (dbOutboundOrder != null) {
			dbOutboundOrder.setProcessedStatusId(10L);
			dbOutboundOrder.setOrderProcessedOn(new Date());
			OutboundOrder outboundOrder = outboundOrderRepository.save(dbOutboundOrder);
			return outboundOrder;
		}
		return dbOutboundOrder;
	}
	
	/**
	 * 
	 * @param orderId
	 */
	public void deleteObOrder (String orderId) {
		OutboundOrder existingOrder = getOBOrderById(orderId);
		if (existingOrder == null) {
			throw new BadRequestException(" Order : " + orderId + " doesn't exist.");
		}
		outboundOrderLinesRepository.deleteAll(existingOrder.getLines());
		outboundOrderRepository.delete(existingOrder);
	}

	/**
	 * 
	 * @param orderId
	 * @return
	 */
	public ShipmentOrder pushOrder(String orderId) {
		OutboundOrder existingOrder = outboundOrderRepository.findByOrderId(orderId);
		if (existingOrder == null) {
			throw new BadRequestException(" Order : " + orderId + " doesn't exist.");
		}
		
		if (existingOrder != null) {
			if (existingOrder.getOutboundOrderTypeID() == 0L) {
				ShipmentOrder so = new ShipmentOrder();
				/*
				 * WH_ID
				 * REF_DOC_NO
				 * PARTNER_CODE
				 * PARTNER_NM
				 * REQ_DEL_DATE
				 */
				SOHeader soHeader = new SOHeader();
				soHeader.setWareHouseId(existingOrder.getWarehouseID());
				soHeader.setTransferOrderNumber(existingOrder.getRefDocumentNo());
				soHeader.setStoreID(existingOrder.getPartnerCode());
				soHeader.setStoreName(existingOrder.getPartnerName());
				soHeader.setRequiredDeliveryDate(DateUtils.date2String_MMDDYYYY(existingOrder.getRequiredDeliveryDate()));
				
				Set<OutboundOrderLine> lines = existingOrder.getLines();
				List<SOLine> soLines = new ArrayList<>();
				for (OutboundOrderLine obline : lines) {
					/*
					 * OB_LINE_NO
					 * ITM_CODE
					 * ITEM_TEXT
					 * ORD_QTY
					 * ORD_UOM
					 * REF_FIELD_1
					 */
					SOLine soLine = new SOLine();
					soLine.setLineReference(obline.getLineReference());
					soLine.setSku(obline.getItemCode());
					soLine.setSkuDescription(obline.getItemText());
					soLine.setOrderedQty(obline.getOrderedQty());
					soLine.setUom(obline.getUom());
					soLine.setOrderType(obline.getRefField1ForOrderType());
					soLines.add(soLine);
				}
				
				so.setSoHeader(soHeader);
				so.setSoLine(soLines);
				
				ShipmentOrder createdSO = warehouseIdService.postSO(so, true);
				if (createdSO != null) {
					return createdSO;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param orderId
	 * @return
	 */
	public List<IntegrationApiResponse> getConfirmationOrder(String orderId) {
		return integrationApiResponseRepository.findByOrderNumber (orderId);
	}
}