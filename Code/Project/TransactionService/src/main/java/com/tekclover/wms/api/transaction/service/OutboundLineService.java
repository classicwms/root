package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.transaction.model.dto.IImbasicData1;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.outbound.pickup.*;
import com.tekclover.wms.api.transaction.repository.ImBasicData1Repository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AXAuthToken;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.transaction.model.impl.OrderStatusReportImpl;
import com.tekclover.wms.api.transaction.model.impl.OutBoundLineImpl;
import com.tekclover.wms.api.transaction.model.impl.ShipmentDispatchSummaryReportImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.outbound.AddOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.SearchOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.SearchOutboundLineReport;
import com.tekclover.wms.api.transaction.model.outbound.UpdateOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.UpdateOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.OrderManagementHeader;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.OrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.UpdateOrderManagementHeader;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.UpdateOrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.AddOutboundReversal;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.OutboundReversal;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.PreOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.PreOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.UpdatePreOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.UpdatePreOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityLine;
import com.tekclover.wms.api.transaction.model.outbound.quality.SearchQualityLine;
import com.tekclover.wms.api.transaction.model.outbound.quality.UpdateQualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.UpdateQualityLine;
import com.tekclover.wms.api.transaction.model.report.SearchOrderStatusReport;
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
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.OutboundLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.OutboundLineReportSpecification;
import com.tekclover.wms.api.transaction.repository.specification.OutboundLineSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OutboundLineService extends BaseService {
	
	@Autowired
	private OutboundLineRepository outboundLineRepository;
	
	@Autowired
	private OutboundHeaderService outboundHeaderService;
	
	@Autowired
	private InventoryService inventoryService;
	
	@Autowired
	private InventoryMovementService inventoryMovementService;
	
	@Autowired
	private InventoryRepository inventoryRepository;
	
	@Autowired
	private QualityHeaderService qualityHeaderService;
	
	@Autowired
	private QualityLineService qualityLineService;
	
	@Autowired
	private PickupLineService pickupLineService;
	
	@Autowired
	private PickupHeaderService pickupHeaderService;
	
	@Autowired
	private OrderManagementHeaderService orderManagementHeaderService;
	
	@Autowired
	private OrderManagementLineService orderManagementLineService;
	
	@Autowired
	private OutboundReversalService outboundReversalService;
	
	@Autowired
	private PreOutboundHeaderService preOutboundHeaderService;
	
	@Autowired
	private PreOutboundLineService preOutboundLineService;
	
	@Autowired
	private MastersService mastersService;
	
	@Autowired
	private AuthTokenService authTokenService;
	
	@Autowired
	private WarehouseService warehouseService;

	@Autowired
	private ImBasicData1Repository imBasicData1Repository;
	
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
		
		if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLine.getFromDeliveryDate(), 
					searchOutboundLine.getToDeliveryDate());
			searchOutboundLine.setFromDeliveryDate(dates[0]);
			searchOutboundLine.setToDeliveryDate(dates[1]);
		}
		
		OutboundLineSpecification spec = new OutboundLineSpecification(searchOutboundLine);
		List<OutboundLine> outboundLineSearchResults = outboundLineRepository.findAll(spec);
//		log.info("results: " + outboundLineSearchResults);
		
		/*
		 * Pass WH-ID/REF_DOC_NO/PRE_OB_NO/OB_LINE_NO/ITM_CODE
		 * PickConfirmQty & QCQty - from QualityLine table
		 */
		if (outboundLineSearchResults != null) {
			for (OutboundLine outboundLineSearchResult : outboundLineSearchResults) {
				SearchQualityLine searchQualityLine = new SearchQualityLine();
				searchQualityLine.setWarehouseId(Arrays.asList(outboundLineSearchResult.getWarehouseId()));
				searchQualityLine.setRefDocNumber(Arrays.asList(outboundLineSearchResult.getRefDocNumber()));
				searchQualityLine.setPreOutboundNo(Arrays.asList(outboundLineSearchResult.getPreOutboundNo()));
				searchQualityLine.setLineNumber(Arrays.asList(outboundLineSearchResult.getLineNumber()));
				searchQualityLine.setItemCode(Arrays.asList(outboundLineSearchResult.getItemCode()));
//				searchQualityLine.setStatusId(Arrays.asList(outboundLineSearchResult.getStatusId()));

				List<QualityLine> qualityLine = qualityLineService.findQualityLine(searchQualityLine);

				// ----Select sum of PickConfirmQty / itemcode wise. -> Ref_field_9
//				double pickConfirmQty = qualityLine.stream().mapToDouble(QualityLine::getPickConfirmQty).sum();
//				outboundLineSearchResult.setReferenceField9(String.valueOf(pickConfirmQty));

//				 ---- Select sum of QCQty / itemcode wise. -> Ref_field_10
				double qcQty = qualityLine.stream().mapToDouble(QualityLine::getQualityQty).sum();
				outboundLineSearchResult.setReferenceField10(String.valueOf(qcQty));

				SearchPickupLine searchPickupLine = new SearchPickupLine();
				searchPickupLine.setWarehouseId(Arrays.asList(outboundLineSearchResult.getWarehouseId()));
				searchPickupLine.setRefDocNumber(Arrays.asList(outboundLineSearchResult.getRefDocNumber()));
				searchPickupLine.setPreOutboundNo(Arrays.asList(outboundLineSearchResult.getPreOutboundNo()));
				searchPickupLine.setLineNumber(Arrays.asList(outboundLineSearchResult.getLineNumber()));
				searchPickupLine.setItemCode(Arrays.asList(outboundLineSearchResult.getItemCode()));

				List<PickupLine> pickupLines = pickupLineService.findPickupLine(searchPickupLine);
				// ----Select sum of PickConfirmQty / itemcode wise. -> Ref_field_9
				double pickConfirmQty = pickupLines.stream().mapToDouble(PickupLine::getPickConfirmQty).sum();
				outboundLineSearchResult.setReferenceField9(String.valueOf(pickConfirmQty));
			}
		}
		return outboundLineSearchResults;
	}

	public List<OutboundLine> findOutboundLineForStockMovement(SearchOutboundLine searchOutboundLine)
			throws ParseException, java.text.ParseException {

		if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLine.getFromDeliveryDate(),
					searchOutboundLine.getToDeliveryDate());
			searchOutboundLine.setFromDeliveryDate(dates[0]);
			searchOutboundLine.setToDeliveryDate(dates[1]);
		}
		searchOutboundLine.setStatusId(Arrays.asList(59L));
		OutboundLineSpecification spec = new OutboundLineSpecification(searchOutboundLine);

		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("deliveryConfirmedOn").ascending());

		Page<OutboundLine> outboundLineSearchResults = outboundLineRepository.findAll(spec,pageable);

//		log.info("results: " + outboundLineSearchResults);

		/*
		 * Pass WH-ID/REF_DOC_NO/PRE_OB_NO/OB_LINE_NO/ITM_CODE
		 * PickConfirmQty & QCQty - from QualityLine table
		 */
		if (outboundLineSearchResults != null) {
			for (OutboundLine outboundLineSearchResult : outboundLineSearchResults.getContent()) {
				List<IImbasicData1> itemList = imBasicData1Repository.findByItemCode(outboundLineSearchResult.getItemCode());
					if(!itemList.isEmpty()){
						outboundLineSearchResult.setItemText(itemList.get(0).getDescription());
						outboundLineSearchResult.setMfrPartNumber(itemList.get(0).getManufacturePart());
					}
			}
		}
		return outboundLineSearchResults.getContent();
	}
	
	/**
	 * 
	 * @param searchOutboundLine
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
//	public List<OutboundLine> findOutboundLineShipmentReport(SearchOutboundLine searchOutboundLine)
//			throws ParseException, java.text.ParseException {
//
//		if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
//			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLine.getFromDeliveryDate(),
//					searchOutboundLine.getToDeliveryDate());
//			searchOutboundLine.setFromDeliveryDate(dates[0]);
//			searchOutboundLine.setToDeliveryDate(dates[1]);
//		}
//
//		OutboundLineSpecification spec = new OutboundLineSpecification(searchOutboundLine);
//		List<OutboundLine> outboundLineSearchResults = outboundLineRepository.findAll(spec);
////		log.info("search results: " + outboundLineSearchResults);
//		return outboundLineSearchResults;
//	}

	/**
	 *
	 * @param searchOutboundLine
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	public List<ShipmentDispatchSummaryReportImpl> findOutboundLineShipmentReport(SearchOutboundLine searchOutboundLine)
			throws ParseException, java.text.ParseException {

		if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLine.getFromDeliveryDate(),
					searchOutboundLine.getToDeliveryDate());
			searchOutboundLine.setFromDeliveryDate(dates[0]);
			searchOutboundLine.setToDeliveryDate(dates[1]);
		}
		
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
	
//	/**
//	 * findOutboundLineOrderStatusReport
//	 * @param searchOrderStatusReport
//	 * @return
//	 * @throws ParseException
//	 * @throws java.text.ParseException
//	 */
//	public List<OutboundLine> findOutboundLineOrderStatusReport (SearchOrderStatusReport searchOrderStatusReport)
//			throws ParseException, java.text.ParseException {
//		if (searchOrderStatusReport.getFromDeliveryDate() != null && searchOrderStatusReport.getToDeliveryDate() != null) {
//			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOrderStatusReport.getFromDeliveryDate(),
//					searchOrderStatusReport.getToDeliveryDate());
//			searchOrderStatusReport.setFromDeliveryDate(dates[0]);
//			searchOrderStatusReport.setToDeliveryDate(dates[1]);
//		}
//
//		OutboundLineOrderStatusReportSpecification spec = new OutboundLineOrderStatusReportSpecification(searchOrderStatusReport);
//		List<OutboundLine> results = outboundLineRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}

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
		log.info("results: " + results);
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
	 * updateOutboundLine
	 * @param loginUserID2 
	 * @param long1 
	 * @param loginUserId 
	 * @param languageId, companyCodeId, plantId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode
	 * @param updateOutboundLine
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<OutboundLine> deliveryConfirmation (String warehouseId, String preOutboundNo, String refDocNumber, 
			String partnerCode, String loginUserID) throws IllegalAccessException, InvocationTargetException {
		List<OutboundLine> outboundLineList = getOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode);
		log.info("outboundLine outboundLineList : " + outboundLineList);
		
		long matchedCount = outboundLineList.stream().filter(a->a.getStatusId() == 57L || a.getStatusId() == 47L || 
				a.getStatusId() == 51L || a.getStatusId() == 41L).count();
		boolean isConditionMet = (matchedCount == outboundLineList.size());
		log.info("isConditionMet : " + isConditionMet);
		
		AXApiResponse axapiResponse = null;
		if (!isConditionMet) {
			throw new BadRequestException("Order is not completely Processed.");
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
		}
		
		/*
		 * Pass the selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/ITEM_CODE/OB_LINE_NO values in OUTBOUNDLINE table and 
		 * Validate STATUS_ID = 55 or 47 or 51, if yes
		 */
		List<OutboundLine> responseOutboundLineList = new ArrayList<>();
		for (OutboundLine outboundLine : outboundLineList) {
			if (outboundLine.getStatusId() == 57L || outboundLine.getStatusId() == 47L || outboundLine.getStatusId() == 51L
					|| outboundLine.getStatusId() == 41L) {
				/*---------------------AXAPI-integration----------------------------------------------------------*/
				// Checking the AX-API response
				if (axapiResponse.getStatusCode() != null && axapiResponse.getStatusCode().equalsIgnoreCase("200")) {
					if (outboundLine.getStatusId() == 57L) {
						try {
							// Pass the above values in OUTBOUNDHEADER and OUTBOUNDLINE tables and update STATUS_ID as "59"
							outboundLine.setStatusId(59L);
							outboundLine.setUpdatedBy(loginUserID);
							outboundLine.setUpdatedOn(new Date());
							outboundLine = outboundLineRepository.save(outboundLine);
							log.info("outboundLine updated : " + outboundLine);
							responseOutboundLineList.add(outboundLine);
							
							// OUTBOUNDHEADER update
							UpdateOutboundHeader updateOutboundHeader = new UpdateOutboundHeader();
							updateOutboundHeader.setStatusId(59L);
							updateOutboundHeader.setDeliveryConfirmedOn(new Date());
							updateOutboundHeader.setUpdatedOn(new Date());
							updateOutboundHeader.setUpdatedBy(loginUserID);
							OutboundHeader updatedOutboundHeader = 
									outboundHeaderService.updateOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, updateOutboundHeader, loginUserID);
							log.info("updatedOutboundHeader updated : " + updatedOutboundHeader);
							
							/*
							 * Pass the selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE values and update STATUS_ID as 59 
							 * for the below tables
							 * QUALITYLINE, QUALITYHEADER, PICKUPLINE, PICKUPHEADER, ORDERMANAGEMENTLINE, ORDERMANAGEMENTHEADER,
							 * PREOUTBOUNDHEADER,PREOUTBOUNDLINE
							 */
							// QUALITYLINE
							UpdateQualityLine updateQualityLine = new UpdateQualityLine();
							updateQualityLine.setStatusId(59L);
							List<QualityLine> updatedQualityLine = qualityLineService.updateQualityLine(warehouseId, preOutboundNo, refDocNumber, partnerCode,
									outboundLine.getLineNumber(), outboundLine.getItemCode(), loginUserID, updateQualityLine);
							log.info("updatedQualityLine updated : " + updatedQualityLine);
							
							// QUALITYHEADER
							if (!updatedQualityLine.isEmpty()) {
								UpdateQualityHeader updateQualityHeader = new UpdateQualityHeader();
								updateQualityHeader.setStatusId(59L);
								QualityLine qualityLine = updatedQualityLine.get(0);
								QualityHeader updatedQualityHeader = qualityHeaderService.updateQualityHeader(warehouseId, preOutboundNo, refDocNumber,
										qualityLine.getQualityInspectionNo(), qualityLine.getActualHeNo(), loginUserID, updateQualityHeader);
								log.info("updatedQualityHeader updated : " + updatedQualityHeader);
							}
							
							// PICKUPLINE
							UpdatePickupLine updatePickupLine = new UpdatePickupLine();
							updatePickupLine.setStatusId(59L);
							PickupLine updatedPickupLine = pickupLineService.updatePickupLine(warehouseId, preOutboundNo, refDocNumber, 
									partnerCode, outboundLine.getLineNumber(), outboundLine.getItemCode(), loginUserID, updatePickupLine);
							log.info("updatedPickupLine updated : " + updatedPickupLine);
							
							// PICKUPHEADER
							UpdatePickupHeader updatePickupHeader = new UpdatePickupHeader();
							updatePickupHeader.setStatusId(59L);
							PickupHeader updatedPickupHeader = pickupHeaderService.updatePickupHeader(warehouseId, preOutboundNo, refDocNumber, 
									partnerCode, updatedPickupLine.getPickupNumber(), updatedPickupLine.getLineNumber(), updatedPickupLine.getItemCode(), 
									loginUserID, updatePickupHeader);
							log.info("updatedPickupLine updated : " + updatedPickupHeader);
							
							// ORDERMANAGEMENTLINE
							UpdateOrderManagementLine updateOrderManagementLine = new UpdateOrderManagementLine();
							updateOrderManagementLine.setStatusId(59L);
							OrderManagementLine updatedOrderManagementLine = orderManagementLineService.updateOrderManagementLine(warehouseId, 
									preOutboundNo, refDocNumber, partnerCode, updatedPickupHeader.getLineNumber(), updatedPickupHeader.getItemCode(), 
									loginUserID, updateOrderManagementLine);
							log.info("updatedOrderManagementLine updated : " + updatedOrderManagementLine);
							
							// ORDERMANAGEMENTHEADER
							UpdateOrderManagementHeader updateOrderManagementHeader = new UpdateOrderManagementHeader();
							updateOrderManagementHeader.setStatusId(59L);
							OrderManagementHeader updatedOrderManagementHeader = orderManagementHeaderService.updateOrderManagementHeader(warehouseId, 
									preOutboundNo, refDocNumber, partnerCode, loginUserID, updateOrderManagementHeader);
							log.info("updatedOrderManagementHeader updated : " + updatedOrderManagementHeader);
							
							// PREOUTBOUNDLINE
							UpdatePreOutboundLine updatePreOutboundLine = new UpdatePreOutboundLine();
							updatePreOutboundLine.setStatusId(59L);
							PreOutboundLine updatedPreOutboundLine = preOutboundLineService.updatePreOutboundLine(warehouseId, refDocNumber, 
									preOutboundNo, partnerCode, updatedPickupLine.getLineNumber(), updatedPickupLine.getItemCode(), loginUserID, updatePreOutboundLine);
							log.info("updatedPreOutboundLine updated : " + updatedPreOutboundLine);
							
							// PREOUTBOUNDHEADER
							UpdatePreOutboundHeader updatePreOutboundHeader = new UpdatePreOutboundHeader();
							updatePreOutboundHeader.setStatusId(59L);
							PreOutboundHeader updatedPreOutboundHeader = preOutboundHeaderService.updatePreOutboundHeader(warehouseId, refDocNumber, 
									preOutboundNo, partnerCode, loginUserID, updatePreOutboundHeader);
							log.info("updatedPreOutboundHeader updated : " + updatedPreOutboundHeader);
							
							/*-----------------Inventory Updates---------------------------*/
							// String warehouseId, String itemCode, Long binClassId
							Long BIN_CL_ID = 5L;
							for(QualityLine qualityLine : updatedQualityLine){
								List<Inventory> inventoryList = inventoryService.getInventoryForDeliveryConfirmtion (outboundLine.getWarehouseId(),
										outboundLine.getItemCode(), qualityLine.getPickPackBarCode(), BIN_CL_ID); //pack_bar_code
								for(Inventory inventory : inventoryList) {
									Double INV_QTY = inventory.getInventoryQuantity() - outboundLine.getDeliveryQty();
									log.info("INV_QTY : " + INV_QTY);

									if (INV_QTY < 0) {
										INV_QTY = 0D;
									}

									// [Prod Fix: 14-07] - Hareesh - Don't need to delete the inventory just update the existing inventory quantity
//								if (INV_QTY == 0) {
////									[Prod Fix: 28-06] - Discussed to comment delete Inventory operation to avoid unwanted delete of Inventory
////									inventoryRepository.delete(inventory);
//									log.info("inventory record is deleted...");
//								}

									if (INV_QTY >= 0) {
										inventory.setInventoryQuantity(INV_QTY);

										// INV_QTY > 0 then, update Inventory Table
										inventory = inventoryRepository.save(inventory);
										log.info("inventory updated : " + inventory);
									}
								}
							};

							/*-------------------Inserting record in InventoryMovement-------------------------------------*/
							// Fetch WH_ID/REF_DOC_NO/PRE_OB_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE from Outboundline table and 
							// pass the same in Qualityline table and fetch the records and update INVENTORYMOVEMENT table
							QualityLine qualityLine = 
									qualityLineService.getQualityLine(warehouseId, preOutboundNo, refDocNumber, 
											partnerCode, outboundLine.getLineNumber(), outboundLine.getItemCode());
				
							Long BIN_CLASS_ID = 5L;
							AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
							StorageBin storageBin = mastersService.getStorageBin(outboundLine.getWarehouseId(), BIN_CLASS_ID, 
									authTokenForMastersService.getAccess_token());
							
							String movementDocumentNo = outboundLine.getRefDocNumber();
							String stBin = storageBin.getStorageBin();
							String movementQtyValue = "N";
							InventoryMovement inventoryMovement = createInventoryMovement(updatedPickupLine, movementDocumentNo, stBin, 
									movementQtyValue, loginUserID, true);
							log.info("InventoryMovement created : " + inventoryMovement);
						} catch (Exception e) {
							log.info("Updating respective tables having Error : " + e.getLocalizedMessage());
						}
					}
				} else {
					String errorFromAXAPI = axapiResponse.getMessage();
					throw new BadRequestException("Error from AX: " + errorFromAXAPI);
				}
			} else {
				throw new BadRequestException("Order is not completely Processed.");
			}
		}
		return responseOutboundLineList;
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
				outboundLine.setDeliveryQty(0D);
				outboundLine.setReversedBy(loginUserID);
				outboundLine.setReversedOn(new Date());
				outboundLine = outboundLineRepository.save(outboundLine);
				log.info("outboundLine updated : " + outboundLine);
				
				/*--------------STEP 2-------------------------------------
				 * Pass WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values fetched 
				 * from OUTBOUNDHEADER and OUTBOUNDLINE table into QCLINE table  and update STATUS_ID = 56								
				 */
				QualityLine qualityLine = qualityLineService.deleteQualityLine(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(), 
						outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(), 
						outboundLine.getItemCode(), loginUserID);
				log.info("QualityLine----------Deleted-------> : " + qualityLine);
				
				QualityHeader qualityHeader = qualityHeaderService.deleteQualityHeader(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(), refDocNumber, 
						qualityLine.getQualityInspectionNo(), qualityLine.getActualHeNo(), loginUserID);
				log.info("QualityHeader----------Deleted-------> : " + qualityHeader);
				
				/*---------------STEP 3------------------------------------
				 * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from QCLINE table and 
				 * pass the keys in PICKUPLINE table  and update STATUS_ID = 53									
				 */
				PickupLine pickupLine = pickupLineService.deletePickupLine(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(), 
						outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
						outboundLine.getItemCode(), loginUserID);
				log.info("PickupLine----------Deleted-------> : ");
				
				/*---------------STEP 3.1-----Inventory update-------------------------------
				 * Pass WH_ID/_ITM_CODE/ST_BIN of BIN_CLASS_ID=5/PACK_BARCODE as PICK_PACK_BARCODE of PICKUPLINE 
				 * in INVENTORY table and update INV_QTY as (INV_QTY - DLV_QTY ) and 
				 * delete the record if INV_QTY = 0 (Update 1)								
				 */
				Inventory inventory = updateInventory1(pickupLine);
				
				/*---------------STEP 3.2-----Inventory update-------------------------------
				 * Pass WH_ID/_ITM_CODE/ST_BIN from PICK_ST_BIN /PACK_BARCODE as PICK_PACK_BARCODE of PICKUPLINE 
				 * in INVENTORY table and update INV_QTY as (INV_QTY + DLV_QTY ) - (Update 2)
				 */
				inventory = inventoryService.getInventory(pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(), 
						pickupLine.getItemCode(), pickupLine.getPickedStorageBin());
				
				Double INV_QTY = inventory.getInventoryQuantity() + pickupLine.getPickConfirmQty();
				inventory.setInventoryQuantity(INV_QTY);
				inventory = inventoryRepository.save(inventory);
				log.info("inventory updated : " + inventory);
				
				/*---------------STEP 4-----PickupHeader update-------------------------------
				 * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from PICKUPLINE table 
				 * and pass the keys in PICKUPHEADER table and Delete PickUpHeader
				 */
				PickupHeader pickupHeader = pickupHeaderService.deletePickupHeader(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(), 
						outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
						pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode(), loginUserID);
				log.info("pickupHeader deleted : " + pickupHeader);
				
				/*---------------STEP 5-----OrderManagement update-------------------------------
				 * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from PICKUPHEADER table 
				 * and pass the keys in ORDERMANAGEMENTLINE table and update STATUS_ID as 47
				 */
				OrderManagementLine orderManagementLine = updateOrderManagementLine (pickupHeader, loginUserID);
				log.info("orderManagementLine updated : " + orderManagementLine);
				
				/*------------------------Record insertion in Outbound Reversal table----------------------------*/
				/////////RECORD-1/////////////////////////////////////////////////////////////////////////////////
				String reversalType = "QUALITY";
				Double reversedQty = qualityLine.getQualityQty();
				OutboundReversal createdOutboundReversal = createOutboundReversal (warehouse, reversalType, refDocNumber, 
						outboundLine.getPartnerCode(), itemCode, qualityLine.getPickPackBarCode(), reversedQty, 
						outboundLine.getStatusId(), loginUserID);
				outboundReversalList.add(createdOutboundReversal);
				
				/////////RECORD-2/////////////////////////////////////////////////////////////////////////////////
				reversalType = "PICKING";
				reversedQty = pickupLine.getPickConfirmQty();
				createdOutboundReversal = createOutboundReversal (warehouse, reversalType, refDocNumber, 
						outboundLine.getPartnerCode(), itemCode, qualityLine.getPickPackBarCode(), reversedQty, 
						outboundLine.getStatusId(), loginUserID);
				outboundReversalList.add(createdOutboundReversal);
				
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
			}
			
			/*-----------------------------Next Process----------------------------------------------------------*/
			// If STATUS_ID = 50 - Reversal of Picking Confirmation
			if (outboundLine.getStatusId() == 50L) {
				/*----------------------STEP 1------------------------------------------------
				 * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from OUTBOUNDLINE table and 
				 * pass the keys in PICKUPLINE table and update STATUS_ID=53 and Delete the record
				 */
				PickupLine pickupLine = pickupLineService.deletePickupLine(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(), 
						outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
						outboundLine.getItemCode(), loginUserID);
				
				// DELETE PICKUP_HEADER
				PickupHeader pickupHeader = pickupHeaderService.deletePickupHeader(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(), 
						outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
						pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode(), loginUserID);
				log.info("pickupHeader deleted : " + pickupHeader);
				
				// DELETE QUALITY_HEADER
				QualityHeader dbQualityHeader = qualityHeaderService.getQualityHeaderForReversal(outboundLine.getWarehouseId(), 
						outboundLine.getPreOutboundNo(), outboundLine.getRefDocNumber(), pickupLine.getPickupNumber(), outboundLine.getPartnerCode());
				if (dbQualityHeader != null) {
					QualityHeader qualityHeader = qualityHeaderService.deleteQualityHeader(outboundLine.getWarehouseId(), 
							outboundLine.getPreOutboundNo(), refDocNumber, dbQualityHeader.getQualityInspectionNo(), 
							dbQualityHeader.getActualHeNo(), loginUserID);
					log.info("QualityHeader----------Deleted-------> : " + qualityHeader);
				}
				
				/*---------------STEP 3-----OrderManagementLine update-------------------------------
				 * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from PICKUPHEADER table and 
				 * pass the keys in ORDERMANAGEMENTLINE table  and update STATUS_ID as 47
				 */
				OrderManagementLine orderManagementLine = updateOrderManagementLine (pickupHeader, loginUserID);
				log.info("orderManagementLine updated : " + orderManagementLine);
				
				/*---------------STEP 3.1-----Inventory update-------------------------------
				 * Pass WH_ID/_ITM_CODE/ST_BIN of BIN_CLASS_ID=4/PACK_BARCODE as PICK_PACK_BARCODE of PICKUPLINE in 
				 * INVENTORY table and update INV_QTY as (INV_QTY - PICK_CNF_QTY ) and 
				 * delete the record If INV_QTY = 0 - (Update 1)								
				 */
				Inventory inventory = updateInventory1(pickupLine);
				
				/*---------------STEP 3.2-----Inventory update-------------------------------
				 * Pass WH_ID/_ITM_CODE/ST_BIN from PICK_ST_BIN/PACK_BARCODE from PICK_PACK_BARCODE of PICKUPLINE in 
				 * INVENTORY table and update INV_QTY as (INV_QTY + PICK_CNF_QTY )- (Update 2)
				 */
				inventory = inventoryService.getInventory(pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(), 
						pickupLine.getItemCode(), pickupLine.getPickedStorageBin());
				
				Double INV_QTY = inventory.getInventoryQuantity() + pickupLine.getPickConfirmQty();
				inventory.setInventoryQuantity(INV_QTY);
				inventory = inventoryRepository.save(inventory);
				log.info("inventory updated : " + inventory);
				
				/*------------------------Record insertion in Outbound Reversal table----------------------------*/
				/////////RECORD-1/////////////////////////////////////////////////////////////////////////////////
				String reversalType = "PICKING";
				Double reversedQty = pickupLine.getPickConfirmQty();
				OutboundReversal createdOutboundReversal = createOutboundReversal (warehouse, reversalType, refDocNumber, outboundLine.getPartnerCode(), itemCode,
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
	private InventoryMovement createInventoryMovement (PickupLine pickupLine, 
			String movementDocumentNo, String storageBin, String movementQtyValue, String loginUserID, boolean isFromDelivery ) 
					throws IllegalAccessException, InvocationTargetException {
		// Flag "isFromDelivery" is not used anywhere. 
		AddInventoryMovement inventoryMovement = new AddInventoryMovement();
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
		// PASS WH_ID/ITM_CODE/BIN_CL_ID and sum the INV_QTY for all selected inventory
		List<Inventory> inventoryList = 
				inventoryService.getInventory (pickupLine.getWarehouseId(), pickupLine.getItemCode(), 1L);
		double sumOfInvQty = inventoryList.stream().mapToDouble(a->a.getInventoryQuantity()).sum();
		inventoryMovement.setBalanceOHQty(sumOfInvQty);
	
		// IM_CTD_BY
		inventoryMovement.setCreatedBy(pickupLine.getPickupConfirmedBy());
		
		// IM_CTD_ON
		inventoryMovement.setCreatedOn(pickupLine.getPickupCreatedOn());

		InventoryMovement createdInventoryMovement = 
				inventoryMovementService.createInventoryMovement(inventoryMovement, loginUserID);
		return createdInventoryMovement;
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
		OrderManagementLine orderManagementLine = orderManagementLineService.updateOrderManagementLine(pickupHeader.getWarehouseId(), pickupHeader.getPreOutboundNo(), 
				pickupHeader.getRefDocNumber(), pickupHeader.getPartnerCode(), pickupHeader.getLineNumber(), 
				pickupHeader.getItemCode(), loginUserID, updateOrderManagementLine);
		log.info("OrderManagementLine updated : " + orderManagementLine);
		return orderManagementLine;
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
	 */
	private Inventory updateInventory1(PickupLine pickupLine) {
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		Long BIN_CLASS_ID = 5L;
		StorageBin storageBin = mastersService.getStorageBin(pickupLine.getWarehouseId(), BIN_CLASS_ID, authTokenForMastersService.getAccess_token());
		Inventory inventory = inventoryService.getInventory(pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(), 
				pickupLine.getItemCode(), storageBin.getStorageBin());
		
		Double INV_QTY = inventory.getInventoryQuantity() - pickupLine.getPickConfirmQty();
		inventory.setInventoryQuantity(INV_QTY);
		
		// INV_QTY > 0 then, update Inventory Table
		inventory = inventoryRepository.save(inventory);
		log.info("Inventory updated : " + inventory);
		
		if (INV_QTY == 0) {
//			[Prod Fix: 28-06] - Discussed to comment delete Inventory operation to avoid unwanted delete of Inventory
//			inventoryRepository.delete(inventory);
			log.info("inventory record is deleted...");
		}
		return inventory;
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
		AXAuthToken authToken = authTokenService.generateAXOAuthToken();
		AXApiResponse apiResponse = 
				warehouseService.postInterWarehouseShipmentConfirmation(interWarehouseShipment, 
						authToken.getAccess_token());
		log.info("apiResponse : " + apiResponse);
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
		AXAuthToken authToken = authTokenService.generateAXOAuthToken();
		AXApiResponse apiResponse = warehouseService.postShipmentConfirmation(shipment, authToken.getAccess_token());
		log.info("apiResponse : " + apiResponse);
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
		AXAuthToken authToken = authTokenService.generateAXOAuthToken();
		AXApiResponse apiResponse = warehouseService.postReturnPOConfirmation(returnPO, authToken.getAccess_token());
		log.info("apiResponse : " + apiResponse);
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
		AXAuthToken authToken = authTokenService.generateAXOAuthToken();
		AXApiResponse apiResponse = warehouseService.postSaleOrderConfirmation(salesOrder, authToken.getAccess_token());
		log.info("apiResponse : " + apiResponse);
		return apiResponse;
	}

	public List<OutboundLine> findOutboundLineReport (SearchOutboundLine searchOutboundLine) {
		// TODO Auto-generated method stub
		return null;
	}
}
