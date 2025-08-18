package com.tekclover.wms.api.outbound.transaction.service;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.outbound.transaction.model.integration.IntegrationApiResponse;
import com.tekclover.wms.api.outbound.transaction.model.outbound.preoutbound.OutboundIntegrationLog;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.cyclecount.CycleCountHeader;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.*;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.v2.FindOutboundOrderLineV2;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.v2.FindOutboundOrderV2;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.v2.OutboundOrderLineV2;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.v2.OutboundOrderV2;
import com.tekclover.wms.api.outbound.transaction.repository.*;
import com.tekclover.wms.api.outbound.transaction.repository.specification.OuboundOrderLineV2Specification;
import com.tekclover.wms.api.outbound.transaction.repository.specification.OuboundOrderV2Specification;
import com.tekclover.wms.api.outbound.transaction.util.CommonUtils;
import com.tekclover.wms.api.outbound.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

@Slf4j
@Service
public class OrderService {

	@Autowired
    OutboundOrderRepository outboundOrderRepository;

	@Autowired
	private CycleCountHeaderRepository cycleCountHeaderRepository;
	
	@Autowired
    OutboundOrderLinesRepository outboundOrderLinesRepository;
	
	@Autowired
    IntegrationApiResponseRepository integrationApiResponseRepository;
	
	@Autowired
    OutboundIntegrationLogRepository outboundIntegrationLogRepository;
	
	@Autowired
	WarehouseService warehouseService;

	@Autowired
	private OutboundOrderV2Repository outboundOrderV2Repository;

	@Autowired
	private OutboundOrderLinesV2Repository outboundOrderLinesV2Repository;

	@Autowired
	DbConfigRepository dbConfigRepository;

	@Autowired
	PickupHeaderV2Repository pickupHeaderV2Repository;


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
	 * @param startDate
	 * @param endDate
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
	 * @param newOutboundOrder
	 * @return
	 */
	public OutboundOrder createOutboundOrders(OutboundOrder newOutboundOrder) {
		OutboundOrder outboundOrder = outboundOrderRepository.save(newOutboundOrder);
		return outboundOrder;
	}

	/**
	 *
	 * @param orderId
	 * @return
	 */
	public OutboundOrder updateProcessedOrder(String orderId) throws ParseException {
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
				
				ShipmentOrder createdSO = warehouseService.postSO(so, true);
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

	/**
	 *
	 * @param cycleCountHeader
	 * @return
	 */
	public CycleCountHeader createCycleCountOrder(CycleCountHeader cycleCountHeader) {
		CycleCountHeader dbCycleCountHeader = cycleCountHeaderRepository.save(cycleCountHeader);
		return dbCycleCountHeader;
	}

	//===================================================================V2========================================================================

	public OutboundOrderV2 updateProcessedOrderV2(String orderId, Long outboundOrderTypeID, Long processStatusId) throws ParseException {
		OutboundOrderV2 dbOutboundOrder = getOBOrderByIdV2(orderId, outboundOrderTypeID);
		log.info("orderId : " + orderId);
		log.info("dbOutboundOrder : " + dbOutboundOrder);
		if (dbOutboundOrder != null) {
			dbOutboundOrder.setProcessedStatusId(processStatusId);
			dbOutboundOrder.setOrderProcessedOn(new Date());
			if(dbOutboundOrder.getNumberOfAttempts() != null && dbOutboundOrder.getNumberOfAttempts().equals(3L)) {
				dbOutboundOrder.setProcessedStatusId(100L);
			}
			OutboundOrderV2 outboundOrder = outboundOrderV2Repository.save(dbOutboundOrder);
			return outboundOrder;
		}
		return dbOutboundOrder;
	}

	/**
	 * re run procedure
	 * @param orderId
	 * @param outboundOrderTypeID
	 * @return
	 */
	public void updateProcessedOrderV2(String orderId, Long outboundOrderTypeID) {
		log.info("rollback - rerun - orderId : " + orderId + "," + outboundOrderTypeID);
		OutboundOrderV2 dbOutboundOrder = getOBOrderByIdV2(orderId, outboundOrderTypeID);
		if (dbOutboundOrder != null) {
			Long numberOfAttemts = 0L;
			Long attempted = 0L;
			Long processStatusId = 0L;
			if(dbOutboundOrder.getNumberOfAttempts() != null){
				if(dbOutboundOrder.getNumberOfAttempts().equals(0L)){
					numberOfAttemts = 1L;
					processStatusId = 0L;
				}
				if(dbOutboundOrder.getNumberOfAttempts().equals(1L)){
					numberOfAttemts = 2L;
					processStatusId = 0L;
				}
				if(dbOutboundOrder.getNumberOfAttempts().equals(2L)){
					numberOfAttemts = 3L;
					processStatusId = 100L;
				}
				if(dbOutboundOrder.getNumberOfAttempts().equals(3L)){
					numberOfAttemts = 3L;
					processStatusId = 100L;
				}
			} else {
				numberOfAttemts = 1L;
				processStatusId = 0L;
			}
			dbOutboundOrder.setProcessedStatusId(processStatusId);
			dbOutboundOrder.setNumberOfAttempts(numberOfAttemts);
			dbOutboundOrder.setOrderProcessedOn(new Date());
			OutboundOrderV2 updatedOutboundOrder = outboundOrderV2Repository.save(dbOutboundOrder);
			log.info("rollback rerun - updatedOutboundOrder : " + updatedOutboundOrder);
		}
	}

	/**
	 *
	 * @param orderId
	 * @param outboundOrderTypeID
	 */
	public void reRunProcessedOrderV2(String orderId, Long outboundOrderTypeID) {
		log.info("rollback(Ext Trigger) - rerun - orderId : " + orderId + "," + outboundOrderTypeID);
		OutboundOrderV2 dbOutboundOrder = getOBOrderByIdV2(orderId, outboundOrderTypeID);
		if (dbOutboundOrder != null) {
			dbOutboundOrder.setProcessedStatusId(0L);
			dbOutboundOrder.setOrderProcessedOn(new Date());
			OutboundOrderV2 updatedOutboundOrder = outboundOrderV2Repository.save(dbOutboundOrder);
			log.info("rollback(Ext Trigger) rerun - updatedOutboundOrder : " + updatedOutboundOrder);
		}
	}

	/**
	 *
	 * @param orderId
	 * @return
	 */
	public OutboundOrderV2 getOBOrderByIdV2(String orderId, Long outboundOrderTypeID) {
//		return outboundOrderRepository.findByOrderId(orderId);
//		OutboundOrderV2 dbOutboundOrder = outboundOrderV2Repository.findByRefDocumentNo (orderId);
		OutboundOrderV2 dbOutboundOrder = outboundOrderV2Repository.findByRefDocumentNoAndOutboundOrderTypeID (orderId, outboundOrderTypeID);

		if(dbOutboundOrder!= null) {
			return dbOutboundOrder;
		} else {
			return null;
		}
		}

	public OutboundOrderV2 getOBOrderByIdV2(String orderId) {
		OutboundOrderV2 dbOutboundOrder = outboundOrderV2Repository.findByRefDocumentNo(orderId);

		if (dbOutboundOrder != null) {
			return dbOutboundOrder;
		} else {
			return null;
		}
	}

	public OutboundOrderV2 createOutboundOrdersV2(OutboundOrderV2 newOutboundOrder) throws ParseException {
//		OutboundOrderV2 dbOutboundOrder = outboundOrderV2Repository.findByRefDocumentNoAndProcessedStatusIdOrderByOrderReceivedOn(newOutboundOrder.getOrderId(), 0L);
//		OutboundOrderV2 dbOutboundOrder = getOBOrderByIdV2(newOutboundOrder.getOrderId());
		OutboundOrderV2 dbOutboundOrder = outboundOrderV2Repository.
				findByRefDocumentNoAndOutboundOrderTypeID(newOutboundOrder.getOrderId(), newOutboundOrder.getOutboundOrderTypeID());
		if(dbOutboundOrder != null) {
			throw new BadRequestException("Order is getting Duplicated");
		}
		newOutboundOrder.setUpdatedOn(new Date());
		OutboundOrderV2 outboundOrder = outboundOrderV2Repository.save(newOutboundOrder);
		String routingDb = dbConfigRepository.getDbName(newOutboundOrder.getCompanyCode(), newOutboundOrder.getBranchCode(), newOutboundOrder.getWarehouseID());
		DataBaseContextHolder.clear();
		DataBaseContextHolder.setCurrentDb(routingDb);
		 outboundOrderV2Repository.save(newOutboundOrder);

		return outboundOrder;
	}

	/**
	 *
	 * @param newOutboundOrder outboundOrder Saved Process (Same Order is coming PickUpLine is already created throw error)
	 * @return
	 * @throws ParseException exception
	 */
	public OutboundOrderV2 createOutboundOrdersV4(OutboundOrderV2 newOutboundOrder) throws ParseException {
		newOutboundOrder.setUpdatedOn(new Date());
		OutboundOrderV2 outboundOrder = outboundOrderV2Repository.save(newOutboundOrder);
		String routingDb = dbConfigRepository.getDbName(newOutboundOrder.getCompanyCode(), newOutboundOrder.getBranchCode(), newOutboundOrder.getWarehouseID());
		DataBaseContextHolder.clear();
		DataBaseContextHolder.setCurrentDb(routingDb);
		List<Long> statusIdList = Arrays.asList(57L, 50L);
		boolean pickUpConfirm = pickupHeaderV2Repository.existsByCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndStatusIdInAndDeletionIndicatorAndAssignedPickerIdIsNotNull(
				newOutboundOrder.getCompanyCode(), newOutboundOrder.getBranchCode(), newOutboundOrder.getWarehouseID(), newOutboundOrder.getOrderId(), statusIdList, 0L);
		log.info("PickupHeader Status Checking " + pickUpConfirm);
		if (pickUpConfirm) {
			throw new BadRequestException("This Order Already PickList Confirm --------> RefDocNo is " + newOutboundOrder.getOrderId());
		}
		outboundOrderV2Repository.save(newOutboundOrder);
		return outboundOrder;
	}

	//Find OutboundOrder
	public List<OutboundOrderV2> findOutboundOrderV2(FindOutboundOrderV2 findOutboundOrderV2) throws ParseException {

		if (findOutboundOrderV2.getFromOrderProcessedOn() != null && findOutboundOrderV2.getToOrderProcessedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(findOutboundOrderV2.getFromOrderProcessedOn(), findOutboundOrderV2.getToOrderProcessedOn());
			findOutboundOrderV2.setFromOrderProcessedOn(dates[0]);
			findOutboundOrderV2.setToOrderProcessedOn(dates[1]);
		}
		if (findOutboundOrderV2.getFromOrderReceivedOn() != null && findOutboundOrderV2.getToOrderReceivedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(findOutboundOrderV2.getFromOrderReceivedOn(), findOutboundOrderV2.getToOrderReceivedOn());
			findOutboundOrderV2.setFromOrderReceivedOn(dates[0]);
			findOutboundOrderV2.setToOrderReceivedOn(dates[1]);
		}
		if (findOutboundOrderV2.getFromSalesInvoiceDate() != null && findOutboundOrderV2.getToSalesInvoiceDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(findOutboundOrderV2.getFromSalesInvoiceDate(), findOutboundOrderV2.getToSalesInvoiceDate());
			findOutboundOrderV2.setFromSalesInvoiceDate(dates[0]);
			findOutboundOrderV2.setToSalesInvoiceDate(dates[1]);
		}

		OuboundOrderV2Specification spec = new OuboundOrderV2Specification(findOutboundOrderV2);
		List<OutboundOrderV2> results = outboundOrderV2Repository.findAll(spec);
		log.info("ObOrders process status result ---> {}", results);
		return results;

	}

	//Find OutboundOrderLine
	public List<OutboundOrderLineV2> findOutboundOrderLineV2(FindOutboundOrderLineV2 findOutboundOrderLineV2) throws ParseException {

		OuboundOrderLineV2Specification spec = new OuboundOrderLineV2Specification(findOutboundOrderLineV2);
		List<OutboundOrderLineV2> results = outboundOrderLinesV2Repository.findAll(spec);
		return results;

	}

	public OutboundOrderV2 createOutboundOrdersV3(OutboundOrderV2 newOutboundOrderV2) {

		try {
			OutboundOrderV2 dbInboundOrder = outboundOrderV2Repository.
					findByRefDocumentNoAndOutboundOrderTypeID(newOutboundOrderV2.getOrderId(), newOutboundOrderV2.getOutboundOrderTypeID());
			if (dbInboundOrder != null) {
				throw new BadRequestException("Order is getting Duplicated");
			}
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb("KNOWELL");
			OutboundOrderV2 inboundOrderV2 = outboundOrderV2Repository.save(newOutboundOrderV2);
			log.info("inboundOrderV2 ----> {}", inboundOrderV2);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb("MT");

			OutboundOrderV2 imdDB = outboundOrderV2Repository.save(newOutboundOrderV2);
			log.info("imdDB ----> {}", imdDB);

			return inboundOrderV2;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	//=========================================Update=====================================================================

	/**
	 * Update InboundOrder
	 *
	 * @param orderId
	 * @return
	 */
	public OutboundOrder updateOutboundOrder(String orderId, UpdateOutboundOrder updateOutboundOrder) {
		OutboundOrder dbOutboundOrder = getOBOrderById(orderId);
		BeanUtils.copyProperties(updateOutboundOrder, dbOutboundOrder, CommonUtils.getNullPropertyNames(updateOutboundOrder));
		dbOutboundOrder.setOrderProcessedOn(new Date());
		log.info("record is updated successfully");
		return outboundOrderRepository.save(dbOutboundOrder);
	}

	//==================================================Delete=========================================================

	/**
	 * Delete OutboundOrder
	 *
	 * @param orderId
	 */
	public void deleteOutboundOrder(String orderId) {
		OutboundOrder delete = getOBOrderById(orderId);
		if (delete == null) {
			throw new BadRequestException(" Order : " + orderId + " doesn't exist.");
		}
		outboundOrderLinesRepository.deleteAll(delete.getLines());
		outboundOrderRepository.delete(delete);
	}
}