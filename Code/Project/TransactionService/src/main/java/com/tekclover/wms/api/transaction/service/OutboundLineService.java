package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.expression.ParseException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AXAuthToken;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.dto.StatusId;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.transaction.model.impl.OrderStatusReportImpl;
import com.tekclover.wms.api.transaction.model.impl.OutBoundLineImpl;
import com.tekclover.wms.api.transaction.model.impl.ShipmentDispatchSummaryReportImpl;
import com.tekclover.wms.api.transaction.model.impl.StockMovementReportImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.integration.IntegrationApiResponse;
import com.tekclover.wms.api.transaction.model.outbound.AddOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLineInterim;
import com.tekclover.wms.api.transaction.model.outbound.SearchOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.SearchOutboundLineReport;
import com.tekclover.wms.api.transaction.model.outbound.UpdateOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.UpdateRequestDeliveryDate;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.OrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.UpdateOrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.AddOutboundReversal;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.OutboundReversal;
import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupLine;
import com.tekclover.wms.api.transaction.model.outbound.pickup.UpdatePickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityLine;
import com.tekclover.wms.api.transaction.model.report.FindImBasicData1;
import com.tekclover.wms.api.transaction.model.report.SearchOrderStatusReport;
import com.tekclover.wms.api.transaction.model.report.StockMovementReport;
import com.tekclover.wms.api.transaction.model.report.StockMovementReport1;
import com.tekclover.wms.api.transaction.model.trans.InventoryTrans;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.AXApiResponse;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.InterWarehouseShipment;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.InterWarehouseShipmentHeader;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.InterWarehouseShipmentLine;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.ReturnPO;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.ReturnPOHeader;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.ReturnPOLine;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.SalesOrder;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.SalesOrderHeader;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.SalesOrderLine;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.Shipment;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.ShipmentHeader;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.ShipmentLine;
import com.tekclover.wms.api.transaction.repository.ImBasicData1Repository;
import com.tekclover.wms.api.transaction.repository.InboundLineRepository;
import com.tekclover.wms.api.transaction.repository.IntegrationApiResponseRepository;
import com.tekclover.wms.api.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.InventoryTransRepository;
import com.tekclover.wms.api.transaction.repository.OrderManagementLineRepository;
import com.tekclover.wms.api.transaction.repository.OutboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.OutboundLineRepository;
import com.tekclover.wms.api.transaction.repository.PickupLineRepository;
import com.tekclover.wms.api.transaction.repository.PreOutboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PreOutboundLineRepository;
import com.tekclover.wms.api.transaction.repository.QualityLineRepository;
import com.tekclover.wms.api.transaction.repository.StockMovementReport1Repository;
import com.tekclover.wms.api.transaction.repository.specification.ImBasicData1Specification;
import com.tekclover.wms.api.transaction.repository.specification.OutboundLineReportSpecification;
import com.tekclover.wms.api.transaction.repository.specification.OutboundLineSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OutboundLineService extends BaseService {
	
	@Autowired
	private OutboundHeaderRepository outboundHeaderRepository;
	
	@Autowired
	private OutboundLineRepository outboundLineRepository;

	@Autowired
	private InboundLineRepository inboundLineRepository;
	
	@Autowired
	private OutboundHeaderService outboundHeaderService;
	
	@Autowired
	private InventoryService inventoryService;
	
	@Autowired
	private InventoryMovementRepository inventoryMovementRepository;
	
	@Autowired
	private InventoryRepository inventoryRepository;
	
	@Autowired
	private QualityHeaderService qualityHeaderService;
	
	@Autowired
	private QualityLineService qualityLineService;
	
	@Autowired
	private QualityLineRepository qualityLineRepository;
	
	@Autowired
	private PickupLineService pickupLineService;
	
	@Autowired
	private PickupHeaderService pickupHeaderService;
	
	@Autowired
	private PickupLineRepository pickupLineRepository;
	
	@Autowired
	private OrderManagementLineService orderManagementLineService;
	
	@Autowired
	private OutboundReversalService outboundReversalService;
	
	@Autowired
	private PreOutboundHeaderRepository preOutboundHeaderRepository;
	
	@Autowired
	private PreOutboundLineRepository preOutboundLineRepository;
	
	@Autowired
	private MastersService mastersService;
	
	@Autowired
	private AuthTokenService authTokenService;
	
	@Autowired
	private WarehouseService warehouseService;
	
	@Autowired
	private TransactionErrorService transactionErrorService;

	@Autowired
	private OrderManagementLineRepository orderManagementLineRepository;
	
	@Autowired
	private IntegrationApiResponseRepository integrationApiResponseRepository;
	
	@Autowired
	private ImBasicData1Repository imbasicdata1Repository;
	
	@Autowired
	private StockMovementReport1Repository stockMovementReport1Repository;
	
	@Autowired
	private PropertiesConfig propertiesConfig;
	
	// Inventory Prod Issue Fix
	@Autowired
	private InventoryTransRepository inventoryTransRepository;
	
	/**
	 * getOutboundLines
	 * @return
	 */
	public List<OutboundLine> getOutboundLines () {
		List<OutboundLine> outboundLineList =  outboundLineRepository.findAll();
		outboundLineList = outboundLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return outboundLineList;
	}
	
	/**
	 * getOutboundLine
	 * @param itemCode 
	 * @param lineNumber2 
	 * @param partnerCode 
	 * @param refDocNumber 
	 * @param preOutboundNo 
	 * @param warehouseId 
	 * @param plantId 
	 * @param languageId 
	 * @return
	 */
	public List<OutboundLine> getOutboundLine (String warehouseId, String preOutboundNo, 
			String refDocNumber, String partnerCode) {
		List<OutboundLine> outboundLine = 
				outboundLineRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, 0L);
		if (outboundLine != null) {
			return outboundLine;
		} else {
			throw new BadRequestException("The given OutboundLine ID : " 
					+ "warehouseId : " + warehouseId
					+ ", preOutboundNo : " + preOutboundNo
					+ ", refDocNumber : " + refDocNumber
					+ ", partnerCode : " + partnerCode
					+ " doesn't exist.");
		}
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param statusIds
	 * @return
	 */
	public long getOutboundLine (String warehouseId, String preOutboundNo, 
			String refDocNumber, String partnerCode, List<Long> statusIds) {
		long outboundLineCount = 
				outboundLineRepository.getOutboudLineByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndStatusIdInAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIds, 0);
		return outboundLineCount;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param statusIds
	 * @return
	 */
	public List<OutboundLine> getOutboundLines (String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, List<Long> statusIds) {
		List<OutboundLine> outboundLines = 
				outboundLineRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndStatusIdInAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIds, 0);
		return outboundLines;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param statusIds
	 * @return
	 */
	public List<OutboundLine> findOutboundLineByStatus (String warehouseId, String preOutboundNo, 
			String refDocNumber, String partnerCode, List<Long> statusIds) {
		List<OutboundLine> outboundLine;
		try {
			outboundLine = outboundLineRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndStatusIdInAndDeletionIndicator(
					warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIds, 0L);
			if (outboundLine == null) {
				throw new BadRequestException("The given OutboundLine ID : " 
						+ "warehouseId : " + warehouseId
						+ ", preOutboundNo : " + preOutboundNo
						+ ", refDocNumber : " + refDocNumber
						+ ", partnerCode : " + partnerCode
						+ " doesn't exist.");
			}
			return outboundLine;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @return
	 */
	public List<OutboundLine> getOutboundLine (String warehouseId, String preOutboundNo, 
			String refDocNumber) {
		List<OutboundLine> outboundLine = 
				outboundLineRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, null, 0L);
		if (!outboundLine.isEmpty()) {
			return outboundLine;
		} else {
			return null;
		}
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @return
	 */
	public List<OutboundLine> getOutboundLineForReports (String warehouseId, String preOutboundNo, 
			String refDocNumber) {
		List<OutboundLine> outboundLine = 
				outboundLineRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, null, 0L);
		if (outboundLine != null) {
			return outboundLine;
		} else {
			throw new BadRequestException("The given OutboundLine ID : " 
					+ "warehouseId : " + warehouseId
					+ ", preOutboundNo : " + preOutboundNo
					+ ", refDocNumber : " + refDocNumber
					+ " doesn't exist.");
		}
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @return
	 */
	public List<Long> getCountofOrderedLines (String warehouseId, String preOutboundNo, String refDocNumber) {
		List<Long> countofOrderedLines = outboundLineRepository.getCountofOrderedLines(warehouseId, preOutboundNo, refDocNumber);
		return countofOrderedLines;
	}
	
	/**
	 * getSumOfOrderedQty
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @return
	 */
	public List<Long> getSumOfOrderedQty (String warehouseId, String preOutboundNo, String refDocNumber) {
		List<Long> sumOfOrderedQty = outboundLineRepository.getSumOfOrderedQty(warehouseId, preOutboundNo, refDocNumber);
		return sumOfOrderedQty;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @return
	 */
	public Long getSumOfOrderedQtyByPartnerCode (String warehouseId, List<String> preOutboundNo, List<String> refDocNumber, String partnerCode) {
		Long sumOfOrderedQty = outboundLineRepository.getSumOfOrderedQtyByPartnerCode(warehouseId, preOutboundNo, refDocNumber, partnerCode);
		return sumOfOrderedQty;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @return
	 */
	public List<Long> getDeliveryLines(String warehouseId, String preOutboundNo, String refDocNumber) {
		List<Long> deliveryLines = outboundLineRepository.getDeliveryLines(warehouseId, preOutboundNo, refDocNumber);
		return deliveryLines;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @return
	 */
	public List<Long> getDeliveryQty (String warehouseId, String preOutboundNo, String refDocNumber) {
		List<Long> deliveryQtyList = outboundLineRepository.getDeliveryQty(warehouseId, preOutboundNo, refDocNumber);
		return deliveryQtyList;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @return
	 */
	public Long getDeliveryQtyByPartnerCode (String warehouseId, List<String> preOutboundNo, List<String> refDocNumber, String partnerCode) {
		Long deliveryQty = outboundLineRepository.getDeliveryQtyByPartnerCode(warehouseId, preOutboundNo, refDocNumber, partnerCode);
		return deliveryQty;
	}
	
	/**
	 * 
	 * @param preOBNo
	 * @param obLineNo
	 * @param itemCode
	 * @return
	 */
	public List<Long> getLineShipped(String preOBNo, Long obLineNo, String itemCode) {
		List<Long> lineShippedList = outboundLineRepository.findLineShipped(preOBNo, obLineNo, itemCode);
		return lineShippedList;
	}

	/**
	 *
	 * @param refDocNumber
	 * @return
	 */
	public List<OutBoundLineImpl> getOutBoundLineDataForOutBoundHeader(List<String> refDocNumber) {
		List<OutBoundLineImpl> outBoundLines = outboundLineRepository.getOutBoundLineDataForOutBoundHeader(refDocNumber);
		return outBoundLines;
	}
	
	/**
	 * Pass the Selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE in OUTBOUNDLINE table and 
	 * update SATATU_ID as 48
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @return 
	 * @return
	 */
	public OutboundLine getOutboundLine(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
			Long lineNumber, String itemCode) {
		OutboundLine outboundLine = outboundLineRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
				warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
		if (outboundLine != null) {
			return outboundLine;
		} 
		throw new BadRequestException("The given OutboundLine ID : " + 
					"warehouseId:" + warehouseId +
					",preOutboundNo:" + preOutboundNo +
					",refDocNumber:" + refDocNumber +
					",partnerCode:" + partnerCode +
					",lineNumber:" + lineNumber +
					",itemCode:" + itemCode +
					" doesn't exist.");
	}
	
	/**
	 * 
	 * @param refDocNo
	 * @return
	 */
	public List<Long> getLineItem_NByRefDocNoAndRefField2IsNull (List<String> refDocNo, Date startDate, Date endDate) {
		List<Long> lineItems = 
				outboundLineRepository.findLineItem_NByRefDocNoAndRefField2IsNull (refDocNo, startDate, endDate);
		return lineItems;
	}
	
	/**
	 * 
	 * @param refDocNo
	 * @return
	 */
	public List<Long> getLineItem_NByRefDocNoAndRefField2IsNull (List<String> refDocNo) {
		List<Long> lineItems = 
				outboundLineRepository.findLineItem_NByRefDocNoAndRefField2IsNull (refDocNo);
		return lineItems;
	}

	/**
	 * 
	 * @param refDocNo
	 * @return
	 */
	public List<Long> getShippedLines (List<String> refDocNo, Date startDate, Date endDate) {
		List<Long> lineItems = outboundLineRepository.findShippedLines(refDocNo, startDate, endDate);
		return lineItems;
	}
	
	/**
	 * 
	 * @param searchOutboundLine
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	public List<OutboundLine> findOutboundLine(SearchOutboundLine searchOutboundLine) 
			throws ParseException, java.text.ParseException {
		
		try {
			if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
				Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLine.getFromDeliveryDate(), 
						searchOutboundLine.getToDeliveryDate());
				searchOutboundLine.setFromDeliveryDate(dates[0]);
				searchOutboundLine.setToDeliveryDate(dates[1]);
			}
			
			OutboundLineSpecification spec = new OutboundLineSpecification(searchOutboundLine);
			List<OutboundLine> outboundLineSearchResults = outboundLineRepository.findAll(spec);
			
			/*
			 * Pass WH-ID/REF_DOC_NO/PRE_OB_NO/OB_LINE_NO/ITM_CODE
			 * PickConfirmQty & QCQty - from QualityLine table
			 */
			if (outboundLineSearchResults != null) {
				for (OutboundLine outboundLineSearchResult : outboundLineSearchResults) {
					Double qcQty = qualityLineRepository.getQualityLineCount(outboundLineSearchResult.getWarehouseId(),
							outboundLineSearchResult.getRefDocNumber(), outboundLineSearchResult.getPreOutboundNo(), 
							outboundLineSearchResult.getLineNumber(), outboundLineSearchResult.getItemCode());
//					log.info("qcQty : " + qcQty);
					if (qcQty != null) {
						outboundLineSearchResult.setReferenceField10(String.valueOf(qcQty));
					}

					Double pickConfirmQty = pickupLineRepository.getPickupLineCount(outboundLineSearchResult.getWarehouseId(),
							outboundLineSearchResult.getRefDocNumber(), outboundLineSearchResult.getPreOutboundNo(), 
							outboundLineSearchResult.getLineNumber(), outboundLineSearchResult.getItemCode());
//					log.info("pickConfirmQty : " + pickConfirmQty);
					if (pickConfirmQty != null) {
						outboundLineSearchResult.setReferenceField9(String.valueOf(pickConfirmQty));
					}
				}
			}
			return outboundLineSearchResults;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param searchOutboundLine
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	public List<OutboundLine> findOutboundLineNew(SearchOutboundLine searchOutboundLine)
			throws ParseException, java.text.ParseException {

		try {
			if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
				Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLine.getFromDeliveryDate(),
						searchOutboundLine.getToDeliveryDate());
				searchOutboundLine.setFromDeliveryDate(dates[0]);
				searchOutboundLine.setToDeliveryDate(dates[1]);
			}

			if(searchOutboundLine.getWarehouseId() == null || searchOutboundLine.getWarehouseId().isEmpty()){
				searchOutboundLine.setWarehouseId(null);
			}
			if(searchOutboundLine.getPreOutboundNo() == null || searchOutboundLine.getPreOutboundNo().isEmpty()){
				searchOutboundLine.setPreOutboundNo(null);
			}
			if(searchOutboundLine.getRefDocNumber() == null || searchOutboundLine.getRefDocNumber().isEmpty()){
				searchOutboundLine.setRefDocNumber(null);
			}
			if(searchOutboundLine.getLineNumber() == null || searchOutboundLine.getLineNumber().isEmpty()){
				searchOutboundLine.setLineNumber(null);
			}
			if(searchOutboundLine.getItemCode() == null || searchOutboundLine.getItemCode().isEmpty()){
				searchOutboundLine.setItemCode(null);
			}
			if(searchOutboundLine.getStatusId() == null || searchOutboundLine.getStatusId().isEmpty()){
				searchOutboundLine.setStatusId(null);
			}
			if(searchOutboundLine.getOrderType() == null || searchOutboundLine.getOrderType().isEmpty()){
				searchOutboundLine.setOrderType(null);
			}
			if(searchOutboundLine.getPartnerCode() == null || searchOutboundLine.getPartnerCode().isEmpty()){
				searchOutboundLine.setPartnerCode(null);
			}

			List<OutboundLine> outboundLineSearchResults = outboundLineRepository.findOutboundLineNew(searchOutboundLine.getWarehouseId(),
					searchOutboundLine.getFromDeliveryDate(),searchOutboundLine.getToDeliveryDate(),searchOutboundLine.getPreOutboundNo(),
					searchOutboundLine.getRefDocNumber(),searchOutboundLine.getLineNumber(),searchOutboundLine.getItemCode(),
					searchOutboundLine.getStatusId(),searchOutboundLine.getOrderType(),searchOutboundLine.getPartnerCode());


			return outboundLineSearchResults;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @throws Exception
	 */
//	@Scheduled(cron = "0 0/32 11 * * *")
	public void scheduleStockMovementReport() throws Exception {
		log.info("scheduleStockMovementReport---STARTED-------> : " + new Date());
		FindImBasicData1 searchImBasicData1 = new FindImBasicData1();
		Date dateFrom = DateUtils.convertStringToDateByYYYYMMDD ("2022-06-20");
		Date dateTo = new Date();
		Date[] dates = DateUtils.addTimeToDatesForSearch(dateFrom, dateTo);
		searchImBasicData1.setWarehouseId(WAREHOUSE_ID_110);
		searchImBasicData1.setFromCreatedOn(dates[0]);
		searchImBasicData1.setToCreatedOn(dates[1]);
		
		ImBasicData1Specification spec = new ImBasicData1Specification(searchImBasicData1);
		List<ImBasicData1> resultsImBasicData1 = imbasicdata1Repository.findAll(spec);
		log.info("resultsImBasicData1 : " + resultsImBasicData1.size());
		
		List<String> itemCodeList = resultsImBasicData1.stream().map(ImBasicData1::getItemCode).collect(Collectors.toList());
		log.info("itemCodeList : " + itemCodeList.size());
		
		itemCodeList.stream().forEach(i -> {
			try {
				findLinesForStockMovementForScheduler (WAREHOUSE_ID_110, i, dates[0], dates[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		log.info("scheduleStockMovementReport---COMPLETED-------> : " + new Date());
	}
	
	/**
	 * 
	 * @param searchOutboundLine
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	public void findLinesForStockMovementForScheduler(String warehouseId, String itemCode, Date fromDate, Date toDate)
			throws ParseException {
		try {
			List<StockMovementReportImpl> allLineData = new ArrayList<>();
			List<StockMovementReportImpl> outboundLineSearchResults = 
					outboundLineRepository.findOutboundLineForStockMovement(Arrays.asList(itemCode), Arrays.asList(warehouseId), 59L, fromDate, toDate);
			
			List<StockMovementReportImpl> inboundLineSearchResults = 
					inboundLineRepository.findInboundLineForStockMovement(Arrays.asList(itemCode), Arrays.asList(warehouseId), Arrays.asList(14L, 20L, 24L));
			
			allLineData.addAll(outboundLineSearchResults);
			allLineData.addAll(inboundLineSearchResults);
			
			List<StockMovementReport1> stockMovementReports = new ArrayList<>();
			allLineData.forEach(data->{
				StockMovementReport1 stockMovementReport = new StockMovementReport1();
				BeanUtils.copyProperties(data, stockMovementReport, CommonUtils.getNullPropertyNames(data));
				if (data.getConfirmedOn() == null) {
					Date date = inboundLineRepository.findDateFromPutawayLine (stockMovementReport.getDocumentNumber(), stockMovementReport.getItemCode());
					stockMovementReport.setConfirmedOn(date);
				}
				stockMovementReports.add(stockMovementReport);
			});
			stockMovementReport1Repository.saveAll(stockMovementReports);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param searchOutboundLine
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	public List<StockMovementReport> findLinesForStockMovement(SearchOutboundLine searchOutboundLine)
			throws ParseException, java.text.ParseException {
		try {
			if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
				Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLine.getFromDeliveryDate(),
						searchOutboundLine.getToDeliveryDate());
				searchOutboundLine.setFromDeliveryDate(dates[0]);
				searchOutboundLine.setToDeliveryDate(dates[1]);
			}
			
			log.info("searchOutboundLine.getWarehouseId() :  " + searchOutboundLine.getWarehouseId());
			List<StockMovementReportImpl> allLineData = new ArrayList<>();
			List<StockMovementReportImpl> outboundLineSearchResults = 
					outboundLineRepository.findOutboundLineForStockMovement(searchOutboundLine.getItemCode(), searchOutboundLine.getWarehouseId(),
							59L, searchOutboundLine.getFromDeliveryDate(), searchOutboundLine.getToDeliveryDate());
			
			List<StockMovementReportImpl> inboundLineSearchResults = 
					inboundLineRepository.findInboundLineForStockMovement(searchOutboundLine.getItemCode(), searchOutboundLine.getWarehouseId(),
							Arrays.asList(14L, 20L, 24L));
			
			allLineData.addAll(outboundLineSearchResults);
			allLineData.addAll(inboundLineSearchResults);
			
			List<StockMovementReport> stockMovementReports = new ArrayList<>();
			allLineData.forEach(data->{
				StockMovementReport stockMovementReport = new StockMovementReport();
				BeanUtils.copyProperties(data, stockMovementReport, CommonUtils.getNullPropertyNames(data));
				if (data.getConfirmedOn() == null) {
					Date date = inboundLineRepository.findDateFromPutawayLine (stockMovementReport.getDocumentNumber(), stockMovementReport.getItemCode());
					stockMovementReport.setConfirmedOn(date);
				}
				stockMovementReports.add(stockMovementReport);
			});
			log.info("stockMovementReports :  " + stockMovementReports);			
			stockMovementReports.sort(Comparator.comparing(StockMovementReport::getConfirmedOn));
			return stockMovementReports;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Inventory Movement included
	 * @param searchOutboundLine
	 * @return
	 * @throws Exception
	 */
	public List<StockMovementReport> findLinesForStockMovementV2(SearchOutboundLine searchOutboundLine) throws Exception {
		try {
			if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
				Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLine.getFromDeliveryDate(),
																 searchOutboundLine.getToDeliveryDate());
				searchOutboundLine.setFromDeliveryDate(dates[0]);
				searchOutboundLine.setToDeliveryDate(dates[1]);
			}

			log.info("Stock Movement Report Input :  " + searchOutboundLine);
			List<StockMovementReportImpl> allLineData = new ArrayList<>();
			List<StockMovementReportImpl> outboundLineSearchResults =
					outboundLineRepository.findOutboundLineForStockMovement(searchOutboundLine.getItemCode(), searchOutboundLine.getWarehouseId(),
																			59L, searchOutboundLine.getFromDeliveryDate(), searchOutboundLine.getToDeliveryDate());

			List<StockMovementReportImpl> inboundLineSearchResults =
					inboundLineRepository.findInboundLineForStockMovement(searchOutboundLine.getItemCode(), searchOutboundLine.getWarehouseId(),
																		  Arrays.asList(14L, 20L, 24L));

			List<StockMovementReportImpl> inventoryMovementSearchResults =
					inventoryMovementRepository.findStockMovement(searchOutboundLine.getItemCode(), searchOutboundLine.getWarehouseId(),
																  searchOutboundLine.getFromDeliveryDate(), searchOutboundLine.getToDeliveryDate());

			allLineData.addAll(outboundLineSearchResults);
			allLineData.addAll(inboundLineSearchResults);
			allLineData.addAll(inventoryMovementSearchResults);

			List<StockMovementReport> stockMovementReports = new ArrayList<>();
			allLineData.forEach(data->{
				StockMovementReport stockMovementReport = new StockMovementReport();
				BeanUtils.copyProperties(data, stockMovementReport, CommonUtils.getNullPropertyNames(data));
				if (data.getConfirmedOn() == null && data.getDocumentType().equalsIgnoreCase("InBound")) {
					Date date = inboundLineRepository.findDateFromPutawayLine (stockMovementReport.getDocumentNumber(), stockMovementReport.getItemCode());
					stockMovementReport.setConfirmedOn(date);
				}
				stockMovementReports.add(stockMovementReport);
			});
			log.info("stockMovementReports :  " + stockMovementReports);
			stockMovementReports.sort(Comparator.comparing(StockMovementReport::getConfirmedOn, Comparator.nullsLast(Comparator.naturalOrder())));
			return stockMovementReports;
		} catch (Exception e) {
			log.error("Exception while Stock movement report generate : " + e.toString());
			throw e;
		}
	}
	
	/**
	 *
	 * @param searchOutboundLine
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	public List<ShipmentDispatchSummaryReportImpl> findOutboundLineShipmentReport(SearchOutboundLine searchOutboundLine)
			throws ParseException, java.text.ParseException {

//		if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
//			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLine.getFromDeliveryDate(),
//					searchOutboundLine.getToDeliveryDate());
//			searchOutboundLine.setFromDeliveryDate(dates[0]);
//			searchOutboundLine.setToDeliveryDate(dates[1]);
//		}
		
		List<ShipmentDispatchSummaryReportImpl> outboundLineSearchResults =
				outboundLineRepository.getOrderLinesForShipmentDispatchReport(searchOutboundLine.getFromDeliveryDate(), 
						searchOutboundLine.getToDeliveryDate(),searchOutboundLine.getWarehouseId().get(0));

			return outboundLineSearchResults;
	}
	
	/**
	 * 	
	 * @param searchOutboundLineReport
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	public List<OutboundLine> findOutboundLineReport (SearchOutboundLineReport searchOutboundLineReport) 
			throws ParseException, java.text.ParseException {
		if (searchOutboundLineReport.getStartConfirmedOn() != null && searchOutboundLineReport.getStartConfirmedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLineReport.getStartConfirmedOn(), searchOutboundLineReport.getEndConfirmedOn());
			searchOutboundLineReport.setStartConfirmedOn(dates[0]);
			searchOutboundLineReport.setEndConfirmedOn(dates[1]);
		}
		
		OutboundLineReportSpecification spec = new OutboundLineReportSpecification (searchOutboundLineReport);
		List<OutboundLine> results = outboundLineRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * findOutboundLineOrderStatusReport
	 * @param searchOrderStatusReport
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	public List<OrderStatusReportImpl> findOutboundLineOrderStatusReport (SearchOrderStatusReport searchOrderStatusReport)
			throws ParseException, java.text.ParseException {
		if (searchOrderStatusReport.getFromDeliveryDate() != null && searchOrderStatusReport.getToDeliveryDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOrderStatusReport.getFromDeliveryDate(),
					searchOrderStatusReport.getToDeliveryDate());
			searchOrderStatusReport.setFromDeliveryDate(dates[0]);
			searchOrderStatusReport.setToDeliveryDate(dates[1]);
		}

		List<OrderStatusReportImpl> results = outboundLineRepository.getOrderStatusReportFromOutboundLines(
				searchOrderStatusReport.getWarehouseId(),
				searchOrderStatusReport.getFromDeliveryDate(),
				searchOrderStatusReport.getToDeliveryDate());
		return results;
	}
	
	/**
	 * createOutboundLine
	 * @param newOutboundLine
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public OutboundLine createOutboundLine (AddOutboundLine newOutboundLine, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		OutboundLine dbOutboundLine = new OutboundLine();
		log.info("newOutboundLine : " + newOutboundLine);
		BeanUtils.copyProperties(newOutboundLine, dbOutboundLine);
		dbOutboundLine.setDeletionIndicator(0L);
		dbOutboundLine.setCreatedBy(loginUserID);
		dbOutboundLine.setUpdatedBy(loginUserID);
		dbOutboundLine.setCreatedOn(new Date());
		dbOutboundLine.setUpdatedOn(new Date());
		return outboundLineRepository.save(dbOutboundLine);
	}
	
	/**
	 * after AX success - Update Inventory with @Transactional along with retry to handle exceptions and status update with retry attemts
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<OutboundLine> deliveryConfirmation (String warehouseId, String preOutboundNo, String refDocNumber, 
			String partnerCode, String loginUserID, Boolean webPortal) throws IllegalAccessException, InvocationTargetException {
		log.info("-----deliveryConfirmation--------called-----> : " + warehouseId + "," + 
			preOutboundNo + "," + refDocNumber + "," + partnerCode);
		
		/*--------------------OutboundLine-Check---STATUS-59---------------------------------------------------------------*/
		List<Long> statusIds = Arrays.asList(59L);
		long outboundLineProcessedCount = getOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIds);
		log.info("outboundLineProcessedCount : " + outboundLineProcessedCount);
		boolean isAlreadyProcessed = (outboundLineProcessedCount > 0 ? true : false);
		log.info("outboundLineProcessed Already Processed? : " + isAlreadyProcessed);
		if (isAlreadyProcessed) {
			throw new BadRequestException("Order is already processed.");
		}
		
		/*--------------------OutboundLine-Check--STATUS-50----------------------------------------------------------------*/
		statusIds = Arrays.asList(50L);
		long outboundLineUnprocessedCount = getOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIds);
		log.info("outboundLineUnprocessedCount : " + outboundLineUnprocessedCount);
		boolean isNotProcessed = (outboundLineUnprocessedCount > 0 ? true : false);
		log.info("outboundLine is not processed : " + isNotProcessed);
		if (isNotProcessed) {
			throw new BadRequestException("Quality Line is not created.");
		}
		
		/*--------------------OrderManagementLine-Check---------------------------------------------------------------------*/
		// OrderManagementLine checking for STATUS_ID - 42L, 43L
		long orderManagementLineCount = orderManagementLineService.getOrderManagementLine(warehouseId, refDocNumber, preOutboundNo, Arrays.asList(42L, 43L));
		boolean isConditionMet = (orderManagementLineCount > 0 ? true : false);
		log.info("orderManagementLineCount ---- isConditionMet : " + isConditionMet);
		if (isConditionMet) {
			throw new BadRequestException("OrderManagementLine is not completely Processed.");
		}
		
		/*--------------------PickupHeader-Check---------------------------------------------------------------------*/
		// PickupHeader checking for STATUS_ID - 48
		long pickupHeaderCount = pickupHeaderService.getPickupHeaderCountForDeliveryConfirmation(warehouseId, refDocNumber, preOutboundNo, 48L);
		isConditionMet = (pickupHeaderCount > 0 ? true : false);
		log.info("pickupHeaderCount ---- isConditionMet : " + isConditionMet);
		if (isConditionMet) {
			throw new BadRequestException("Pickup is not completely Processed.");
		}
		
		// QualityHeader checking for STATUS_ID - 54
		long qualityHeaderCount = qualityHeaderService.getQualityHeaderCountForDeliveryConfirmation(warehouseId, refDocNumber, preOutboundNo, 54L);
		isConditionMet = (qualityHeaderCount > 0 ? true : false);
		log.info("qualityHeaderCount ---- isConditionMet : " + isConditionMet);
		if (isConditionMet) {
			throw new BadRequestException("Quality check is not completely Processed.");
		}
		
		//----------------------------------------------------------------------------------------------------------
		List<Long> statusIdsToBeChecked = Arrays.asList(57L, 47L, 51L, 41L);
		long outboundLineListCount = getOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIdsToBeChecked);
		log.info("outboundLineListCount : " + outboundLineListCount);
		isConditionMet = (outboundLineListCount > 0 ? true : false);
		log.info("isConditionMet : " + isConditionMet);
		
		AXApiResponse axapiResponse = null;
		if (!isConditionMet) {
			throw new BadRequestException("OutboundLine: Order is not completely Processed.");
		} else {
			log.info("Order can be Processed.");
			/*
			 * Call this respective API end points when REF_DOC_NO is confirmed with STATUS_ID = 59 in OUTBOUNDHEADER and 
			 * OUTBOUNDLINE tables and based on OB_ORD_TYP_ID as per API document
			 */
			OutboundHeader confirmedOutboundHeader = outboundHeaderService.getOutboundHeader(warehouseId, preOutboundNo, refDocNumber);
			List<OutboundLine> confirmedOutboundLines = getOutboundLine(warehouseId, preOutboundNo, refDocNumber);
			log.info("OutboundOrderTypeId : " + confirmedOutboundHeader.getOutboundOrderTypeId() );
			log.info("confirmedOutboundLines: " + confirmedOutboundLines);
			
			List<OutboundLine> outboundLines = getOutboundLines(warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIdsToBeChecked);
			List<Long> lineNumbers = outboundLines.stream().map(OutboundLine::getLineNumber).collect(Collectors.toList());	
			List<String> itemCodes = outboundLines.stream().map(OutboundLine::getItemCode).collect(Collectors.toList());	
			
			// WebPortal value true is coming Outbound_Header Update Pdf_Print value false
//			if(webPortal){
//				log.info("WebPortal value is " + webPortal);
//				outboundHeaderRepository.updatePdfPrintOutboundHeader(warehouseId, preOutboundNo, refDocNumber);
//			}
			/*---------------------AXAPI-integration----------------------------------------------------------*/
			
			// if OB_ORD_TYP_ID = 0 in OUTBOUNDHEADER table - call Shipment Confirmation
			if (confirmedOutboundHeader.getOutboundOrderTypeId() == 0L && confirmedOutboundLines != null) {
				axapiResponse = postShipment (confirmedOutboundHeader, confirmedOutboundLines);
				log.info("AXApiResponse: " + axapiResponse);
			}

			// if OB_ORD_TYP_ID = 1 in OUTBOUNDHEADER table - Interwarehouse Shipment Confirmation
			if (confirmedOutboundHeader.getOutboundOrderTypeId() == 1L && confirmedOutboundLines != null) {
				axapiResponse = postInterwarehouseShipment (confirmedOutboundHeader, confirmedOutboundLines);
				log.info("AXApiResponse: " + axapiResponse);
			}

			//  if OB_ORD_TYP_ID = 2 in OUTBOUNDHEADER table - Return PO Confirmation
			if (confirmedOutboundHeader.getOutboundOrderTypeId() == 2L && confirmedOutboundLines != null) {
				axapiResponse = postReturnPO (confirmedOutboundHeader, confirmedOutboundLines);
				log.info("AXApiResponse: " + axapiResponse);
			}

			// if OB_ORD_TYP_ID = 3 in OUTBOUNDHEADER table - Sale Order Confirmation - True Express
			if (confirmedOutboundHeader.getOutboundOrderTypeId() == 3L && confirmedOutboundLines != null) {
				axapiResponse = postSalesOrder (confirmedOutboundHeader, confirmedOutboundLines);
				log.info("AXApiResponse: " + axapiResponse);
			}
			axapiResponse = new AXApiResponse();
			axapiResponse.setStatusCode("200");
		}
	
		if (axapiResponse.getStatusCode() != null && axapiResponse.getStatusCode().equalsIgnoreCase("200")) {
			try {
				Long STATUS_ID_59 = 59L;
				List<Long> statusId57 = Arrays.asList(51L, 57L);
				List<OutboundLine> outboundLineByStatus57List = findOutboundLineByStatus(warehouseId, preOutboundNo, refDocNumber, partnerCode, statusId57);
				
				// ----------------OoutboundLine update-----------------------------------------------------------------------------------------
				List<Long> lineNumbers = outboundLineByStatus57List.stream().map(OutboundLine::getLineNumber).collect(Collectors.toList());
				List<String> itemCodes = outboundLineByStatus57List.stream().map(OutboundLine::getItemCode).collect(Collectors.toList());
				
				// Inventory Update
				inventoryUpdateBeforeAXSubmit (warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumbers, itemCodes);
				
				/*------------------------------------Status Update-------------------------------------------*/
				deliveryConfirmStatusUpdate(warehouseId, preOutboundNo, refDocNumber, STATUS_ID_59, lineNumbers, loginUserID);
							
				/*-------------------Inserting record in InventoryMovement-------------------------------------*/
				Long BIN_CLASS_ID = 5L;
				AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
				StorageBin storageBin = mastersService.getStorageBin(warehouseId, BIN_CLASS_ID, authTokenForMastersService.getAccess_token());
				String movementDocumentNo = refDocNumber;
				String stBin = storageBin.getStorageBin();
				String movementQtyValue = "N";
				
				List<PickupLine> dbPickupLine = pickupLineService.getPickupLine (warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumbers, itemCodes);
				log.info("dbPickupLine: " + dbPickupLine.size());
				if(dbPickupLine != null) {
					List<InventoryMovement> newInventoryMovementList = new ArrayList<>();
					for(PickupLine pickupLine : dbPickupLine ){
						InventoryMovement inventoryMovement = createInventoryMovement(pickupLine, movementDocumentNo, stBin,
								movementQtyValue, loginUserID, true);
						newInventoryMovementList.add(inventoryMovement);
					}
					if (newInventoryMovementList.size() > 0 ){
						inventoryMovementRepository.saveAll(newInventoryMovementList);
						log.info("InventoryMovement list created.");
					}
				}
				return outboundLineByStatus57List;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			String errorFromAXAPI = axapiResponse.getMessage();
//			throw new BadRequestException("Error from AX: " + errorFromAXAPI);
			log.info("Error from AX:" + errorFromAXAPI);
		}
		return null;
		
		////////////////////////////////////////////OLD_CODE////////////////////////////////////////////////////////////////////////
		/*
		 * Pass the selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/ITEM_CODE/OB_LINE_NO values in OUTBOUNDLINE table and 
		 * Validate STATUS_ID = 55 or 47 or 51, if yes
		 */
//		List<OutboundLine> responseOutboundLineList = new ArrayList<>();
//		for (OutboundLine outboundLine : outboundLineList) {
//			if (outboundLine.getStatusId() == 57L || outboundLine.getStatusId() == 47L || outboundLine.getStatusId() == 51L
//					|| outboundLine.getStatusId() == 41L) {
//				/*---------------------AXAPI-integration----------------------------------------------------------*/
//				// Checking the AX-API response
//				if (axapiResponse.getStatusCode() != null && axapiResponse.getStatusCode().equalsIgnoreCase("200")) {
//					if (outboundLine.getStatusId() == 57L) {
//						try {
//							// Pass the above values in OUTBOUNDHEADER and OUTBOUNDLINE tables and update STATUS_ID as "59"
//							try {
//								outboundLine.setStatusId(59L);
//								outboundLine.setUpdatedBy(loginUserID);
//								outboundLine.setUpdatedOn(new Date());
//								outboundLine = outboundLineRepository.save(outboundLine);
//								log.info("outboundLine updated : " + outboundLine);
//								responseOutboundLineList.add(outboundLine);
//							} catch (Exception e) {
//								e.printStackTrace();
//								log.error("deliveryConfirmation---OutboundLine update error: " + outboundLine);
//							}
//							
//							// OUTBOUNDHEADER update
//							try {
//								UpdateOutboundHeader updateOutboundHeader = new UpdateOutboundHeader();
//								updateOutboundHeader.setStatusId(59L);
//								updateOutboundHeader.setDeliveryConfirmedOn(new Date());
//								updateOutboundHeader.setUpdatedOn(new Date());
//								updateOutboundHeader.setUpdatedBy(loginUserID);
//								OutboundHeader updatedOutboundHeader = 
//										outboundHeaderService.updateOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, updateOutboundHeader, loginUserID);
//								log.info("updatedOutboundHeader updated : " + updatedOutboundHeader);
//							} catch (Exception e) {
//								e.printStackTrace();
//								log.error("deliveryConfirmation---UpdateOutboundHeader update error: [args]" + preOutboundNo + "," + refDocNumber + "," + partnerCode);
//							}
//							
//							//---------------------------------------------------------------------------------------------------------------------------
//							// QUALITYLINE - NOT REQUIRED TO UPDATE THE STATUS
//							try {
//								List<QualityLine> dbQualityLine = 
//										qualityLineService.getQualityLineForUpdateForDeliverConformation(warehouseId, preOutboundNo, refDocNumber, partnerCode, outboundLine.getLineNumber(), outboundLine.getItemCode());
//								
//								/*-----------------Inventory Updates---------------------------*/
//								// String warehouseId, String itemCode, Long binClassId
//								Long BIN_CL_ID = 5L;
//								for(QualityLine qualityLine : dbQualityLine) {
//									List<Inventory> inventoryList = inventoryService.getInventoryForDeliveryConfirmtion (outboundLine.getWarehouseId(),
//											outboundLine.getItemCode(), qualityLine.getPickPackBarCode(), BIN_CL_ID); //pack_bar_code
//									for(Inventory inventory : inventoryList) {
//										Double INV_QTY = inventory.getInventoryQuantity() - qualityLine.getQualityQty();
//
//										if (INV_QTY < 0) {
//											INV_QTY = 0D;
//										}
//
//										if (INV_QTY >= 0) {
//											inventory.setInventoryQuantity(INV_QTY);
//
//											// INV_QTY > 0 then, update Inventory Table
//											inventory = inventoryRepository.save(inventory);
//										}
//									}
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//								log.info("ERROR: Update QualityHeader & line error: [args] " + warehouseId  + "," + preOutboundNo  + "," + refDocNumber  + "," + partnerCode  + "," + 
//										outboundLine.getLineNumber()  + "," +  outboundLine.getItemCode());
//							}
//							
//							try {
//								// PREOUTBOUNDLINE
//								UpdatePreOutboundLine updatePreOutboundLine = new UpdatePreOutboundLine();
//								updatePreOutboundLine.setStatusId(59L);
//								PreOutboundLine updatedPreOutboundLine = preOutboundLineService.updatePreOutboundLine(warehouseId, refDocNumber,
//										preOutboundNo, partnerCode, loginUserID, updatePreOutboundLine);
//							} catch (Exception e) {
//								e.printStackTrace();
//								log.info("Update PreOutboundLine error: [args] " + warehouseId  + "," + preOutboundNo  + "," + refDocNumber  + "," + partnerCode );
//							}
//							
//							try {
//								// PREOUTBOUNDHEADER
//								UpdatePreOutboundHeader updatePreOutboundHeader = new UpdatePreOutboundHeader();
//								updatePreOutboundHeader.setStatusId(59L);
//								PreOutboundHeader updatedPreOutboundHeader = preOutboundHeaderService.updatePreOutboundHeader(warehouseId, refDocNumber, 
//										preOutboundNo, partnerCode, loginUserID, updatePreOutboundHeader);
//								log.info("updatedPreOutboundHeader updated : " + updatedPreOutboundHeader);
//							} catch (Exception e) {
//								e.printStackTrace();
//								log.info("Update PreOutboundHeader error: [args] " + warehouseId  + "," + preOutboundNo  + "," + refDocNumber  + "," + partnerCode );
//							}
//							
//							/*-------------------Inserting record in InventoryMovement-------------------------------------*/
//							Long BIN_CLASS_ID = 5L;
//							AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//							StorageBin storageBin = mastersService.getStorageBin(outboundLine.getWarehouseId(), BIN_CLASS_ID, 
//									authTokenForMastersService.getAccess_token());
//							
//							String movementDocumentNo = outboundLine.getRefDocNumber();
//							String stBin = storageBin.getStorageBin();
//							String movementQtyValue = "N";
//							List<PickupLine> dbPickupLine = 
//									pickupLineService.getPickupLineForUpdateConfirmation (warehouseId, preOutboundNo, refDocNumber, partnerCode, outboundLine.getLineNumber(), outboundLine.getItemCode());
//							if(dbPickupLine != null) {
//								for(PickupLine pickupLine : dbPickupLine ){
//									InventoryMovement inventoryMovement = createInventoryMovement(pickupLine, movementDocumentNo, stBin,
//											movementQtyValue, loginUserID, true);
//									log.info("InventoryMovement created : " + inventoryMovement);
//								}
//							}
//						} catch (Exception e) {
//							log.info("Updating respective tables having Error : " + e.getLocalizedMessage());
//						}
//					}
//				} else {
//					String errorFromAXAPI = axapiResponse.getMessage();
//					throw new BadRequestException("Error from AX: " + errorFromAXAPI);
//				}
//			} else {
//				throw new BadRequestException("Order is not completely Processed.");
//			}
//		}
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param refDocNumber
	 * @param preOutboundNo
	 * @param STATUS_ID_59
	 * @param lineNumbers
	 * @param loginUserID
	 */
	@Retryable(
			value = {CannotAcquireLockException.class, LockAcquisitionException.class}, 
			maxAttempts = 3, 
			backoff = @Backoff(delay = 3000, multiplier = 2, maxDelay = 10000)
			)
	private void deliveryConfirmStatusUpdate (String warehouseId, String preOutboundNo, String refDocNumber, Long STATUS_ID_59, List<Long> lineNumbers, String loginUserID) {
        try {
            AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
            outboundLineRepository.updateOutboundLineStatus (warehouseId, refDocNumber, STATUS_ID_59, lineNumbers, new Date());
            log.info("OutboundLine updated ");

            //----------------Outbound Header update----------------------------------------------------------------------------------------
            outboundHeaderRepository.updateOutboundHeaderStatus (warehouseId, refDocNumber, STATUS_ID_59, new Date());
            OutboundHeader isOrderConfirmedOutboundHeader = outboundHeaderService.getOutboundHeader(warehouseId, preOutboundNo, refDocNumber);
            log.info("OutboundHeader updated----1---> : " + isOrderConfirmedOutboundHeader.getRefDocNumber() + "---" + isOrderConfirmedOutboundHeader.getStatusId());
            if (isOrderConfirmedOutboundHeader.getStatusId() != 59L) {
                log.info("OutboundHeader is still updated not updated.");
                log.info("Updating again OutboundHeader.");
                isOrderConfirmedOutboundHeader.setStatusId(STATUS_ID_59);
                isOrderConfirmedOutboundHeader.setUpdatedBy(loginUserID);
                isOrderConfirmedOutboundHeader.setUpdatedOn(new Date());
                isOrderConfirmedOutboundHeader.setDeliveryConfirmedOn(new Date());
                outboundHeaderRepository.saveAndFlush(isOrderConfirmedOutboundHeader);
                log.info("OutboundHeader updated---2---> : " + isOrderConfirmedOutboundHeader.getRefDocNumber() + "---" + isOrderConfirmedOutboundHeader.getStatusId());
            }
            //----------------Preoutbound Line----------------------------------------------------------------------------------------------
            preOutboundLineRepository.updatePreOutboundLineStatus(warehouseId, refDocNumber, STATUS_ID_59);
            log.info("PreOutbound Line updated");

            //----------------Preoutbound Header--------------------------------------------------------------------------------------------
            StatusId idStatus = idmasterService.getStatus(STATUS_ID_59, warehouseId, authTokenForIDService.getAccess_token());
            preOutboundHeaderRepository.updatePreOutboundHeaderStatus(warehouseId, refDocNumber, STATUS_ID_59, idStatus.getStatus());
            log.info("PreOutbound Header updated");
        } catch (Exception e) {
            log.error("Exception while delivery confirmation status update---> " + preOutboundNo + " | " + refDocNumber + " | " + warehouseId);
			e.printStackTrace();
        }
    }
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumbers
	 * @param itemCodes
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
//	@Transactional
//	@Retryable(value = SQLException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
	private void inventoryUpdateBeforeAXSubmit (String warehouseId, String preOutboundNo, String refDocNumber, 
			String partnerCode, List<Long> lineNumbers, List<String> itemCodes) throws IllegalAccessException, InvocationTargetException {
		List<PickupLine> dbPickupLines = 
				pickupLineService.getPickupLine (warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumbers, itemCodes);
		log.info("dbPickupLine: " + dbPickupLines.size());
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		for (PickupLine dbPickupLine : dbPickupLines) {
			/*
			 * Inventory Update 
			 */
			Inventory inventory = inventoryService.getInventory (dbPickupLine.getWarehouseId(),
					dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(), dbPickupLine.getPickedStorageBin());
			log.info("---inventory record queried: " + inventory);
			log.info("---dbPickupLine------>: " + dbPickupLine);
			if (inventory != null) {
				if (dbPickupLine.getAllocatedQty() > 0D) {
					Double INV_QTY_ERR = 0D;
					Double ALLOC_QTY_ERR = 0D;
					try {
						Double INV_QTY = ((inventory.getInventoryQuantity() + dbPickupLine.getAllocatedQty())
								- dbPickupLine.getPickConfirmQty());
						Double ALLOC_QTY = (inventory.getAllocatedQuantity() - dbPickupLine.getAllocatedQty());

						log.info("---inventoryUpdateBeforeAXSubmit----INV_QTY---->: " + INV_QTY);
						log.info("---inventoryUpdateBeforeAXSubmit-----ALLOC_QTY--->: " + ALLOC_QTY);

						/*
						 * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
						 */
						// Start
						if (INV_QTY < 0D) {
							INV_QTY = 0D;
						}

						if (ALLOC_QTY < 0D) {
							ALLOC_QTY = 0D;
						}

						INV_QTY_ERR = INV_QTY;
						ALLOC_QTY_ERR = ALLOC_QTY;

//						inventory.setInventoryQuantity(INV_QTY);
//						inventory.setAllocatedQuantity(ALLOC_QTY);

						// INV_QTY > 0 then, update Inventory Table
//						inventory = inventoryRepository.save(inventory);
//						log.info("inventory updated : " + inventory);
						
						/*
						 * Inventory update is not working as expected
						 */
//						inventoryRepository.updateInventoryUpdateProcedure(dbPickupLine.getWarehouseId(),
//								dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(),
//								dbPickupLine.getPickedStorageBin(), INV_QTY, ALLOC_QTY);
						
						inventoryRepository.updateInventory(dbPickupLine.getWarehouseId(),
								dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(),
								dbPickupLine.getPickedStorageBin(), INV_QTY, ALLOC_QTY);
						log.info("----updateInventory-is-done-->: ");
						
						Inventory updatedInventory = inventoryService.getInventory (dbPickupLine.getWarehouseId(),
								dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(), dbPickupLine.getPickedStorageBin());
						log.info("----After-update-using stored procedure---->: " + updatedInventory);

						if (INV_QTY == 0) {
							// Setting up statusId = 0
							try {
								// Check whether Inventory has record or not
								Inventory inventoryByStBin = inventoryService.getInventoryByStorageBin(warehouseId, inventory.getStorageBin());
								if (inventoryByStBin == null) {
									StorageBin dbStorageBin = mastersService.getStorageBin(inventory.getStorageBin(),
											dbPickupLine.getWarehouseId(), authTokenForMastersService.getAccess_token());
									dbStorageBin.setStatusId(0L);
									dbStorageBin.setWarehouseId(dbPickupLine.getWarehouseId());
									mastersService.updateStorageBin(inventory.getStorageBin(), dbStorageBin, "",
											authTokenForMastersService.getAccess_token());
								}
							} catch (Exception e) {
								log.error("updateStorageBin Error :" + e.toString());
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						String objectData = warehouseId + "|" + preOutboundNo + "|" + refDocNumber + "|" + partnerCode + "|" + dbPickupLine.getPickupNumber();				
						transactionErrorService.createTransactionError("INVENTORY", "createPickupLine | Inventory Update", e.getMessage(), e.getLocalizedMessage(), objectData, "");	
						log.error("Inventory Update Error:" + e.toString());
						e.printStackTrace();
						
						// Inventory Error Handling
						InventoryTrans newInventoryTrans = new InventoryTrans();
						BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
						
						newInventoryTrans.setWarehouseId(dbPickupLine.getWarehouseId());	
						newInventoryTrans.setPackBarcodes(dbPickupLine.getPickedPackCode());
						newInventoryTrans.setItemCode(dbPickupLine.getItemCode());	
						newInventoryTrans.setStorageBin(dbPickupLine.getPickedStorageBin());			
						newInventoryTrans.setInventoryQuantity(INV_QTY_ERR);
						newInventoryTrans.setAllocatedQuantity(ALLOC_QTY_ERR);
						newInventoryTrans.setReRun(0L);	
						InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
						log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
					}
				}

				if (dbPickupLine.getAllocatedQty() == null || dbPickupLine.getAllocatedQty() == 0D) {
					Double INV_QTY_ERR = 0D;
					Double INV_QTY;
					try {
						INV_QTY = inventory.getInventoryQuantity() - dbPickupLine.getPickConfirmQty();
						/*
						 * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
						 */
						// Start
						if (INV_QTY < 0D) {
							INV_QTY = 0D;
						}
						// End
						inventory.setInventoryQuantity(INV_QTY);
//						inventory = inventoryRepository.save(inventory);
//						log.info("inventory updated : " + inventory);

						INV_QTY_ERR = INV_QTY;
						inventoryRepository.updateInventoryUpdateProcedure(dbPickupLine.getWarehouseId(),
								dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(),
								dbPickupLine.getPickedStorageBin(), INV_QTY);
						log.info("inventory updated using stored procedure: " + inventory);
						
						//-------------------------------------------------------------------
						// PASS PickedConfirmedStBin, WH_ID to inventory
						// 	If inv_qty && alloc_qty is zero or null then do the below logic.
						//-------------------------------------------------------------------
						Double[] inventoryBySTBIN = inventoryService.getInventoryCountByStorageBin (warehouseId, dbPickupLine.getPickedStorageBin());
						if (inventoryBySTBIN != null && (inventoryBySTBIN [0] == null || inventoryBySTBIN [0] == 0D) 
								&& (inventoryBySTBIN [1] == null || inventoryBySTBIN [1] == 0D)) {
							try {
								// Setting up statusId = 0
								StorageBin dbStorageBin = mastersService.getStorageBin(inventory.getStorageBin(),
										dbPickupLine.getWarehouseId(), authTokenForMastersService.getAccess_token());
								dbStorageBin.setStatusId(0L);
								mastersService.updateStorageBin(inventory.getStorageBin(), dbStorageBin, "",
										authTokenForMastersService.getAccess_token());
							} catch (Exception e) {
								log.error("updateStorageBin Error :" + e.toString());
								e.printStackTrace();
							}
						}
					} catch (Exception e1) {
						String objectData = warehouseId + "|" + preOutboundNo + "|" + refDocNumber + "|" + partnerCode + "|" + dbPickupLine.getPickupNumber();
						transactionErrorService.createTransactionError("INVENTORY", 
								"createPickupLine | Inventory Update | AllocatedQty is null OR AllocatedQty is zero", 
								e1.getMessage(), e1.getLocalizedMessage(), objectData, "");	
						log.error("Inventory cum StorageBin update: Error :" + e1.toString());
						e1.printStackTrace();
						
						// Inserting the failed Inventory in Inventory Trans Table
						// Inventory Error Handling
						InventoryTrans newInventoryTrans = new InventoryTrans();
						BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
						
						newInventoryTrans.setWarehouseId(dbPickupLine.getWarehouseId());	
						newInventoryTrans.setPackBarcodes(dbPickupLine.getPickedPackCode());
						newInventoryTrans.setItemCode(dbPickupLine.getItemCode());	
						newInventoryTrans.setStorageBin(dbPickupLine.getPickedStorageBin());			
						newInventoryTrans.setInventoryQuantity(INV_QTY_ERR);
						newInventoryTrans.setAllocatedQuantity(0D);
						newInventoryTrans.setReRun(0L);	
						InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
						log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
					}
				}
			} // End of Inventory Update
		}
	}
	
	/**
	 * 
	 */
	@Scheduled(fixedDelayString = "PT1M", initialDelayString = "PT2M")
	private void updateErroredOutInventory () {
		List<InventoryTrans> inventoryTransList = inventoryTransRepository.findByReRun(0L);
		inventoryTransList.stream().forEach( it -> {
			log.info("----updateErroredOutInventory-->: " + it);
			inventoryRepository.updateInventory(it.getWarehouseId(),
					it.getPackBarcodes(), it.getItemCode(),
					it.getStorageBin(), it.getInventoryQuantity(), it.getAllocatedQuantity());
			it.setReRun(1L);
			inventoryTransRepository.save(it);		
			log.info("----updateInventoryTrans-is-done-->: " + it);
		});
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param loginUserID
	 * @param updateOutboundLine
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public OutboundLine updateOutboundLine (String warehouseId, String preOutboundNo, String refDocNumber, 
			String partnerCode, Long lineNumber, String itemCode, String loginUserID, UpdateOutboundLine updateOutboundLine) 
					throws IllegalAccessException, InvocationTargetException {
		OutboundLine outboundLine = getOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode);
		BeanUtils.copyProperties(updateOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(updateOutboundLine));
		outboundLine.setUpdatedBy(loginUserID);
		outboundLine.setUpdatedOn(new Date());
		outboundLineRepository.save(outboundLine);
		return outboundLine;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumbers
	 * @param itemCode
	 * @param loginUserID
	 * @param updateOutboundLine
	 * @return 
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<OutboundLine> updateOutboundLines (String loginUserID, List<UpdateOutboundLine> updateOutboundLines) 
					throws IllegalAccessException, InvocationTargetException {
		List<OutboundLine> updatedOutboundLines = new ArrayList<>();
		List<Long> lineNumbers = new ArrayList<>();
		List<String> itemCodes = new ArrayList<>();
		String warehouseId = null;
		String preOutboundNo = null;
		String refDocNumber = null;
		String partnerCode = null;
		for (UpdateOutboundLine updateOutboundLine : updateOutboundLines) {
			OutboundLine outboundLine = getOutboundLine(updateOutboundLine.getWarehouseId(), updateOutboundLine.getPreOutboundNo(), 
					updateOutboundLine.getRefDocNumber(), updateOutboundLine.getPartnerCode(), updateOutboundLine.getLineNumber(), 
					updateOutboundLine.getItemCode());
			BeanUtils.copyProperties(updateOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(updateOutboundLine));
			outboundLine.setUpdatedBy(loginUserID);
			outboundLine.setUpdatedOn(new Date());
			outboundLine = outboundLineRepository.save(outboundLine);
			updatedOutboundLines.add(outboundLine);
			
			warehouseId = updateOutboundLine.getWarehouseId();
			preOutboundNo = updateOutboundLine.getPreOutboundNo();
			refDocNumber = updateOutboundLine.getRefDocNumber();
			partnerCode = updateOutboundLine.getPartnerCode();
			lineNumbers.add(updateOutboundLine.getLineNumber());
			itemCodes.add(updateOutboundLine.getItemCode());
		}
		log.info("-----outboundLines-updated----> : " + updatedOutboundLines);
		return updatedOutboundLines;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @param loginUserID
	 * @param statusId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public boolean updateOutboundLineByProc (String warehouseId, String preOutboundNo, String refDocNumber, 
			String partnerCode, Long lineNumber, String itemCode, String loginUserID, Long statusId) 
					throws IllegalAccessException, InvocationTargetException {
		outboundLineRepository.updateStatusIdByProcedure(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, 
				itemCode, statusId, loginUserID);
		log.info("------updateOutboundLineByProc-------> : " + statusId + " updated...");		
		return true;
	}
	
	/**
	 * WarehouseId, PreOutboundNo, RefDocNumber, PartnerCode, LineNumber, ItemCode, DeliveryQty, DeliveryOrderNo, StatusId(57L);
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @param loginUserID
	 * @param statusId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public boolean updateOutboundLineByQLCreateProc (String warehouseId, String preOutboundNo, String refDocNumber, 
			String partnerCode, Long lineNumber, String itemCode, Double deliveryQty, String deliveryOrderNo, Long statusId) 
					throws IllegalAccessException, InvocationTargetException {
		outboundLineRepository.updateOBlineByQLCreateProcedure(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, 
				itemCode, deliveryQty, deliveryOrderNo, statusId);
		log.info("------updateOutboundLineByProc-------> : " + statusId + " updated...");		
		return true;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param loginUserID
	 * @param updateOutboundLine
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<OutboundLine> updateOutboundLine (String warehouseId, String preOutboundNo, String refDocNumber, 
			String partnerCode, String loginUserID, UpdateOutboundLine updateOutboundLine) 
					throws IllegalAccessException, InvocationTargetException {
		List<OutboundLine> outboundLines = getOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode);
		for (OutboundLine outboundLine : outboundLines) {
			BeanUtils.copyProperties(updateOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(updateOutboundLine));
			outboundLine.setUpdatedBy(loginUserID);
			outboundLine.setUpdatedOn(new Date());
			outboundLineRepository.save(outboundLine);
		}
		return outboundLines;
	}
	
	/**
	 * deleteOutboundLine
	 * @param loginUserID 
	 * @param lineNumber
	 */
	public void deleteOutboundLine (String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber, 
			String itemCode, String loginUserID) {
		OutboundLine outboundLine = getOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode);
		if ( outboundLine != null) {
			outboundLine.setDeletionIndicator(1L);
			outboundLine.setUpdatedBy(loginUserID);
			outboundLineRepository.save(outboundLine);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
		}
	}
	
	/**
	 * 
	 * @param refDocNumber
	 * @param itemCode
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
	public List<OutboundReversal> doReversal(String refDocNumber, String itemCode, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		List<OutboundLine> outboundLineList = 
				outboundLineRepository.findByRefDocNumberAndItemCodeAndDeletionIndicator(refDocNumber, itemCode, 0L);
		log.info("outboundLineList---------> : " + outboundLineList);
		
		List<OutboundReversal> outboundReversalList = new ArrayList<>();
		for (OutboundLine outboundLine : outboundLineList) {
			Warehouse warehouse = getWarehouse(outboundLine.getWarehouseId());
			/*--------------STEP 1-------------------------------------*/
			// If STATUS_ID = 57 - Reversal of QC/Picking confirmation
			if (outboundLine.getStatusId() == 57L) {
				//Get current status id for inventory update
				Long outboundLineStatusIdBeforeUpdate = outboundLine.getStatusId();

				outboundLine.setDeliveryQty(0D);
				outboundLine.setReversedBy(loginUserID);
				outboundLine.setReversedOn(new Date());
				outboundLine.setStatusId(47L);
				outboundLine = outboundLineRepository.save(outboundLine);
				log.info("outboundLine updated : " + outboundLine);
				
				/*--------------STEP 2-------------------------------------
				 * Pass WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values fetched 
				 * from OUTBOUNDHEADER and OUTBOUNDLINE table into QCLINE table  and update STATUS_ID = 56								
				 */
				List<QualityLine> qualityLine = qualityLineService.deleteQualityLineForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
						outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(), 
						outboundLine.getItemCode(), loginUserID);
				log.info("QualityLine----------Deleted-------> : " + qualityLine);
				
				List<OutboundLineInterim> outboundLineInterim = qualityLineService.deleteOutboundLineInterimForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
						outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(), 
						outboundLine.getItemCode(), loginUserID);
				log.info("OutboundLineInterim----------Deleted-------> : " + outboundLineInterim);

				if(qualityLine != null && qualityLine.size() > 0) {
					for (QualityLine qualityLineData : qualityLine) {
						List<QualityHeader> qualityHeader = qualityHeaderService.deleteQualityHeaderForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(), refDocNumber,
								qualityLineData.getQualityInspectionNo(), qualityLineData.getActualHeNo(), loginUserID);
						log.info("QualityHeader----------Deleted-------> : " + qualityHeader);
					}
				}
				
				/*---------------STEP 3------------------------------------
				 * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from QCLINE table and 
				 * pass the keys in PICKUPLINE table  and update STATUS_ID = 53									
				 */
				// HAREESH 06/09/2022 change from single line delete to multiple line delete since there maybe be multiple records for same parameter
				List<PickupLine> pickupLineList = pickupLineService.deletePickupLineForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
						outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
						outboundLine.getItemCode(), loginUserID);
				log.info("PickupLine----------Deleted-------> : ");
				
				/*---------------STEP 3.1-----Inventory update-------------------------------
				 * Pass WH_ID/_ITM_CODE/ST_BIN of BIN_CLASS_ID=5/PACK_BARCODE as PICK_PACK_BARCODE of PICKUPLINE 
				 * in INVENTORY table and update INV_QTY as (INV_QTY - DLV_QTY ) and 
				 * delete the record if INV_QTY = 0 (Update 1)								
				 */
				List<Long> list_50_StatusId = new ArrayList<>();
				List<Long> list_51_StatusId = new ArrayList<>();
				for(PickupLine pickupLine : pickupLineList) {
					// StatusID - 50
					if (pickupLine.getStatusId() == 50L) {
						list_50_StatusId.add(pickupLine.getStatusId());
					}
					// StatusID - 51
					if (pickupLine.getStatusId() == 51L) {
						list_51_StatusId.add(pickupLine.getStatusId());
					}
				}
				log.info("------list_50_StatusId-------: " + list_50_StatusId);
				log.info("------list_51_StatusId-------: " + list_51_StatusId);
				
				if(pickupLineList != null && !pickupLineList.isEmpty()) {
					for(PickupLine pickupLine : pickupLineList) {
						//Get pickupheader for inventory update
						List<PickupHeader> pickupHeader = pickupHeaderService.getPickupHeaderForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
								outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
								pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode());
						log.info("get pickupHeader : " + pickupHeader);

						/*---------------STEP 5-----OrderManagement update-------------------------------
						 * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from PICKUPHEADER table
						 * and pass the keys in ORDERMANAGEMENTLINE table and update STATUS_ID as 47
						 */
						// HAREESH 07/09/2022 change from single line get to multiple line get since there maybe be multiple records for same parameter
						if(pickupHeader != null) {
							for (PickupHeader pickupHeaderData : pickupHeader) {
								List<OrderManagementLine> orderManagementLine = updateOrderManagementLineForReversal(pickupHeaderData, loginUserID);
								log.info("orderManagementLine updated : " + orderManagementLine);
							}
						}

						// Inventory Update
						if (!list_50_StatusId.isEmpty() && list_51_StatusId.isEmpty()) {
							Inventory inventory = updateInventory1(pickupLine,outboundLineStatusIdBeforeUpdate);
							if(inventory != null) {
								try {
									inventory = inventoryService.getInventory(pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(),
											pickupLine.getItemCode(), pickupLine.getPickedStorageBin());
									if(inventory != null && pickupHeader != null){
										Double INV_QTY = inventory.getInventoryQuantity() + pickupLine.getPickConfirmQty();
										inventory.setInventoryQuantity(INV_QTY);
										inventory = inventoryRepository.save(inventory);
										log.info("inventory updated : " + inventory);
									}
								} catch (Exception e) {
									String objectData = (pickupLine.getWarehouseId() + "|" + pickupLine.getPickedPackCode() + "|" +
											pickupLine.getItemCode() + "|" + pickupLine.getPickedStorageBin());
									transactionErrorService.createTransactionError("INVENTORY", "Reversal | Inventory Update Error | STEP 3.2", 
											e.getMessage(), e.getLocalizedMessage(), objectData, loginUserID);
									log.info("inventory update error: " + e.toString());
									e.printStackTrace();
								}
							}
						}
						
						if (!list_51_StatusId.isEmpty()) {
							Inventory inventory = updateInventory1(pickupLine,outboundLineStatusIdBeforeUpdate);
							if(inventory != null) {
								try {
									inventory = inventoryService.getInventory(pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(),
											pickupLine.getItemCode(), pickupLine.getPickedStorageBin());
									if(inventory != null && pickupHeader != null){
										Double INV_QTY = inventory.getInventoryQuantity() + pickupLine.getPickConfirmQty();
										inventory.setInventoryQuantity(INV_QTY);
										inventory = inventoryRepository.save(inventory);
										log.info("inventory updated : " + inventory);
									}
								} catch (Exception e) {
									String objectData = (pickupLine.getWarehouseId() + "|" + pickupLine.getPickedPackCode() + "|" +
											pickupLine.getItemCode() + "|" + pickupLine.getPickedStorageBin());
									transactionErrorService.createTransactionError("INVENTORY", "Reversal | Inventory Update Error | STEP 3.2", 
											e.getMessage(), e.getLocalizedMessage(), objectData, loginUserID);
									log.info("inventory update error: " + e.toString());
									e.printStackTrace();
								}
							}
						}

						/*------------------------Record insertion in Outbound Reversal table----------------------------*/
						/////////RECORD-1/////////////////////////////////////////////////////////////////////////////////
						for (QualityLine qualityLineData : qualityLine) {
							String reversalType = "QUALITY";
							Double reversedQty = qualityLineData.getQualityQty();
							OutboundReversal createdOutboundReversal = createOutboundReversal (warehouse, reversalType, refDocNumber,
									outboundLine.getPartnerCode(), itemCode, qualityLineData.getPickPackBarCode(), reversedQty,
									60L, loginUserID);
							outboundReversalList.add(createdOutboundReversal);

							/////////RECORD-2/////////////////////////////////////////////////////////////////////////////////
							reversalType = "PICKING";
							reversedQty = pickupLine.getPickConfirmQty();
							createdOutboundReversal = createOutboundReversal (warehouse, reversalType, refDocNumber,
									outboundLine.getPartnerCode(), itemCode, qualityLineData.getPickPackBarCode(), reversedQty,
									outboundLine.getStatusId(), loginUserID);
							outboundReversalList.add(createdOutboundReversal);
						}

						/*-----------------------InventoryMovement----------------------------------*/
						// Inserting record in InventoryMovement------UPDATE 1-----------------------
						AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
						Long BIN_CLASS_ID = 5L;
						StorageBin storageBin = mastersService.getStorageBin(outboundLine.getWarehouseId(), BIN_CLASS_ID, authTokenForMastersService.getAccess_token());
						String movementDocumentNo = outboundLine.getDeliveryOrderNo();
						String stBin = storageBin.getStorageBin();
						String movementQtyValue = "N";
						InventoryMovement inventoryMovement = createInventoryMovement(pickupLine, movementDocumentNo, stBin,
								movementQtyValue, loginUserID, false);
						log.info("InventoryMovement created for update 1-->: " + inventoryMovement);

						/*----------------------UPDATE-2------------------------------------------------*/
						// Inserting record in InventoryMovement------UPDATE 2-----------------------
						movementDocumentNo = pickupLine.getPickupNumber();
						stBin = pickupLine.getPickedStorageBin();
						movementQtyValue = "P";
						inventoryMovement = createInventoryMovement(pickupLine, movementDocumentNo, stBin,
								movementQtyValue, loginUserID, false);
						log.info("InventoryMovement created for update 2-->: " + inventoryMovement);
					};
					
					//Delete pickupheader after inventory update
					for(PickupLine pickupLine : pickupLineList) {
						/*---------------STEP 4-----PickupHeader update-------------------------------
						 * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from PICKUPLINE table
						 * and pass the keys in PICKUPHEADER table and Delete PickUpHeader
						 */
						List<PickupHeader> pickupHeader = pickupHeaderService.deletePickupHeaderForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
								outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
								pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode(),loginUserID);
						log.info("pickupHeader deleted : " + pickupHeader);
					}
				}
			}
			
			/*-----------------------------Next Process----------------------------------------------------------*/
			// If STATUS_ID = 50 - Reversal of Picking Confirmation
			// HAREESH 27-08-2022 added status id 51
			if (outboundLine.getStatusId() == 50L || outboundLine.getStatusId() == 51L) {
				/*----------------------STEP 1------------------------------------------------
				 * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from OUTBOUNDLINE table and
				 * pass the keys in PICKUPLINE table and update STATUS_ID=53 and Delete the record
				 */
				// HAREESH 25/11/2022 update outboundline
				//Get current status id for inventory update
				Long outboundLineStatusIdBeforeUpdate = outboundLine.getStatusId();
				outboundLine.setStatusId(47L);
				outboundLine = outboundLineRepository.save(outboundLine);
				log.info("outboundLine updated : " + outboundLine);

				// HAREESH 07/09/2022 change from single line delete to multiple line delete since there maybe be multiple records for same parameter
				List<PickupLine> pickupLineList = pickupLineService.deletePickupLineForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
						outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
						outboundLine.getItemCode(), loginUserID);
				
				List<Long> list_50_StatusId = new ArrayList<>();
				List<Long> list_51_StatusId = new ArrayList<>();
				Double PICK_CNF_QTY_50 = 0D;
				for(PickupLine pickupLine : pickupLineList) {
					// StatusID - 50
					if (pickupLine.getStatusId() == 50L) {
						list_50_StatusId.add(pickupLine.getStatusId());
						PICK_CNF_QTY_50 = pickupLine.getPickConfirmQty();
					}
					// StatusID - 51
					if (pickupLine.getStatusId() == 51L) {
						list_51_StatusId.add(pickupLine.getStatusId());
					}
				}
				
				log.info("------list_50_StatusId-------: " + list_50_StatusId);
				log.info("------list_51_StatusId-------: " + list_51_StatusId);
				
				if (pickupLineList != null && !pickupLineList.isEmpty()) {
					for (PickupLine pickupLine : pickupLineList) {
						//get pickup header
						List<PickupHeader> pickupHeader = pickupHeaderService.getPickupHeaderForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
								outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
								pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode());
						log.info("get pickupHeader : " + pickupHeader);

						List<QualityLine> qualityLine = qualityLineService.deleteQualityLineForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
								outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
								outboundLine.getItemCode(),loginUserID);
						log.info("QualityLine----------Deleted-------> : " + qualityLine);

						// DELETE QUALITY_HEADER
						List<QualityHeader> dbQualityHeader = qualityHeaderService.getInitialQualityHeaderForReversal(outboundLine.getWarehouseId(),
								outboundLine.getPreOutboundNo(), outboundLine.getRefDocNumber(), pickupLine.getPickupNumber(), outboundLine.getPartnerCode());
						if (dbQualityHeader != null && dbQualityHeader.size() > 0) {
							for (QualityHeader qualityHeaderData : dbQualityHeader) {
								QualityHeader qualityHeader = qualityHeaderService.deleteQualityHeader(outboundLine.getWarehouseId(),
										outboundLine.getPreOutboundNo(), refDocNumber, qualityHeaderData.getQualityInspectionNo(),
										qualityHeaderData.getActualHeNo(), loginUserID);
								log.info("QualityHeader----------Deleted-------> : " + qualityHeader);
							}
						}

						/*---------------STEP 3-----OrderManagementLine update-------------------------------
						 * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from PICKUPHEADER table and
						 * pass the keys in ORDERMANAGEMENTLINE table  and update STATUS_ID as 47
						 */
						// HAREESH 07/09/2022 change from single line get to multiple line get since there maybe be multiple records for same parameter
						if(pickupHeader != null) {
							for (PickupHeader pickupHeaderData : pickupHeader) {
								List<OrderManagementLine> orderManagementLine = updateOrderManagementLineForReversal(pickupHeaderData, loginUserID);
								log.info("orderManagementLine updated : " + orderManagementLine);
							}
						}
						
						log.info("!list_50_StatusId.isEmpty() && list_51_StatusId.isEmpty()---> " + 
								list_50_StatusId.isEmpty() + "," + list_51_StatusId.isEmpty());
						// Inventory Update
						if (!list_50_StatusId.isEmpty() && list_51_StatusId.isEmpty()) {
							log.info("========1================ENTERIMNG========>");
							/*---------------STEP 3.1-----Inventory update-------------------------------
							 * Pass WH_ID/_ITM_CODE/ST_BIN of BIN_CLASS_ID=4/PACK_BARCODE as PICK_PACK_BARCODE of PICKUPLINE in
							 * INVENTORY table and update INV_QTY as (INV_QTY - PICK_CNF_QTY ) and
							 * delete the record If INV_QTY = 0 - (Update 1)
							 */
							updateInventory1(pickupLine, outboundLineStatusIdBeforeUpdate);
	
							/*---------------STEP 3.2-----Inventory update-------------------------------
							 * Pass WH_ID/_ITM_CODE/ST_BIN from PICK_ST_BIN/PACK_BARCODE from PICK_PACK_BARCODE of PICKUPLINE in
							 * INVENTORY table and update INV_QTY as (INV_QTY + PICK_CNF_QTY )- (Update 2)
							 */
							Inventory inventory = inventoryService.getInventory(pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(),
									pickupLine.getItemCode(), pickupLine.getPickedStorageBin());
	
							if(inventory != null) {
								// HAREESH -28-08-2022 change to update allocated qty
								try {
									Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0);
									Double ALLOC_QTY = (inventory.getAllocatedQuantity() != null ? inventory.getAllocatedQuantity() : 0);
									Double PICK_CNF_QTY = (pickupLine.getPickConfirmQty() != null ? pickupLine.getPickConfirmQty() : 0);
									INV_QTY = INV_QTY + PICK_CNF_QTY;
									ALLOC_QTY = ALLOC_QTY - PICK_CNF_QTY;
									log.info("----inventory update-50flow---INV_QTY-------- " + INV_QTY);
									log.info("----inventory update-50flow---ALLOC_QTY-------- " + ALLOC_QTY);
									log.info("----inventory update-51flow---PICK_CNF_QTY-------- " + PICK_CNF_QTY);
									
									if (INV_QTY < 0) {
										log.info("inventory qty calculated is less than 0: " + INV_QTY);
										INV_QTY = Double.valueOf(0);
									}
									inventory.setInventoryQuantity(INV_QTY);
									inventory.setAllocatedQuantity(ALLOC_QTY);
									inventory = inventoryRepository.save(inventory);
									log.info("inventory updated : " + inventory);
								} catch (Exception e) {
									String objectData = (pickupLine.getWarehouseId() + "|" + pickupLine.getPickedPackCode() + "|" +
											pickupLine.getItemCode() + "|" + pickupLine.getPickedStorageBin());
									transactionErrorService.createTransactionError("INVENTORY", "Reversal | Inventory Update - inventory qty calculated is < 0", 
											e.getMessage(), e.getLocalizedMessage(), objectData, loginUserID);
									log.info("inventory update error: " + e.toString());
									e.printStackTrace();
								}
							}
						}
						
						// Inventory Update
						if (!list_51_StatusId.isEmpty() && pickupLine.getStatusId() == 51L) {
							log.info("========2================ENTERIMNG========>");
							/*---------------STEP 3.1-----Inventory update-------------------------------
							 * Pass WH_ID/_ITM_CODE/ST_BIN of BIN_CLASS_ID=4/PACK_BARCODE as PICK_PACK_BARCODE of PICKUPLINE in
							 * INVENTORY table and update INV_QTY as (INV_QTY - PICK_CNF_QTY ) and
							 * delete the record If INV_QTY = 0 - (Update 1)
							 */
							updateInventory1(pickupLine, outboundLineStatusIdBeforeUpdate);
	
							/*---------------STEP 3.2-----Inventory update-------------------------------
							 * Pass WH_ID/_ITM_CODE/ST_BIN from PICK_ST_BIN/PACK_BARCODE from PICK_PACK_BARCODE of PICKUPLINE in
							 * INVENTORY table and update INV_QTY as (INV_QTY + PICK_CNF_QTY )- (Update 2)
							 */
							Inventory inventory = inventoryService.getInventory(pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(),
									pickupLine.getItemCode(), pickupLine.getPickedStorageBin());
	
							if(inventory != null) {
								// HAREESH -28-08-2022 change to update allocated qty
								try {
									Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0);
									Double ALLOC_QTY = (inventory.getAllocatedQuantity() != null ? inventory.getAllocatedQuantity() : 0);
									Double PICK_CNF_QTY = (pickupLine.getPickConfirmQty() != null ? pickupLine.getPickConfirmQty() : 0);
									
									// If the StatusID -> 51's PickupConfirmQty is 0 then consider the 50's PickupConfirmQty value
									if (PICK_CNF_QTY == 0D) {
										PICK_CNF_QTY = PICK_CNF_QTY_50;
									}
									INV_QTY = INV_QTY + PICK_CNF_QTY;
									ALLOC_QTY = ALLOC_QTY - PICK_CNF_QTY;
									log.info("----inventory update-51flow---INV_QTY-------- " + INV_QTY);
									log.info("----inventory update-51flow---ALLOC_QTY-------- " + ALLOC_QTY);
									log.info("----inventory update-51flow---PICK_CNF_QTY-------- " + PICK_CNF_QTY);
									
									if (INV_QTY < 0) {
										log.info("inventory qty calculated is less than 0: " + INV_QTY);
										INV_QTY = Double.valueOf(0);
									}
									inventory.setInventoryQuantity(INV_QTY);
									inventory.setAllocatedQuantity(ALLOC_QTY);
									inventory = inventoryRepository.save(inventory);
									log.info("------inventory updated------>: " + inventory);
								} catch (Exception e) {
									String objectData = (pickupLine.getWarehouseId() + "|" + pickupLine.getPickedPackCode() + "|" +
											pickupLine.getItemCode() + "|" + pickupLine.getPickedStorageBin());
									transactionErrorService.createTransactionError("INVENTORY", "Reversal | Inventory Update - inventory qty calculated is < 0", 
											e.getMessage(), e.getLocalizedMessage(), objectData, loginUserID);
									log.info("inventory update error: " + e.toString());
									e.printStackTrace();
								}
							}
						}

						/*------------------------Record insertion in Outbound Reversal table----------------------------*/
						/////////RECORD-1/////////////////////////////////////////////////////////////////////////////////
						String reversalType = "PICKING";
						Double reversedQty = pickupLine.getPickConfirmQty();
						OutboundReversal createdOutboundReversal = createOutboundReversal(warehouse, reversalType, refDocNumber, outboundLine.getPartnerCode(), itemCode,
								pickupLine.getPickedPackCode(), reversedQty, outboundLine.getStatusId(), loginUserID);
						outboundReversalList.add(createdOutboundReversal);
						/****************************************************************************/

						/*-----------------------InventoryMovement----------------------------------*/
						// Inserting record in InventoryMovement------UPDATE 1-----------------------
						AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
						Long BIN_CLASS_ID = 4L;
						StorageBin storageBin = mastersService.getStorageBin(outboundLine.getWarehouseId(), BIN_CLASS_ID, authTokenForMastersService.getAccess_token());

						String movementDocumentNo = pickupLine.getRefDocNumber();
						String stBin = storageBin.getStorageBin();
						String movementQtyValue = "N";
						InventoryMovement inventoryMovement = createInventoryMovement(pickupLine, movementDocumentNo, stBin,
								movementQtyValue, loginUserID, false);
						log.info("InventoryMovement created for update 1-->: " + inventoryMovement);

						/*----------------------UPDATE-2------------------------------------------------*/
						// Inserting record in InventoryMovement------UPDATE 2-----------------------
						movementDocumentNo = pickupLine.getPickupNumber();
						stBin = pickupLine.getPickedStorageBin();
						movementQtyValue = "P";
						inventoryMovement = createInventoryMovement(pickupLine, movementDocumentNo, stBin,
								movementQtyValue, loginUserID, false);
						log.info("InventoryMovement created for update 2-->: " + inventoryMovement);
					}

					//Delete pickupheader after inventory update
					for(PickupLine pickupLine : pickupLineList) {
						List<PickupHeader> pickupHeader = pickupHeaderService.deletePickupHeaderForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
								outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
								pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode(),loginUserID);
						log.info("pickupHeader deleted : " + pickupHeader);
					}
				}
			}
		}
		
		return outboundReversalList;
	}
	
	/**
	 * 
	 * @param qualityLine
	 * @param subMvtTypeId 
	 * @param movementDocumentNo
	 * @param storageBin
	 * @param movementQtyValue
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
//	private InventoryMovement createInventoryMovement (PickupLine pickupLine, 
//			String movementDocumentNo, String storageBin, String movementQtyValue, String loginUserID, boolean isFromDelivery ) 
//					throws IllegalAccessException, InvocationTargetException {
//		// Flag "isFromDelivery" is not used anywhere. 
//		AddInventoryMovement inventoryMovement = new AddInventoryMovement();
//		BeanUtils.copyProperties(pickupLine, inventoryMovement, CommonUtils.getNullPropertyNames(pickupLine));
//		
//		// MVT_TYP_ID
//		inventoryMovement.setMovementType(3L);
//		
//		// SUB_MVT_TYP_ID
//		inventoryMovement.setSubmovementType(5L);
//		
//		// PACK_BARCODE
//		inventoryMovement.setPackBarcodes(pickupLine.getPickedPackCode());
//		
//		// VAR_ID
//		inventoryMovement.setVariantCode(1L);
//		
//		// VAR_SUB_ID
//		inventoryMovement.setVariantSubCode("1");
//		
//		// STR_MTD
//		inventoryMovement.setStorageMethod("1");
//		
//		// STR_NO
//		inventoryMovement.setBatchSerialNumber("1");
//		
//		// MVT_DOC_NO
//		inventoryMovement.setMovementDocumentNo(movementDocumentNo);
//		
//		// ST_BIN
//		inventoryMovement.setStorageBin(storageBin);
//		
//		// MVT_QTY_VAL
//		inventoryMovement.setMovementQtyValue(movementQtyValue);
//		
//		// MVT_QTY
//		inventoryMovement.setMovementQty(pickupLine.getPickConfirmQty());
//		
//		// MVT_UOM
//		inventoryMovement.setInventoryUom(pickupLine.getPickUom());
//		
//		// BAL_OH_QTY
//		// PASS WH_ID/ITM_CODE/BIN_CL_ID and sum the INV_QTY for all selected inventory
//		List<Inventory> inventoryList = 
//				inventoryService.getInventory (pickupLine.getWarehouseId(), pickupLine.getItemCode(), 1L);
//		double sumOfInvQty = inventoryList.stream().mapToDouble(a->a.getInventoryQuantity()).sum();
//		inventoryMovement.setBalanceOHQty(sumOfInvQty);
//	
//		// IM_CTD_BY
//		inventoryMovement.setCreatedBy(pickupLine.getPickupConfirmedBy());
//		
//		// IM_CTD_ON
//		inventoryMovement.setCreatedOn(pickupLine.getPickupCreatedOn());
//
//		InventoryMovement createdInventoryMovement = 
//				inventoryMovementService.createInventoryMovement(inventoryMovement, loginUserID);
//		return createdInventoryMovement;
//	}
	
	private InventoryMovement createInventoryMovement (PickupLine pickupLine, 
			String movementDocumentNo, String storageBin, String movementQtyValue, String loginUserID, boolean isFromDelivery ) 
					throws IllegalAccessException, InvocationTargetException {
		// Flag "isFromDelivery" is not used anywhere. 
		InventoryMovement inventoryMovement = new InventoryMovement();
		BeanUtils.copyProperties(pickupLine, inventoryMovement, CommonUtils.getNullPropertyNames(pickupLine));
		
		// MVT_TYP_ID
		inventoryMovement.setMovementType(3L);
		
		// SUB_MVT_TYP_ID
		inventoryMovement.setSubmovementType(5L);
		
		// PACK_BARCODE
		inventoryMovement.setPackBarcodes(pickupLine.getPickedPackCode());
		
		// VAR_ID
		inventoryMovement.setVariantCode(1L);
		
		// VAR_SUB_ID
		inventoryMovement.setVariantSubCode("1");
		
		// STR_MTD
		inventoryMovement.setStorageMethod("1");
		
		// STR_NO
		inventoryMovement.setBatchSerialNumber("1");
		
		// MVT_DOC_NO
		inventoryMovement.setMovementDocumentNo(movementDocumentNo);
		
		// ST_BIN
		inventoryMovement.setStorageBin(storageBin);
		
		// MVT_QTY_VAL
		inventoryMovement.setMovementQtyValue(movementQtyValue);
		
		// MVT_QTY
		inventoryMovement.setMovementQty(pickupLine.getPickConfirmQty());
		
		// MVT_UOM
		inventoryMovement.setInventoryUom(pickupLine.getPickUom());
		
		// BAL_OH_QTY
		Double sumOfInvQty = inventoryService.getInventoryQtyCount(pickupLine.getWarehouseId(), pickupLine.getItemCode());
		inventoryMovement.setBalanceOHQty(sumOfInvQty);
	
		// IM_CTD_BY
		inventoryMovement.setCreatedBy(pickupLine.getPickupConfirmedBy());
		
		// IM_CTD_ON
		inventoryMovement.setCreatedOn(pickupLine.getPickupCreatedOn());

		return inventoryMovement;
	}

	/**
	 * 
	 * @param warehouse
	 * @param reversalType
	 * @param refDocNumber
	 * @param partnerCode
	 * @param itemCode
	 * @param packBarcode
	 * @param reversedQty
	 * @param statusId
	 * @param loginUserID
	 * @return 
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private OutboundReversal createOutboundReversal(Warehouse warehouse, String reversalType, String refDocNumber,
			String partnerCode, String itemCode, String packBarcode, Double reversedQty, Long statusId,
			String loginUserID) throws IllegalAccessException, InvocationTargetException {
		AuthToken idmasterAuthToken = authTokenService.getIDMasterServiceAuthToken();
		StatusId idStatus = idmasterService.getStatus(47L, warehouse.getWarehouseId(), idmasterAuthToken.getAccess_token());
		AddOutboundReversal newOutboundReversal = new AddOutboundReversal();
		
		// LANG_ID
		newOutboundReversal.setLanguageId(warehouse.getLanguageId());
		
		// C_ID - C_ID of the selected REF_DOC_NO
		newOutboundReversal.setCompanyCodeId(warehouse.getCompanyCode());
		
		// PLANT_ID
		newOutboundReversal.setPlantId(warehouse.getPlantId());
		
		// WH_ID
		newOutboundReversal.setWarehouseId(warehouse.getWarehouseId());
		
		// OB_REVERSAL_NO
		/*
		 * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE = 13  in NUMBERRANGE table and 
		 * fetch NUM_RAN_CURRENT value of FISCALYEAR=CURRENT YEAR and add +1 and insert
		 */
		long NUM_RAN_CODE = 13;
		String OB_REVERSAL_NO = getNextRangeNumber(NUM_RAN_CODE, warehouse.getWarehouseId());
		newOutboundReversal.setOutboundReversalNo(OB_REVERSAL_NO);
		
		// REVERSAL_TYPE
		newOutboundReversal.setReversalType(reversalType);
		
		// REF_DOC_NO
		newOutboundReversal.setRefDocNumber(refDocNumber);
		
		// PARTNER_CODE
		newOutboundReversal.setPartnerCode(partnerCode);
		
		// ITM_CODE
		newOutboundReversal.setItemCode(itemCode);
		
		// PACK_BARCODE
		newOutboundReversal.setPackBarcode(packBarcode);
		
		// REV_QTY
		newOutboundReversal.setReversedQty(reversedQty);
		
		// STATUS_ID
		newOutboundReversal.setStatusId(statusId);
		newOutboundReversal.setReferenceField10(idStatus.getStatus());
		
		OutboundReversal outboundReversal = 
				outboundReversalService.createOutboundReversal(newOutboundReversal, loginUserID);
		log.info("OutboundReversal created : " + outboundReversal);
		return outboundReversal;
	}
	

	/**
	 * 
	 * @param pickupHeader
	 * @param loginUserID
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private OrderManagementLine updateOrderManagementLine(PickupHeader pickupHeader, String loginUserID) throws IllegalAccessException, InvocationTargetException {
		UpdateOrderManagementLine updateOrderManagementLine = new UpdateOrderManagementLine();
		updateOrderManagementLine.setPickupNumber(null);
		updateOrderManagementLine.setStatusId(43L);

		//HAREESH - 27-08-2022 pickup number null update was reflected due to bean util null property ignore , so wrote the method here and manually set pickupnumber to null
//		OrderManagementLine orderManagementLine = orderManagementLineService.updateOrderManagementLine(pickupHeader.getWarehouseId(), pickupHeader.getPreOutboundNo(),
//				pickupHeader.getRefDocNumber(), pickupHeader.getPartnerCode(), pickupHeader.getLineNumber(),
//				pickupHeader.getItemCode(), loginUserID, updateOrderManagementLine);

		List<OrderManagementLine> dbOrderManagementLines = orderManagementLineService.getOrderManagementLine(pickupHeader.getWarehouseId(), pickupHeader.getPreOutboundNo(),
				pickupHeader.getRefDocNumber(), pickupHeader.getPartnerCode(), pickupHeader.getLineNumber(), pickupHeader.getItemCode());
		OrderManagementLine updatedOrderManagementLine = null;
		for (OrderManagementLine dbOrderManagementLine : dbOrderManagementLines) {
			BeanUtils.copyProperties(updateOrderManagementLine, dbOrderManagementLine, CommonUtils.getNullPropertyNames(updateOrderManagementLine));
			dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
			dbOrderManagementLine.setPickupNumber(null);
			dbOrderManagementLine.setStatusId(43L);
			dbOrderManagementLine.setPickupUpdatedOn(new Date());
			updatedOrderManagementLine = orderManagementLineRepository.save(dbOrderManagementLine);
			log.info("OrderManagementLine updated : " + updatedOrderManagementLine);
		}
		return updatedOrderManagementLine;
	}

	/**
	 * 
	 * @param pickupHeader
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
    private List<OrderManagementLine> updateOrderManagementLineForReversal(PickupHeader pickupHeader, String loginUserID) throws IllegalAccessException, InvocationTargetException {
        //HAREESH - 27-08-2022 pickup number null update was reflected due to bean util null property ignore , so wrote the method here and manually set pickupnumber to null

        List<OrderManagementLine> dbOrderManagementLine = orderManagementLineService.getListOrderManagementLine(pickupHeader.getWarehouseId(), pickupHeader.getPreOutboundNo(),
                pickupHeader.getRefDocNumber(), pickupHeader.getPartnerCode(), pickupHeader.getLineNumber(),
                pickupHeader.getItemCode());
        AuthToken idmasterAuthToken = authTokenService.getIDMasterServiceAuthToken();
		StatusId idStatus = idmasterService.getStatus(47L, pickupHeader.getWarehouseId(), idmasterAuthToken.getAccess_token());
        if (dbOrderManagementLine != null && !dbOrderManagementLine.isEmpty()) {
            List<OrderManagementLine> orderManagementLineList = new ArrayList<>();
            dbOrderManagementLine.forEach(data->{
                data.setPickupUpdatedBy(loginUserID);
                data.setPickupNumber(null);
				data.setAllocatedQty(0D); 						// HAREESH 25/11/2022
                data.setStatusId(47L);
                data.setReferenceField7(idStatus.getStatus());	// ref_field_7
                data.setPickupUpdatedOn(new Date());
                orderManagementLineList.add(data);
            });
            
            List<OrderManagementLine> orderManagementLine = orderManagementLineRepository.saveAll(orderManagementLineList);
            log.info("OrderManagementLine updated : " + orderManagementLine);
			if(orderManagementLine.size() > 1) {
				log.info("Delete OrderManagementLines if more that one line : ");
				int i = 0;
				List<OrderManagementLine> deleteOrderManagementLineList = new ArrayList<>();
				for(OrderManagementLine line : orderManagementLine) {
					if(i != 0){
						line.setDeletionIndicator(1L);
						deleteOrderManagementLineList.add(line);
					}
					i++;
				}
				log.info("OrderManagementLines for delete : " + deleteOrderManagementLineList);
				orderManagementLineRepository.saveAll(deleteOrderManagementLineList);
				log.info("OrderManagementLines deleted : ");
			}
            return orderManagementLine;
        } else {
            return null;
        }
    }

	/**
	 * 
	 * @param pickupLine
	 * @param loginUserID
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private PickupHeader updatePickupHeader(PickupLine pickupLine, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		UpdatePickupHeader updatePickupHeader = new UpdatePickupHeader();
		updatePickupHeader.setStatusId(53L);
		PickupHeader pickupHeader = pickupHeaderService.updatePickupHeader(pickupLine.getWarehouseId(), 
				pickupLine.getPreOutboundNo(), pickupLine.getRefDocNumber(), pickupLine.getPartnerCode(), 
				pickupLine.getPickupNumber(), pickupLine.getLineNumber(), pickupLine.getItemCode(), 
				loginUserID, updatePickupHeader);
		log.info("pickupHeader updated : " + pickupHeader);
		return pickupHeader;
	}

	/**
	 * 
	 * @param pickupLine
	 * @param  
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private Inventory updateInventory1(PickupLine pickupLine,Long statusId) throws IllegalAccessException, InvocationTargetException {
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		Long BIN_CLASS_ID = 5L;
		if(statusId == 50L){
			BIN_CLASS_ID = 4L;
		}
		StorageBin storageBin = mastersService.getStorageBin(pickupLine.getWarehouseId(), BIN_CLASS_ID, authTokenForMastersService.getAccess_token());
		try {
			Inventory inventory = inventoryService.getInventory(pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(), 
					pickupLine.getItemCode(), storageBin.getStorageBin());
			if(inventory != null) {
				Double INV_QTY = inventory.getInventoryQuantity() - pickupLine.getPickConfirmQty();
				inventory.setInventoryQuantity(INV_QTY);

				// INV_QTY > 0 then, update Inventory Table
				inventory = inventoryRepository.save(inventory);
				log.info("Inventory updated : " + inventory);
			}
			return inventory;
		} catch (Exception e) {
			String objectData = pickupLine.getWarehouseId() + "|" + pickupLine.getPickedPackCode() + "|" + pickupLine.getItemCode() + "|" + storageBin.getStorageBin();
			transactionErrorService.createTransactionError("INVENTORY", "Reversal | Inventory Update | updateInventory1() ", e.getMessage(), e.getLocalizedMessage(), objectData, "Reversal");	
			log.error("Inventory Update Error:" + e.toString());
			e.printStackTrace();
		}
		return null;
	}

	/*--------------------------------------OUTBOUND---------------------------------------------*/
	
	/**
	 * InterwarehouseShipment API
	 * @param confirmedOutboundHeader
	 * @param confirmedOutboundLines
	 * @return
	 */
	private AXApiResponse postInterwarehouseShipment(OutboundHeader confirmedOutboundHeader,
			List<OutboundLine> confirmedOutboundLines) {
		InterWarehouseShipmentHeader toHeader = new InterWarehouseShipmentHeader();
		toHeader.setTransferOrderNumber(confirmedOutboundHeader.getRefDocNumber());	// REF_DOC_NO
		
		List<InterWarehouseShipmentLine> toLines = new ArrayList<>();
		for (OutboundLine confirmedOutboundLine : confirmedOutboundLines) {
			log.info("DLV_QTY : " + confirmedOutboundLine.getDeliveryQty());
			log.info("ReferenceField2 : " + confirmedOutboundLine.getReferenceField2());
			
			if (confirmedOutboundLine.getDeliveryQty() != null && confirmedOutboundLine.getDeliveryQty() > 0) {
				confirmedOutboundLine.setDeliveryConfirmedOn(new Date());
				InterWarehouseShipmentLine iwhShipmentLine = new InterWarehouseShipmentLine();
				
				// SKU	<-	ITM_CODE
				iwhShipmentLine.setSku(confirmedOutboundLine.getItemCode());
				
				// SKU description	<- ITEM_TEXT
				iwhShipmentLine.setSkuDescription(confirmedOutboundLine.getDescription());
				
				// Line reference	<-	IB_LINE_NO
				iwhShipmentLine.setLineReference(confirmedOutboundLine.getLineNumber());
				
				// Ordered Qty	<- ORD_QTY
				iwhShipmentLine.setOrderedQty(confirmedOutboundLine.getOrderQty());
				
				// Shipped Qty	<-	DLV_QTY
				iwhShipmentLine.setShippedQty(confirmedOutboundLine.getDeliveryQty());
				
				// Delivery Date <-	DLV_CNF_ON
				String date = DateUtils.date2String_MMDDYYYY(confirmedOutboundLine.getDeliveryConfirmedOn());
				iwhShipmentLine.setDeliveryDate(date);
				
				// FromWhsID	<-	WH_ID
				iwhShipmentLine.setFromWhsID(confirmedOutboundLine.getWarehouseId());
				
				// ToWhsID	<-	PARTNER_CODE
				iwhShipmentLine.setToWhsID(confirmedOutboundLine.getPartnerCode());
				
				toLines.add(iwhShipmentLine);
			}
		}
		
		InterWarehouseShipment interWarehouseShipment = new InterWarehouseShipment();
		interWarehouseShipment.setToHeader(toHeader);
		interWarehouseShipment.setToLines(toLines);
		log.info("InterWarehouseShipment : " + interWarehouseShipment);
		
		/*
		 * Posting to AX_API
		 */
		AXApiResponse apiResponse = null;
		try {
			AXAuthToken authToken = authTokenService.generateAXOAuthToken();
			apiResponse = warehouseService.postInterWarehouseShipmentConfirmation(interWarehouseShipment, authToken.getAccess_token());
			log.info("apiResponse : " + apiResponse);
		} catch (Exception e) {
			e.printStackTrace();
			apiResponse = new AXApiResponse();
			apiResponse.setStatusCode("400");
			apiResponse.setMessage(e.toString());
			log.info("ApiResponse got Error: " + apiResponse);
		}
		
		// Capture the AXResponse in Table
		IntegrationApiResponse response = new IntegrationApiResponse();
		response.setOrderNumber(toHeader.getTransferOrderNumber());
		response.setOrderType("OUTBOUND");
		response.setOrderTypeId(confirmedOutboundHeader.getOutboundOrderTypeId());
		response.setResponseCode(apiResponse.getStatusCode());
		response.setResponseText(apiResponse.getMessage());
		response.setApiUrl(propertiesConfig.getAxapiServiceIWHouseShipmentUrl());
		response.setTransDate(new Date());
		
		integrationApiResponseRepository.save(response);
		return apiResponse;
	}
	
	/**
	 * Do not send ITM_CODE values where DLV_QTY = 0 or REF_FIELD_2 is Not Null to MS Dynamics system
	 * @param confirmedOutboundHeader
	 * @param confirmedOutboundLines
	 * @return
	 */
	private AXApiResponse postShipment(OutboundHeader confirmedOutboundHeader, List<OutboundLine> confirmedOutboundLines) {
		ShipmentHeader toHeader = new ShipmentHeader();
		toHeader.setTransferOrderNumber(confirmedOutboundHeader.getRefDocNumber());	// REF_DOC_NO
		
		log.info("confirmedOutboundLines--------------> :  " + confirmedOutboundLines);
		List<ShipmentLine> toLines = new ArrayList<>();
		for (OutboundLine confirmedOutboundLine : confirmedOutboundLines) {
			
			log.info("DLV_QTY : " + confirmedOutboundLine.getDeliveryQty());
			log.info("ReferenceField2 : " + confirmedOutboundLine.getReferenceField2());
			
			if (confirmedOutboundLine.getDeliveryQty() != null && confirmedOutboundLine.getDeliveryQty() > 0) {
				confirmedOutboundLine.setDeliveryConfirmedOn(new Date());
				ShipmentLine shipmentLine = new ShipmentLine();
				
				// SKU	<-	ITM_CODE
				shipmentLine.setSku(confirmedOutboundLine.getItemCode());
				
				// SKU description	<- ITEM_TEXT
				shipmentLine.setSkuDescription(confirmedOutboundLine.getDescription());
				
				// Line reference	<-	IB_LINE_NO
				shipmentLine.setLineReference(confirmedOutboundLine.getLineNumber());
				
				// Ordered Qty	<- ORD_QTY
				shipmentLine.setOrderedQty(confirmedOutboundLine.getOrderQty());
				
				// Shipped Qty	<-	DLV_QTY
				shipmentLine.setShippedQty(confirmedOutboundLine.getDeliveryQty());
				
				// Delivery Date <-	DLV_CNF_ON
				String date = DateUtils.date2String_MMDDYYYY(confirmedOutboundLine.getDeliveryConfirmedOn());
				shipmentLine.setDeliveryDate(date);
				
				// Store ID <-	PARTNER_CODE
				shipmentLine.setStoreId(confirmedOutboundLine.getPartnerCode());
				
				// Warehouse ID	<-	WH_ID
				shipmentLine.setWareHouseID(confirmedOutboundLine.getWarehouseId());
				
				toLines.add(shipmentLine);
			}
		}
		
		Shipment shipment = new Shipment();
		shipment.setToHeader(toHeader);
		shipment.setToLines(toLines);
		log.info("Sending to AX : " + shipment);
		
		/*
		 * Posting to AX_API
		 */
		AXApiResponse apiResponse = null;
		try {
			AXAuthToken authToken = authTokenService.generateAXOAuthToken();
			apiResponse = warehouseService.postShipmentConfirmation(shipment, authToken.getAccess_token());
			log.info("apiResponse : " + apiResponse);
		} catch (Exception e) {
			e.printStackTrace();
			apiResponse = new AXApiResponse();
			apiResponse.setStatusCode("400");
			apiResponse.setMessage(e.toString());
			log.info("ApiResponse got Error: " + apiResponse);
		}
		
		// Capture the AXResponse in Table
		IntegrationApiResponse response = new IntegrationApiResponse();
		response.setOrderNumber(toHeader.getTransferOrderNumber());
		response.setOrderType("OUTBOUND");
		response.setOrderTypeId(confirmedOutboundHeader.getOutboundOrderTypeId());
		response.setResponseCode(apiResponse.getStatusCode());
		response.setResponseText(apiResponse.getMessage());
		response.setApiUrl(propertiesConfig.getAxapiServiceShipmentUrl());
		response.setTransDate(new Date());
		integrationApiResponseRepository.save(response);
		return apiResponse;
	}
	
	/**
	 * ReturnPO
	 * @param confirmedOutboundHeader
	 * @param confirmedOutboundLines
	 * @return
	 */
	private AXApiResponse postReturnPO(OutboundHeader confirmedOutboundHeader,
			List<OutboundLine> confirmedOutboundLines) {
		ReturnPOHeader poHeader = new ReturnPOHeader();
		poHeader.setPoNumber(confirmedOutboundHeader.getRefDocNumber());	// REF_DOC_NO
		poHeader.setSupplierInvoice(confirmedOutboundHeader.getRefDocNumber());	// REF_DOC_NO
		
		List<ReturnPOLine> poLines = new ArrayList<>();
		for (OutboundLine confirmedOutboundLine : confirmedOutboundLines) {
			
			log.info("DLV_QTY : " + confirmedOutboundLine.getDeliveryQty());
			log.info("ReferenceField2 : " + confirmedOutboundLine.getReferenceField2());
			
			if (confirmedOutboundLine.getDeliveryQty() != null && confirmedOutboundLine.getDeliveryQty() > 0) {
				ReturnPOLine returnPOLine = new ReturnPOLine();
				
				// SKU	<-	ITM_CODE
				returnPOLine.setSku(confirmedOutboundLine.getItemCode());
				
				// SKU description	<- ITEM_TEXT
				returnPOLine.setSkuDescription(confirmedOutboundLine.getDescription());
				
				// Line reference	<-	IB_LINE_NO
				returnPOLine.setLineReference(confirmedOutboundLine.getLineNumber());
				
				// Return Qty <-	ORD_QTY
				returnPOLine.setReturnQty(confirmedOutboundLine.getOrderQty());
				
				// Shipped Qty	<-	DLV_QTY
				returnPOLine.setShippedQty(confirmedOutboundLine.getDeliveryQty());
				
				// Warehouse ID	<-	WH_ID
				returnPOLine.setWarehouseID(confirmedOutboundLine.getWarehouseId());
				
				poLines.add(returnPOLine);
			}
		}
		
		ReturnPO returnPO = new ReturnPO();
		returnPO.setPoHeader(poHeader);
		returnPO.setPoLines(poLines);
		log.info("ReturnPO : " + returnPO);
		
		/*
		 * Posting to AX_API
		 */
		AXApiResponse apiResponse = null;
		try {
			AXAuthToken authToken = authTokenService.generateAXOAuthToken();
			apiResponse = warehouseService.postReturnPOConfirmation(returnPO, authToken.getAccess_token());
			log.info("apiResponse : " + apiResponse);
		} catch (Exception e) {
			e.printStackTrace();
			apiResponse = new AXApiResponse();
			apiResponse.setStatusCode("400");
			apiResponse.setMessage(e.toString());
			log.info("ApiResponse got Error: " + apiResponse);
		}
		
		IntegrationApiResponse response = new IntegrationApiResponse();
		response.setOrderNumber(poHeader.getPoNumber());
		response.setOrderType("OUTBOUND");
		response.setOrderTypeId(confirmedOutboundHeader.getOutboundOrderTypeId());
		response.setResponseCode(apiResponse.getStatusCode());
		response.setResponseText(apiResponse.getMessage());
		response.setTransDate(new Date());
		response.setApiUrl(propertiesConfig.getAxapiServiceReturnPOUrl());
		integrationApiResponseRepository.save(response);
		
		return apiResponse;
	}
	
	/**
	 * 
	 * @param confirmedOutboundHeader
	 * @param confirmedOutboundLines
	 * @return
	 */
	private AXApiResponse postSalesOrder(OutboundHeader confirmedOutboundHeader,
			List<OutboundLine> confirmedOutboundLines) {
		SalesOrderHeader soHeader = new SalesOrderHeader();
		soHeader.setSalesOrderNumber(confirmedOutboundHeader.getRefDocNumber());	// REF_DOC_NO
		
		List<SalesOrderLine> soLines = new ArrayList<>();
		for (OutboundLine confirmedOutboundLine : confirmedOutboundLines) {
			
			log.info("DLV_QTY : " + confirmedOutboundLine.getDeliveryQty());
			log.info("ReferenceField2 : " + confirmedOutboundLine.getReferenceField2());
			
			if (confirmedOutboundLine.getDeliveryQty() != null && confirmedOutboundLine.getDeliveryQty() > 0) {
				confirmedOutboundLine.setDeliveryConfirmedOn(new Date());
				SalesOrderLine salesOrderLine = new SalesOrderLine();
				
				// SKU	<-	ITM_CODE
				salesOrderLine.setSku(confirmedOutboundLine.getItemCode());
				
				// SKU description	<- ITEM_TEXT
				salesOrderLine.setSkuDescription(confirmedOutboundLine.getDescription());
				
				// Line reference	<-	IB_LINE_NO
				salesOrderLine.setLineReference(confirmedOutboundLine.getLineNumber());
				
				// Ordered Qty	<- ORD_QTY
				salesOrderLine.setOrderedQty(confirmedOutboundLine.getOrderQty());
				
				// Shipped Qty	<-	DLV_QTY
				salesOrderLine.setShippedQty(confirmedOutboundLine.getDeliveryQty());
				
				// Delivery Date <-	DLV_CNF_ON
				String date = DateUtils.date2String_MMDDYYYY(confirmedOutboundLine.getDeliveryConfirmedOn());
				salesOrderLine.setDeliveryDate(date);
				
				// Store ID <-	PARTNER_CODE
				salesOrderLine.setStoreId(confirmedOutboundLine.getPartnerCode());
				
				// Warehouse ID	<-	WH_ID
				salesOrderLine.setWareHouseID(confirmedOutboundLine.getWarehouseId());
				
				soLines.add(salesOrderLine);
			}
		}
		
		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setSoHeader(soHeader);
		salesOrder.setSoLines(soLines);
		log.info("SalesOrder : " + salesOrder);
		
		/*
		 * Posting to AX_API
		 */
		AXApiResponse apiResponse = null;
		try {
			AXAuthToken authToken = authTokenService.generateAXOAuthToken();
			apiResponse = warehouseService.postSaleOrderConfirmation(salesOrder, authToken.getAccess_token());
			log.info("apiResponse : " + apiResponse);
		} catch (Exception e) {
			e.printStackTrace();
			apiResponse = new AXApiResponse();
			apiResponse.setStatusCode("400");
			apiResponse.setMessage(e.toString());
			log.info("ApiResponse got Error: " + apiResponse);
		}
		
		IntegrationApiResponse response = new IntegrationApiResponse();
		response.setOrderNumber(soHeader.getSalesOrderNumber());
		response.setOrderType("OUTBOUND");
		response.setOrderTypeId(confirmedOutboundHeader.getOutboundOrderTypeId());
		response.setResponseCode(apiResponse.getStatusCode());
		response.setResponseText(apiResponse.getMessage());
		response.setApiUrl(propertiesConfig.getAxapiServiceSalesOrderUrl());
		response.setTransDate(new Date());
		integrationApiResponseRepository.save(response);
		
		return apiResponse;
	}

	/**
	 * doRequiredDeliveryDateUpdate
	 * @param warehouseId
	 * @param refDocNumber
	 * @param requiredDeliveryDate
	 */
	public void doRequiredDeliveryDateUpdate(UpdateRequestDeliveryDate updateRequestDeliveryDate) {
		String warehouseId = updateRequestDeliveryDate.getWarehouseId();
		String refDocNumber = updateRequestDeliveryDate.getRefDocNumber();
		Date requiredDeliveryDate = updateRequestDeliveryDate.getRequiredDeliveryDate();	
		
		log.info("warehouseId : " + warehouseId + ",refDocNumber :" + refDocNumber + ", requiredDeliveryDate : " + requiredDeliveryDate);		
		preOutboundHeaderRepository.updatePreOutboundHeaderRequiredDeliveryDate(warehouseId, refDocNumber, requiredDeliveryDate);
		preOutboundLineRepository.updatePreOutboundLineRequiredDeliveryDate(warehouseId, refDocNumber, requiredDeliveryDate);
		orderManagementLineRepository.updateOrderManagementLineRequiredDeliveryDate(warehouseId, refDocNumber, requiredDeliveryDate);
		outboundHeaderRepository.updateOutboundHeaderRequiredDeliveryDate(warehouseId, refDocNumber, requiredDeliveryDate);
//		outboundLineRepository.updateOutboundHeaderRequiredDeliveryDate(warehouseId, refDocNumber, requiredDeliveryDate);
		log.info("All tables were update");
	}
}