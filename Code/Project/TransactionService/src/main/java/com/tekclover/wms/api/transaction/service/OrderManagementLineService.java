package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.AddOrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.AssignPicker;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.OrderManagementHeader;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.OrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.SearchOrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.UpdateOrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupHeader;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.OrderManagementHeaderRepository;
import com.tekclover.wms.api.transaction.repository.OrderManagementLineRepository;
import com.tekclover.wms.api.transaction.repository.OutboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.OutboundLineRepository;
import com.tekclover.wms.api.transaction.repository.PickupHeaderRepository;
import com.tekclover.wms.api.transaction.repository.specification.OrderManagementLineSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderManagementLineService extends BaseService {
	
	@Autowired
	OrderManagementHeaderRepository orderManagementHeaderRepository;
	
	@Autowired
	OrderManagementLineRepository orderManagementLineRepository;
	
	@Autowired
	InventoryRepository inventoryRepository;
	
	@Autowired
	OutboundHeaderRepository outboundHeaderRepository;
	
	@Autowired
	PickupHeaderRepository pickupHeaderRepository;
	
	@Autowired
	OutboundLineRepository outboundLineRepository;
	
	@Autowired
	OrderManagementHeaderService orderManagementHeaderService;
	
	@Autowired
	InventoryService inventoryService;
	
	@Autowired
	OutboundHeaderService outboundHeaderService;
	
	@Autowired
	OutboundLineService outboundLineService;
	
	@Autowired
	AuthTokenService authTokenService;
	
	@Autowired
	MastersService mastersService;
	
	/**
	 * getOrderManagementLines
	 * @return
	 */
	public List<OrderManagementLine> getOrderManagementLines () {
		List<OrderManagementLine> orderManagementHeaderList =  orderManagementLineRepository.findAll();
		orderManagementHeaderList = orderManagementHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return orderManagementHeaderList;
	}
	
	/**
	 * getOrderManagementLine
	 * @param proposedPackCode 
	 * @param proposedStorageBin 
	 * @param itemCode 
	 * @param lineNumber 
	 * @param partnerCode 
	 * @param refDocNumber2 
	 * @param preOutboundNo 
	 * @param warehouseId 
	 * @param plantId 
	 * @param companyCodeId 
	 * @return
	 * 
	 * Pass the Selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE/PROP_ST_BIN/PROP_PACK_BARCODE in 
	 * ORDERMANAGEMENTLINE table 
	 */
	public OrderManagementLine getOrderManagementLine (String warehouseId, String preOutboundNo, String refDocNumber, 
			String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode) {
		OrderManagementLine orderManagementHeader = 
				orderManagementLineRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode, 0L);
		if (orderManagementHeader != null) {
			return orderManagementHeader;
		} 
		throw new BadRequestException("The given OrderManagementLine ID : " + 
					"warehouseId:" + warehouseId +
					",preOutboundNo:" + preOutboundNo +
					",refDocNumber:" + refDocNumber +
					",partnerCode:" + partnerCode +
					",lineNumber:" + lineNumber +
					",itemCode:" + itemCode +
					",proposedStorageBin:" + proposedStorageBin +
					",proposedPackCode:" + proposedPackCode +
					" doesn't exist.");
	}
	
	/**
	 * Used by Allocation
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @return
	 */
	public OrderManagementLine getOrderManagementLine (String warehouseId, String preOutboundNo, String refDocNumber, 
			String partnerCode, Long lineNumber, String itemCode) {
		OrderManagementLine orderManagementHeader = 
				orderManagementLineRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
		if (orderManagementHeader != null) {
			return orderManagementHeader;
		} 
		throw new BadRequestException("The given OrderManagementLine ID : " + 
					"warehouseId:" + warehouseId +
					",preOutboundNo:" + preOutboundNo +
					",refDocNumber:" + refDocNumber +
					",partnerCode:" + partnerCode +
					",lineNumber:" + lineNumber +
					",itemCode:" + itemCode +
					" doesn't exist.");
	}

	public List<OrderManagementLine> getOrderManagementLineForReversal (String warehouseId, String preOutboundNo, String refDocNumber,
													   String partnerCode, Long lineNumber, String itemCode) {
		List<OrderManagementLine> orderManagementHeader =
				orderManagementLineRepository.findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
		if (orderManagementHeader != null) {
			return orderManagementHeader;
		}
		throw new BadRequestException("The given OrderManagementLine ID : " +
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
	 * @param preOutboundNo
	 * @param lineNumber
	 * @param itemCode
	 * @return
	 */
	public List<OrderManagementLine> getOrderManagementLine (String preOutboundNo, Long lineNumber, String itemCode) {
		List<OrderManagementLine> orderManagementHeader = 
				orderManagementLineRepository.findByPreOutboundNoAndLineNumberAndItemCodeAndDeletionIndicator (
						preOutboundNo, lineNumber, itemCode, 0L);
		if (orderManagementHeader != null) {
			return orderManagementHeader;
		} else {
			throw new BadRequestException("The given OrderManagementLine ID : "
					+ "preOutboundNo" + preOutboundNo
					+ ",lineNumber" + lineNumber
					+ ",itemCode" + itemCode +
					" doesn't exist.");
		}
	}
	
	/**
	 * 
	 * @param searchOrderManagementLine
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException 
	 */
	public List<OrderManagementLine> findOrderManagementLine(SearchOrderManagementLine searchOrderManagementLine) 
			throws ParseException, java.text.ParseException {		
		
		if (searchOrderManagementLine.getStartRequiredDeliveryDate() != null && searchOrderManagementLine.getEndRequiredDeliveryDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOrderManagementLine.getStartRequiredDeliveryDate(), searchOrderManagementLine.getEndRequiredDeliveryDate());
			searchOrderManagementLine.setStartRequiredDeliveryDate(dates[0]);
			searchOrderManagementLine.setEndRequiredDeliveryDate(dates[1]);
		}
		
		if (searchOrderManagementLine.getStartOrderDate() != null && searchOrderManagementLine.getEndOrderDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOrderManagementLine.getStartOrderDate(), searchOrderManagementLine.getEndOrderDate());
			searchOrderManagementLine.setStartOrderDate(dates[0]);
			searchOrderManagementLine.setEndOrderDate(dates[1]);
		}		
		OrderManagementLineSpecification spec = new OrderManagementLineSpecification(searchOrderManagementLine);
		List<OrderManagementLine> searchResults = orderManagementLineRepository.findAll(spec);
		
		/*
		 * Getting StorageBin and SpanID from Masters-StorageBin table
		 */
		try {
			AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
			for (OrderManagementLine orderManagementLine : searchResults) {
				if (orderManagementLine.getProposedStorageBin() != null && 
						orderManagementLine.getProposedStorageBin().trim().length() > 0) {
					StorageBin storageBin =
							mastersService.getStorageBin(orderManagementLine.getProposedStorageBin(), authTokenForMastersService.getAccess_token());
					
					// Ref_Field_9 for storing ST_SEC_ID
					orderManagementLine.setReferenceField9(storageBin.getStorageSectionId());
					
					// Ref_Field_10 for storing SPAN_ID
					orderManagementLine.setReferenceField10(storageBin.getSpanId());
				}
			}
		} catch (Exception e) {
			log.error("Error on : " + e.toString());
			throw new BadRequestException(e.toString());
		}
		return searchResults;
	}
	
	/**
	 * createOrderManagementLine
	 * @param newOrderManagementLine
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public OrderManagementLine createOrderManagementLine (AddOrderManagementLine newOrderManagementLine, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		OrderManagementLine dbOrderManagementLine = new OrderManagementLine();
		log.info("newOrderManagementLine : " + newOrderManagementLine);
		BeanUtils.copyProperties(newOrderManagementLine, dbOrderManagementLine);
		dbOrderManagementLine.setDeletionIndicator(0L);
		return orderManagementLineRepository.save(dbOrderManagementLine);
	}
	
	/**
	 * updateOrderManagementLine
	 * @param loginUserId 
	 * @param refDocNumber
	 * @param updateOrderManagementLine 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public OrderManagementLine doUnAllocation (String warehouseId, String preOutboundNo, String refDocNumber, 
			String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackBarCode, 
			String loginUserID) throws IllegalAccessException, InvocationTargetException {
		OrderManagementLine dbOrderManagementLine = 
				getOrderManagementLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode,
						proposedStorageBin, proposedPackBarCode);
		
		/*
		 * Update Inventory table
		 * --------------------------
		 * Pass the WH_ID/ITM_CODE/PACK_BARCODE(PROP_PACK_BARCODE)/ST_BIN(PROP_ST_BIN) values in INVENTORY table 
		 * update INV_QTY as (INV_QTY+ALLOC_QTY) and change ALLOC_QTY as 0 			
		 */
		String packBarcodes = dbOrderManagementLine.getProposedPackBarCode();
		String storageBin = dbOrderManagementLine.getProposedStorageBin();
		Inventory inventory = inventoryService.getInventory(warehouseId, packBarcodes, itemCode, storageBin);
		Double invQty = inventory.getInventoryQuantity() + dbOrderManagementLine.getAllocatedQty();
		
		/*
		 * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
		 */
		// Start
		if (invQty < 0D) {
			invQty = 0D;
		}
		// End
		
		inventory.setInventoryQuantity(invQty);
		log.info("Inventory invQty: " + invQty);
		
		Double allocQty = inventory.getAllocatedQuantity() - dbOrderManagementLine.getAllocatedQty();
		inventory.setAllocatedQuantity(allocQty);
		log.info("Inventory allocQty: " + allocQty);
		
		inventory = inventoryRepository.save(inventory);
		log.info("Inventory updated: " + inventory);
		
		/*
    	 * 1. Update ALLOC_QTY value as 0
    	 * 2. Update STATUS_ID = 47
    	 */
		dbOrderManagementLine.setAllocatedQty(0D);
		dbOrderManagementLine.setStatusId(47L);	
		dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
		dbOrderManagementLine.setPickupUpdatedOn(new Date());
		OrderManagementLine updatedOrderManagementLine = orderManagementLineRepository.save(dbOrderManagementLine);
		log.info("OrderManagementLine updated: " + updatedOrderManagementLine);
		return dbOrderManagementLine;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @param OB_ORD_TYP_ID
	 * @param ORD_QTY
	 * @param loginUserID 
	 * @param loginUserID 
	 * @return
	 */
	public OrderManagementLine doAllocation (String warehouseId, String preOutboundNo, String refDocNumber, 
			String partnerCode, Long lineNumber, String itemCode, String loginUserID) {
		OrderManagementLine dbOrderManagementLine = 
				getOrderManagementLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode);
		log.info("Selected record: " + dbOrderManagementLine);
		
		Long OB_ORD_TYP_ID = dbOrderManagementLine.getOutboundOrderTypeId();
		Double ORD_QTY = dbOrderManagementLine.getOrderQty();
		
		if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
			List<String> storageSectionIds = Arrays.asList("ZB","ZC","ZG","ZT"); //ZB,ZC,ZG,ZT
			dbOrderManagementLine = updateAllocation (dbOrderManagementLine, storageSectionIds, ORD_QTY, warehouseId, itemCode, loginUserID);
		}
		
		if (OB_ORD_TYP_ID == 2L) {
			List<String> storageSectionIds = Arrays.asList("ZD"); //ZD
			dbOrderManagementLine = updateAllocation (dbOrderManagementLine, storageSectionIds, ORD_QTY, warehouseId, itemCode, loginUserID);
			
		}
		dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
		dbOrderManagementLine.setPickupUpdatedOn(new Date());
		dbOrderManagementLine = orderManagementLineRepository.save (dbOrderManagementLine);
		return dbOrderManagementLine;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @param proposedStorageBin
	 * @param proposedPackCode
	 * @param assignedPickerId
	 * @param loginUserID
	 * @return
	 */
	public List<OrderManagementLine> doAssignPicker(List<AssignPicker> assignPickers, String assignedPickerId, String loginUserID) {
		String warehouseId = null;
		String preOutboundNo = null;
		String refDocNumber = null;
		String partnerCode = null;
		Long lineNumber = null;
		String itemCode = null;		
		String proposedStorageBin = null;
		String proposedPackCode = null;	
		List<OrderManagementLine> orderManagementLineList = new ArrayList<>();
		
		// Iterating over AssignPicker
		for (AssignPicker assignPicker : assignPickers) {
			warehouseId = assignPicker.getWarehouseId();
			preOutboundNo = assignPicker.getPreOutboundNo();
			refDocNumber = assignPicker.getRefDocNumber();
			partnerCode = assignPicker.getPartnerCode();
			lineNumber = assignPicker.getLineNumber();
			itemCode = assignPicker.getItemCode();
			proposedStorageBin = assignPicker.getProposedStorageBin();
			proposedPackCode = assignPicker.getProposedPackCode();
			
			OrderManagementLine dbOrderManagementLine = 
					getOrderManagementLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 
							proposedStorageBin, proposedPackCode);
			
			dbOrderManagementLine.setAssignedPickerId(assignedPickerId);
			dbOrderManagementLine.setStatusId(48L); 	// 2. Update STATUS_ID = 48
			dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
			dbOrderManagementLine.setPickupUpdatedOn(new Date());
			dbOrderManagementLine = orderManagementLineRepository.save (dbOrderManagementLine);
			log.info("dbOrderManagementLine updated : " + dbOrderManagementLine);
			
			/* Update ORDERMANAGEMENTHEADER
			 * ---------------------------------
			 * Pass the Selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE in OUTBOUNDLINE table and 
			 * update SATATU_ID as 48
			 */
			OutboundLine outboundLine = outboundLineService.getOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode);
			outboundLine.setStatusId(48L);
			outboundLine = outboundLineRepository.save(outboundLine);
			log.info("outboundLine updated : " + outboundLine);
			
			// OutboundHeader Update
			OutboundHeader outboundHeader = 
					outboundHeaderService.getOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode);
			outboundHeader.setStatusId(48L);
			outboundHeaderRepository.save(outboundHeader);
			
			// ORDERMANAGEMENTHEADER Update
			OrderManagementHeader orderManagementHeader = 
					orderManagementHeaderService.getOrderManagementHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode);
			orderManagementHeader.setStatusId(48L);
			orderManagementHeaderRepository.save(orderManagementHeader);
			
			// Create Pickup TO Number
			/*
			 * Pass the Selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/ITM_CODE/OBLINE_NO and 
			 * validate PU_NO is Null in ORDERMANAGEMENTLINE table , If yes
			 * 
			 * Create New PU_NO by Pass WH_ID - Userlogged in WH_ID and NUM_RAN_CODE = 10 in NUMBERRANGE table and 
			 * fetch NUM_RAN_CURRENT value of FISCALYEAR=CURRENT YEAR and add +1 and then update in ORDERMANAGEMENTLINE table 
			 * by passing WH_ID/PRE_OB_NO/OB_LINE_NO/REF_DOC_NO/ITM_CODE			
			 */
			log.info("dbOrderManagementLine.getPickupNumber() -----> : " + dbOrderManagementLine.getPickupNumber() );
			if (dbOrderManagementLine.getPickupNumber() == null) {
				long NUM_RAN_CODE = 10;
				String PU_NO = getNextRangeNumber(NUM_RAN_CODE, dbOrderManagementLine.getWarehouseId());
				log.info("PU_NO : " + PU_NO);
				
				// Insertion of Record in PICKUPHEADER tables
				PickupHeader pickupHeader = new PickupHeader();
				BeanUtils.copyProperties(dbOrderManagementLine, pickupHeader, CommonUtils.getNullPropertyNames(dbOrderManagementLine));
				
				// PU_NO
				pickupHeader.setPickupNumber(PU_NO);
				
				// PICK_TO_QTY
				pickupHeader.setPickToQty(dbOrderManagementLine.getAllocatedQty());
				
				// PICK_UOM
				pickupHeader.setPickUom(dbOrderManagementLine.getOrderUom());
				
				// STATUS_ID
				pickupHeader.setStatusId(48L);
				
				// ProposedPackbarcode 
				pickupHeader.setProposedPackBarCode(dbOrderManagementLine.getProposedPackBarCode());
				
				pickupHeader.setPickupCreatedBy(loginUserID);
				pickupHeader.setPickupCreatedOn(new Date());
				
				// REF_FIELD_1
				pickupHeader.setReferenceField1(dbOrderManagementLine.getReferenceField1());
				pickupHeader = pickupHeaderRepository.save(pickupHeader);
				log.info("pickupHeader created : " + pickupHeader);
				
				// Updating Ordermanagementline 
				dbOrderManagementLine.setPickupNumber(PU_NO);
				dbOrderManagementLine = orderManagementLineRepository.save (dbOrderManagementLine);
				log.info("OrderManagementLine updated : " + dbOrderManagementLine);
			}
			orderManagementLineList.add(dbOrderManagementLine);
		}
		return orderManagementLineList;
	}
	
	/**
	 * 
	 * @param dbOrderManagementLine
	 * @param storageSectionIds
	 * @param ORD_QTY
	 * @param warehouseId
	 * @param itemCode
	 * @return
	 */
	private OrderManagementLine updateAllocation (OrderManagementLine orderManagementLine, List<String> storageSectionIds, Double ORD_QTY,
			String warehouseId, String itemCode, String loginUserID) {
		List<Inventory> stBinInventoryList = inventoryService.getInventory(warehouseId, itemCode);
		log.info("---Global---stBinInventoryList-------> : " + stBinInventoryList);
		
		/*
		 * If the Inventory doesn't exists in the Table then inserting 0th record in Ordermanagementline table
		 */
		if (stBinInventoryList.isEmpty()) {
			return updateOrderManagementLine(orderManagementLine);
		}
		
		/* To obtain the SumOfInvQty */
		List<String> stBins = stBinInventoryList.stream().map(Inventory::getStorageBin).collect(Collectors.toList());
		log.info("---Filtered---stBins -----> : " + stBins);
		
		List<Inventory> finalInventoryList = new ArrayList<>();
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
		storageBinPutAway.setStorageBin(stBins);
		storageBinPutAway.setStorageSectionIds(storageSectionIds);
		StorageBin[] storageBin = mastersService.getStorageBin(storageBinPutAway, authTokenForMastersService.getAccess_token());
		log.info("---1----selected----storageBins---from---masters-----> : " + storageBin);
		
		// If the StorageBin returns null, then creating OrderManagementLine table with Zero Alloc_qty and Inv_Qty
		if (storageBin == null) {
			return updateOrderManagementLine(orderManagementLine);
		}
		
		if (storageBin != null && storageBin.length > 0) {
			log.info("----2----selected----storageBins---from---masters-----> : " + Arrays.asList(storageBin));
			
			// Pass the filtered ST_BIN/WH_ID/ITM_CODE/BIN_CL_ID=01/STCK_TYP_ID=1 in Inventory table and fetch SUM (INV_QTY)
			for (StorageBin dbStorageBin : storageBin) {
				List<Inventory> listInventory = 
						inventoryService.getInventoryForOrderMgmt (warehouseId, itemCode, 1L, dbStorageBin.getStorageBin(), 1L);
				log.info("----Selected--Inventory--by--stBin--wise----> : " + listInventory);
				if (listInventory != null) {
					finalInventoryList.addAll(listInventory);
				}
			}
		}
		log.info("Final inventory list###########---> : " + finalInventoryList);
		
		/*
		 * If the Inventory doesn't exists in the Table then inserting 0th record in Ordermanamentline table
		 */
		if (finalInventoryList.isEmpty()) {
			return updateOrderManagementLine(orderManagementLine);
		}
		
		Double tempMaxQty = 0D;
		for (Inventory inventory : finalInventoryList) {
			if (tempMaxQty < inventory.getInventoryQuantity()) {
				tempMaxQty = inventory.getInventoryQuantity();
			}
		}
		
		Inventory maxQtyHoldsInventory = new Inventory();
		for (Inventory inventory : finalInventoryList) {
			if (inventory.getInventoryQuantity() == tempMaxQty) {
				BeanUtils.copyProperties(inventory, maxQtyHoldsInventory, CommonUtils.getNullPropertyNames(inventory));
			}
		}
		log.info("Found ------tempMaxQty-----> : " + tempMaxQty);
		log.info("Found ------tempMaxQty--Inventory---> : " + maxQtyHoldsInventory);
		
		/*
		 * Sorting the list
		 */
		Collections.sort(finalInventoryList, new Comparator<Inventory>() {
            public int compare(Inventory s1, Inventory s2) {
                return ((Double)s2.getInventoryQuantity()).compareTo(s1.getInventoryQuantity());
            }
        });
		
		log.info("Collections------sort-----> : " + finalInventoryList);
		if (ORD_QTY < maxQtyHoldsInventory.getInventoryQuantity()) {
			Long STATUS_ID = 0L;
			Double ALLOC_QTY = 0D;
			
			Double INV_QTY = maxQtyHoldsInventory.getInventoryQuantity();
			
			// INV_QTY
			orderManagementLine.setInventoryQty(INV_QTY);
			
			if (ORD_QTY <= INV_QTY) {
				ALLOC_QTY = ORD_QTY;
			} else if (ORD_QTY > INV_QTY) {
				ALLOC_QTY = INV_QTY;
			} else if (INV_QTY == 0) {
				ALLOC_QTY = 0D;
			}
			log.info ("ALLOC_QTY -----@@--->: " + ALLOC_QTY);
			
			orderManagementLine.setAllocatedQty(ALLOC_QTY);
			orderManagementLine.setReAllocatedQty(ALLOC_QTY);
						
			// STATUS_ID 
			/* if ORD_QTY> ALLOC_QTY , then STATUS_ID is hardcoded as "42" */
			if (ORD_QTY > ALLOC_QTY) {
				STATUS_ID = 42L;
			}
			
			/* if ORD_QTY=ALLOC_QTY,  then STATUS_ID is hardcoded as "43" */
			if (ORD_QTY == ALLOC_QTY) {
				STATUS_ID = 43L;
			}
			
			orderManagementLine.setStatusId(STATUS_ID);
			orderManagementLine.setPickupUpdatedBy(loginUserID);
			orderManagementLine.setPickupUpdatedOn(new Date());
			
			/*
			 * Deleting current record and inserting new record (since UK is allowing to update 
			 * prop_st_bin and Pack_bar_codes columns)
			 */
			try {
				orderManagementLineRepository.delete(orderManagementLine);
				log.info("--#---orderManagementLine--deleted----: " + orderManagementLine);
			} catch (Exception e) {
				log.info("--Error---orderManagementLine--deleted----: " + orderManagementLine);
				e.printStackTrace();
			}
			
			OrderManagementLine newOrderManagementLine = new OrderManagementLine();
			BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine, CommonUtils.getNullPropertyNames(orderManagementLine));
			newOrderManagementLine.setProposedStorageBin(maxQtyHoldsInventory.getStorageBin());
			newOrderManagementLine.setProposedPackBarCode(maxQtyHoldsInventory.getPackBarcodes());
			OrderManagementLine createdOrderManagementLine = orderManagementLineRepository.save(newOrderManagementLine);
			log.info("--1---createdOrderManagementLine newly created------: " + createdOrderManagementLine);
			
			if (createdOrderManagementLine.getAllocatedQty() > 0) {
				// Update Inventory table
				Inventory inventoryForUpdate = inventoryService.getInventory(warehouseId, createdOrderManagementLine.getProposedPackBarCode(), 
						itemCode, createdOrderManagementLine.getProposedStorageBin());
				log.info("-----inventoryForUpdate------> : " + inventoryForUpdate);
				if (inventoryForUpdate == null) {
					throw new BadRequestException("Inventory found as null.");
				}
				
				double dbInventoryQty = 0;
				double dbInvAllocatedQty = 0;
				
				if (inventoryForUpdate.getAllocatedQuantity() != null) {
					dbInvAllocatedQty = inventoryForUpdate.getAllocatedQuantity();
				}
				
				if (inventoryForUpdate.getInventoryQuantity() != null) {
					dbInventoryQty = inventoryForUpdate.getInventoryQuantity();
				}
				
				double inventoryQty = dbInventoryQty - createdOrderManagementLine.getAllocatedQty();
				double allocatedQty = dbInvAllocatedQty + createdOrderManagementLine.getAllocatedQty();
				
				/*
				 * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
				 */
				// Start
				if (inventoryQty < 0) {
					inventoryQty = 0;
				}
				// End
				inventoryForUpdate.setInventoryQuantity(inventoryQty);
				inventoryForUpdate.setAllocatedQuantity(allocatedQty);
				inventoryForUpdate = inventoryRepository.save(inventoryForUpdate);
				log.info("inventoryForUpdate updated: " + inventoryForUpdate);
				
				if (inventoryQty == 0 && allocatedQty == 0) {
					log.info("inventoryForUpdate inventoryQty became zero." + inventoryQty);
					
//					[Prod Fix: 28-06] - Discussed to comment delete Inventory operation to avoid unwanted delete of Inventory
//					inventoryRepository.delete(inventoryForUpdate);  
				}
			}
			return createdOrderManagementLine;
		} else {
			OrderManagementLine newOrderManagementLine = null;
			for (Inventory stBinInventory : finalInventoryList) {
				Long STATUS_ID = 0L;
				Double ALLOC_QTY = 0D;
				
				log.info("\nfinalListInventory Inventory ---->: " + stBinInventory + "\n");
				log.info("\nBin-wise Inventory : " + stBinInventory + "\n");
				
				/*	
				 * ALLOC_QTY
				 * 1. If ORD_QTY< INV_QTY , then ALLOC_QTY = ORD_QTY.
				 * 2. If ORD_QTY>INV_QTY, then ALLOC_QTY = INV_QTY.
				 * If INV_QTY = 0, Auto fill ALLOC_QTY=0
				 */
				Double INV_QTY = stBinInventory.getInventoryQuantity();
				
				// INV_QTY
				orderManagementLine.setInventoryQty(INV_QTY);
				
				if (ORD_QTY <= INV_QTY) {
					ALLOC_QTY = ORD_QTY;
				} else if (ORD_QTY > INV_QTY) {
					ALLOC_QTY = INV_QTY;
				} else if (INV_QTY == 0) {
					ALLOC_QTY = 0D;
				}
				log.info ("ALLOC_QTY -----1--->: " + ALLOC_QTY);
				
				if (orderManagementLine.getStatusId() == 47L) {
					try {
						orderManagementLineRepository.delete(orderManagementLine);
						log.info("--#---orderManagementLine--deleted----: " + orderManagementLine);
					} catch (Exception e) {
						log.info("--Error---orderManagementLine--deleted----: " + orderManagementLine);
						e.printStackTrace();
					}
				}
				
				orderManagementLine.setAllocatedQty(ALLOC_QTY);
				orderManagementLine.setReAllocatedQty(ALLOC_QTY);
							
				// STATUS_ID 
				/* if ORD_QTY> ALLOC_QTY , then STATUS_ID is hardcoded as "42" */
				if (ORD_QTY > ALLOC_QTY) {
					STATUS_ID = 42L;
				}
				
				/* if ORD_QTY=ALLOC_QTY,  then STATUS_ID is hardcoded as "43" */
				if (ORD_QTY == ALLOC_QTY) {
					STATUS_ID = 43L;
				}
				
				orderManagementLine.setStatusId(STATUS_ID);
				orderManagementLine.setPickupUpdatedBy(loginUserID);
				orderManagementLine.setPickupUpdatedOn(new Date());
				
				double allocatedQtyFromOrderMgmt = 0.0;
				
				/*
				 * Deleting current record and inserting new record (since UK is not allowing to update 
				 * prop_st_bin and Pack_bar_codes columns
				 */
				newOrderManagementLine = new OrderManagementLine();
				BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine, CommonUtils.getNullPropertyNames(orderManagementLine));
				newOrderManagementLine.setProposedStorageBin(stBinInventory.getStorageBin());
				newOrderManagementLine.setProposedPackBarCode(stBinInventory.getPackBarcodes());
				OrderManagementLine createdOrderManagementLine = orderManagementLineRepository.save(newOrderManagementLine);
				log.info("--else---createdOrderManagementLine newly created------: " + createdOrderManagementLine);
				allocatedQtyFromOrderMgmt = createdOrderManagementLine.getAllocatedQty();
				
				if (ORD_QTY > ALLOC_QTY) {
					ORD_QTY = ORD_QTY - ALLOC_QTY;
				}
				
//				if (orderManagementLine.getAllocatedQty() > 0) {
				if (allocatedQtyFromOrderMgmt > 0) {
					// Update Inventory table
					Inventory inventoryForUpdate = inventoryService.getInventory(warehouseId, stBinInventory.getPackBarcodes(), 
							itemCode, stBinInventory.getStorageBin());
					
					double dbInventoryQty = 0;
					double dbInvAllocatedQty = 0;
					
					if (inventoryForUpdate.getInventoryQuantity() != null) {
						dbInventoryQty = inventoryForUpdate.getInventoryQuantity();
					}
					
					if (inventoryForUpdate.getAllocatedQuantity() != null) {
						dbInvAllocatedQty = inventoryForUpdate.getAllocatedQuantity();
					}
					
					double inventoryQty = dbInventoryQty - allocatedQtyFromOrderMgmt;
					double allocatedQty = dbInvAllocatedQty + allocatedQtyFromOrderMgmt;
					
					/*
					 * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
					 */
					// Start
					if (inventoryQty < 0) {
						inventoryQty = 0;
					}
					// End
					inventoryForUpdate.setInventoryQuantity(inventoryQty);
					inventoryForUpdate.setAllocatedQuantity(allocatedQty);
					inventoryForUpdate = inventoryRepository.save(inventoryForUpdate);
					log.info("inventoryForUpdate updated: " + inventoryForUpdate);
					
					if (inventoryQty == 0 && allocatedQty == 0) {
						log.info("inventoryForUpdate inventoryQty became zero." + inventoryQty);
						
//						[Prod Fix: 28-06] - Discussed to comment delete Inventory operation to avoid unwanted delete of Inventory
//						inventoryRepository.delete(inventoryForUpdate);
					}
				}
				
				if (ORD_QTY == ALLOC_QTY) {
					log.info("ORD_QTY fully allocated: " + ORD_QTY);
					break; //If the Inventory satisfied the Ord_qty
				}
			}
			log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
			return newOrderManagementLine;
		}
	}
	
	/**
	 * 
	 * @param orderManagementLine
	 * @return
	 */
	private OrderManagementLine updateOrderManagementLine(OrderManagementLine orderManagementLine) {
		orderManagementLine.setStatusId(47L);
		orderManagementLine.setProposedStorageBin("");
		orderManagementLine.setProposedPackBarCode("");
		orderManagementLine.setInventoryQty(0D);
		orderManagementLine.setAllocatedQty(0D);
		orderManagementLine = orderManagementLineRepository.save(orderManagementLine);
		log.info("orderManagementLine created: " + orderManagementLine);
		return orderManagementLine;
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
	 * @param updateOrderManagementLine
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public OrderManagementLine updateOrderManagementLine (String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode, String loginUserID, UpdateOrderManagementLine updateOrderManagementLine) 
					throws IllegalAccessException, InvocationTargetException {
		OrderManagementLine dbOrderManagementLine = getOrderManagementLine(warehouseId, preOutboundNo, refDocNumber, 
				partnerCode, lineNumber, itemCode);
		if (dbOrderManagementLine != null) {
			BeanUtils.copyProperties(updateOrderManagementLine, dbOrderManagementLine, CommonUtils.getNullPropertyNames(updateOrderManagementLine));
			dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
			dbOrderManagementLine.setPickupUpdatedOn(new Date());
			return orderManagementLineRepository.save(dbOrderManagementLine);
		}
		return null;
	}
	
	/**
	 * updateOrderManagementLine
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @param proposedStorageBin
	 * @param proposedPackCode
	 * @param loginUserID
	 * @param updateOrderMangementLine
	 * @return
	 */
	public OrderManagementLine updateOrderManagementLine (String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode,
			String loginUserID, @Valid UpdateOrderManagementLine updateOrderMangementLine) {
		OrderManagementLine dbOrderManagementLine = getOrderManagementLine(warehouseId, preOutboundNo, refDocNumber, 
				partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode);
		if (dbOrderManagementLine != null) {
			BeanUtils.copyProperties(updateOrderMangementLine, dbOrderManagementLine, 
					CommonUtils.getNullPropertyNames(updateOrderMangementLine));
			dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
			dbOrderManagementLine.setPickupUpdatedOn(new Date());
			return orderManagementLineRepository.save(dbOrderManagementLine);
		}
		return null;
	}
	
	/**
	 * deleteOrderManagementLine
	 * @param loginUserID 
	 * @param refDocNumber
	 */
	public void deleteOrderManagementLine (String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode, String loginUserID) {
		OrderManagementLine orderManagementHeader = getOrderManagementLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode);
		if ( orderManagementHeader != null) {
			orderManagementHeader.setDeletionIndicator(1L);
			orderManagementLineRepository.save(orderManagementHeader);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + refDocNumber);
		}
	}
}
