package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.BomHeader;
import com.tekclover.wms.api.transaction.model.dto.BomLine;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.dto.StatusId;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.OrderManagementHeader;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.OrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.AddPreOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.OutboundIntegrationHeader;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.OutboundIntegrationLine;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.OutboundIntegrationLog;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.PreOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.PreOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.SearchPreOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.UpdatePreOutboundHeader;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.OrderManagementHeaderRepository;
import com.tekclover.wms.api.transaction.repository.OrderManagementLineRepository;
import com.tekclover.wms.api.transaction.repository.OutboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.OutboundIntegrationLogRepository;
import com.tekclover.wms.api.transaction.repository.OutboundLineRepository;
import com.tekclover.wms.api.transaction.repository.PreOutboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PreOutboundLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.PreOutboundHeaderSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PreOutboundHeaderService extends BaseService {
	
	@Autowired
	private PreOutboundHeaderRepository preOutboundHeaderRepository;
	
	@Autowired
	private PreOutboundLineRepository preOutboundLineRepository;
	
	@Autowired
	private OrderManagementHeaderRepository orderManagementHeaderRepository;
	
	@Autowired
	private OutboundHeaderRepository outboundHeaderRepository;
	
	@Autowired
	private OutboundLineRepository outboundLineRepository;
	
	@Autowired
	private OrderManagementLineRepository orderManagementLineRepository;
	
	@Autowired
	private InventoryService inventoryService;
	
	@Autowired
	private OutboundLineService outboundLineService;
	
	@Autowired
	InventoryRepository inventoryRepository;
	
	@Autowired
	private OrderManagementHeaderService orderManagementHeaderService;
	
	@Autowired
	private OrderManagementLineService orderManagementLineService;
	
	@Autowired
	private OutboundIntegrationLogRepository outboundIntegrationLogRepository;
	
	@Autowired
	private MastersService mastersService;
	
	@Autowired
	private OrderService orderService;
	
	/**
	 * getPreOutboundHeaders
	 * @return
	 */
	public List<PreOutboundHeader> getPreOutboundHeaders () {
		List<PreOutboundHeader> preOutboundHeaderList =  preOutboundHeaderRepository.findAll();
		preOutboundHeaderList = preOutboundHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return preOutboundHeaderList;
	}
	
	/**
	 * getPreOutboundHeader
	 * @param preOutboundNo
	 * @return
	 */
	public PreOutboundHeader getPreOutboundHeader (String warehouseId, String refDocNumber, String preOutboundNo, String partnerCode) {
		Optional<PreOutboundHeader> preOutboundHeader = 
				preOutboundHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndPartnerCodeAndDeletionIndicator(
						getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, refDocNumber, preOutboundNo, partnerCode, 0L);
		if (!preOutboundHeader.isEmpty()) {
			return preOutboundHeader.get();
		} 
		return null;
	}
	
	/**
	 * 
	 * @param preOutboundNo
	 * @return
	 */
	public PreOutboundHeader getPreOutboundHeader (String preOutboundNo) {
		PreOutboundHeader preOutboundHeader = preOutboundHeaderRepository.findByPreOutboundNo(preOutboundNo);
		if (preOutboundHeader != null && preOutboundHeader.getDeletionIndicator() == 0) {
			return preOutboundHeader;
		} else {
			throw new BadRequestException("The given PreOutboundHeader ID : " + preOutboundNo + " doesn't exist.");
		}
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param refDocNumber
	 * @return
	 */
	public PreOutboundHeader getPreOutboundHeader (String warehouseId, String refDocNumber) {
		Optional<PreOutboundHeader> preOutboundHeader = 
				preOutboundHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
						getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, refDocNumber, 0L);
		if (!preOutboundHeader.isEmpty()) {
			return preOutboundHeader.get();
		} 
		return null;
	}
	
	/**
	 * 
	 * @param searchPreOutboundHeader
	 * @return
	 * @throws Exception
	 */
	public List<PreOutboundHeader> findPreOutboundHeader(SearchPreOutboundHeader searchPreOutboundHeader) 
			throws Exception {
		
		if (searchPreOutboundHeader.getStartRequiredDeliveryDate() != null && searchPreOutboundHeader.getEndRequiredDeliveryDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreOutboundHeader.getStartRequiredDeliveryDate(), searchPreOutboundHeader.getEndRequiredDeliveryDate());
			searchPreOutboundHeader.setStartRequiredDeliveryDate(dates[0]);
			searchPreOutboundHeader.setEndRequiredDeliveryDate(dates[1]);
		}
		
		if (searchPreOutboundHeader.getStartOrderDate() != null && searchPreOutboundHeader.getStartOrderDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreOutboundHeader.getStartOrderDate(), searchPreOutboundHeader.getEndOrderDate());
			searchPreOutboundHeader.setStartOrderDate(dates[0]);
			searchPreOutboundHeader.setEndOrderDate(dates[1]);
		}
		
		if (searchPreOutboundHeader.getStartCreatedOn() != null && searchPreOutboundHeader.getStartCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreOutboundHeader.getStartCreatedOn(), searchPreOutboundHeader.getEndCreatedOn());
			searchPreOutboundHeader.setStartCreatedOn(dates[0]);
			searchPreOutboundHeader.setEndCreatedOn(dates[1]);
		}
		
		PreOutboundHeaderSpecification spec = new PreOutboundHeaderSpecification(searchPreOutboundHeader);
		List<PreOutboundHeader> results = preOutboundHeaderRepository.findAll(spec);
		return results;
	}
	
	/**
	 * 
	 * @param searchPreOutboundHeader
	 * @return
	 * @throws Exception
	 */
	public Stream<PreOutboundHeader> findPreOutboundHeaderNew(SearchPreOutboundHeader searchPreOutboundHeader)
			throws Exception {

		if (searchPreOutboundHeader.getStartRequiredDeliveryDate() != null && searchPreOutboundHeader.getEndRequiredDeliveryDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreOutboundHeader.getStartRequiredDeliveryDate(), searchPreOutboundHeader.getEndRequiredDeliveryDate());
			searchPreOutboundHeader.setStartRequiredDeliveryDate(dates[0]);
			searchPreOutboundHeader.setEndRequiredDeliveryDate(dates[1]);
		}

		if (searchPreOutboundHeader.getStartOrderDate() != null && searchPreOutboundHeader.getStartOrderDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreOutboundHeader.getStartOrderDate(), searchPreOutboundHeader.getEndOrderDate());
			searchPreOutboundHeader.setStartOrderDate(dates[0]);
			searchPreOutboundHeader.setEndOrderDate(dates[1]);
		}

		if (searchPreOutboundHeader.getStartCreatedOn() != null && searchPreOutboundHeader.getStartCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreOutboundHeader.getStartCreatedOn(), searchPreOutboundHeader.getEndCreatedOn());
			searchPreOutboundHeader.setStartCreatedOn(dates[0]);
			searchPreOutboundHeader.setEndCreatedOn(dates[1]);
		}

		PreOutboundHeaderSpecification spec = new PreOutboundHeaderSpecification(searchPreOutboundHeader);
		Stream<PreOutboundHeader> preOutboundHeaderList = preOutboundHeaderRepository.stream(spec, PreOutboundHeader.class);
		return preOutboundHeaderList;
	}
	
	/**
	 * createPreOutboundHeader
	 * @param newPreOutboundHeader
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PreOutboundHeader createPreOutboundHeader (AddPreOutboundHeader newPreOutboundHeader, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PreOutboundHeader dbPreOutboundHeader = new PreOutboundHeader();
		log.info("newPreOutboundHeader : " + newPreOutboundHeader);
		BeanUtils.copyProperties(newPreOutboundHeader, dbPreOutboundHeader, CommonUtils.getNullPropertyNames(newPreOutboundHeader));
		
		dbPreOutboundHeader.setDeletionIndicator(0L);
		dbPreOutboundHeader.setCreatedBy(loginUserID);
		dbPreOutboundHeader.setUpdatedBy(loginUserID);
		dbPreOutboundHeader.setCreatedOn(new Date());
		dbPreOutboundHeader.setUpdatedOn(new Date());
		return preOutboundHeaderRepository.save(dbPreOutboundHeader);
	}
	
	/**
	 * updatePreOutboundHeader
	 * @param loginUserId 
	 * @param preOutboundNo
	 * @param updatePreOutboundHeader
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PreOutboundHeader updatePreOutboundHeader (String warehouseId, String refDocNumber, String preOutboundNo, String partnerCode, 
			String loginUserID, UpdatePreOutboundHeader updatePreOutboundHeader) 
			throws IllegalAccessException, InvocationTargetException {
		PreOutboundHeader dbPreOutboundHeader = getPreOutboundHeader (warehouseId, refDocNumber, preOutboundNo, partnerCode);
		BeanUtils.copyProperties(updatePreOutboundHeader, dbPreOutboundHeader, CommonUtils.getNullPropertyNames(updatePreOutboundHeader));
		dbPreOutboundHeader.setUpdatedBy(loginUserID);
		dbPreOutboundHeader.setUpdatedOn(new Date());
		return preOutboundHeaderRepository.save(dbPreOutboundHeader);
	}
	
	/**
	 * deletePreOutboundHeader
	 * @param loginUserID 
	 * @param preOutboundNo
	 */
	public void deletePreOutboundHeader (String languageId, String companyCodeId, String plantId, String warehouseId, String refDocNumber, String preOutboundNo, String partnerCode, String loginUserID) {
		PreOutboundHeader preOutboundHeader = getPreOutboundHeader(preOutboundNo);
		if ( preOutboundHeader != null) {
			preOutboundHeader.setDeletionIndicator(1L);
			preOutboundHeader.setUpdatedBy(loginUserID);
			preOutboundHeaderRepository.save(preOutboundHeader);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + preOutboundNo);
		}
	}
	
	/*----------------------PROCESS-OUTBOUND-RECEIVED----------------------------------------------*/
	/*
     * Process the PreoutboundIntegraion data
     */
	@Transactional
	public OutboundHeader processOutboundReceived (OutboundIntegrationHeader outboundIntegrationHeader) 
			throws IllegalAccessException, InvocationTargetException, BadRequestException, Exception {
		/*
		 * Checking whether received refDocNumber processed already.
		 */
		Optional<PreOutboundHeader> orderProcessedStatus = 
				preOutboundHeaderRepository.findByRefDocNumberAndDeletionIndicator(outboundIntegrationHeader.getRefDocumentNo(), 0);
		if (!orderProcessedStatus.isEmpty()) {
			orderService.updateProcessedOrder(outboundIntegrationHeader.getRefDocumentNo());
			throw new BadRequestException("Order :" + outboundIntegrationHeader.getRefDocumentNo() + 
					" already processed. Reprocessing can't be allowed.");
		}
		
		String warehouseId = outboundIntegrationHeader.getWarehouseID();
		log.info("warehouseId : " + warehouseId);
		
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		Warehouse warehouse = getWarehouse (warehouseId);
		log.info("warehouse : " + warehouse);
		if (warehouse == null) {
			log.info("warehouse not found.");
			throw new BadRequestException("Warehouse cannot be null.");
		}
		
		// Getting PreOutboundNo from NumberRangeTable
		String preOutboundNo = getPreOutboundNo(warehouseId);
		String refField1ForOrderType = null;
		
		List<PreOutboundLine> overallCreatedPreoutboundLineList = new ArrayList<>();
		for (OutboundIntegrationLine outboundIntegrationLine : outboundIntegrationHeader.getOutboundIntegrationLine()) {
			log.info("outboundIntegrationLine : " + outboundIntegrationLine);
			
			/*-------------Insertion of BOM item in preOutboundLine table---------------------------------------------------------*/
			/*
			 * Before Insertion into PREOUTBOUNDLINE table , validate the Below
			 * Pass the WH_ID/ITM_CODE as PAR_ITM_CODE into BOMHEADER table and validate the records, 
			 * If record is not Null then fetch BOM_NO Pass BOM_NO in BOMITEM table and fetch CHL_ITM_CODE and 
			 * CHL_ITM_QTY values and insert along with PAR_ITM_CODE in PREOUTBOUNDLINE table as below
			 * Insertion of CHL_ITM_CODE values
			 */
			BomHeader bomHeader = mastersService.getBomHeader(outboundIntegrationLine.getItemCode(), 
					warehouseId, authTokenForMastersService.getAccess_token());
			if (bomHeader != null) {
				BomLine[] bomLine = mastersService.getBomLine(bomHeader.getBomNumber(), bomHeader.getWarehouseId(), 
						authTokenForMastersService.getAccess_token());
				List<PreOutboundLine> toBeCreatedpreOutboundLineList = new ArrayList<>();
				for (BomLine dbBomLine : bomLine) {
					toBeCreatedpreOutboundLineList.add(createPreoutboundLineBOMBased (warehouse.getCompanyCode(), 
							warehouse.getPlantId(), preOutboundNo , outboundIntegrationHeader, dbBomLine, outboundIntegrationLine));
				}
				
				// Batch Insert - preOutboundLines
				if (!toBeCreatedpreOutboundLineList.isEmpty()) {
					List<PreOutboundLine> createdpreOutboundLine = preOutboundLineRepository.saveAll(toBeCreatedpreOutboundLineList);
					log.info("createdpreOutboundLine [BOM] : " + createdpreOutboundLine);
					overallCreatedPreoutboundLineList.addAll(createdpreOutboundLine);
				}
			}
			refField1ForOrderType = outboundIntegrationLine.getRefField1ForOrderType();
		}
		
		/*
		 * Inserting BOM Line records in OutboundLine and OrderManagementLine
		 */
		if (!overallCreatedPreoutboundLineList.isEmpty()) {
			for (PreOutboundLine preOutboundLine : overallCreatedPreoutboundLineList) {
				// OrderManagementLine
				OrderManagementLine orderManagementLine = createOrderManagementLine(warehouse.getCompanyCode(), 
						warehouse.getPlantId(), preOutboundNo, outboundIntegrationHeader, preOutboundLine);
				log.info("orderManagementLine created---BOM---> : " + orderManagementLine);
			}
			
			/*------------------Record Insertion in OUTBOUNDLINE table--for BOM---------*/	
			List<OutboundLine> createOutboundLineListForBOM = createOutboundLine (overallCreatedPreoutboundLineList);
			log.info("createOutboundLine created : " + createOutboundLineListForBOM);
		}
			
		/*
		 * Append PREOUTBOUNDLINE table through below logic
		 */
		List<PreOutboundLine> createdPreOutboundLineList = new ArrayList<>();
		for (OutboundIntegrationLine outboundIntegrationLine : outboundIntegrationHeader.getOutboundIntegrationLine()) {
			// PreOutboundLine
			try {
				PreOutboundLine preOutboundLine = createPreOutboundLine (warehouse.getCompanyCode(), 
						warehouse.getPlantId(), preOutboundNo, outboundIntegrationHeader, outboundIntegrationLine);
				PreOutboundLine createdPreOutboundLine = preOutboundLineRepository.save(preOutboundLine);
				log.info("preOutboundLine created---1---> : " + createdPreOutboundLine);
				createdPreOutboundLineList.add(createdPreOutboundLine);
				
				// OrderManagementLine
				OrderManagementLine orderManagementLine = createOrderManagementLine(warehouse.getCompanyCode(), 
						warehouse.getPlantId(), preOutboundNo, outboundIntegrationHeader, preOutboundLine);
				log.info("orderManagementLine created---1---> : " + orderManagementLine);
			} catch (Exception e) {
				log.error("Error on processing Preoutboudline : " + e.toString());
				e.printStackTrace();
			}
		}
		
		/*------------------Record Insertion in OUTBOUNDLINE tables-----------*/
		List<OutboundLine> createOutboundLineList = createOutboundLine (createdPreOutboundLineList);
		log.info("createOutboundLine created : " + createOutboundLineList);
			
		/*------------------Insert into PreOutboundHeader table-----------------------------*/
		PreOutboundHeader createdPreOutboundHeader = createPreOutboundHeader (warehouse.getCompanyCode(), 
				warehouse.getPlantId(), preOutboundNo, outboundIntegrationHeader, refField1ForOrderType);
		log.info("preOutboundHeader Created : " + createdPreOutboundHeader);

		/*------------------ORDERMANAGEMENTHEADER TABLE-------------------------------------*/
		OrderManagementHeader createdOrderManagementHeader = createOrderManagementHeader(createdPreOutboundHeader);
		log.info("OrderMangementHeader Created : " + createdOrderManagementHeader);
		
		/*------------------Record Insertion in OUTBOUNDHEADER/OUTBOUNDLINE tables-----------*/		
		OutboundHeader outboundHeader = createOutboundHeader (createdPreOutboundHeader, createdOrderManagementHeader.getStatusId());
		
		/*------------------------------------------------------------------------------------*/
		updateStatusAs47ForOBHeader(warehouseId, preOutboundNo, outboundHeader.getRefDocNumber());
		return outboundHeader;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 */
	private void updateStatusAs47ForOBHeader(String warehouseId, String preOutboundNo, String refDocNumber) {
		List<OutboundLine> outboundLineList = outboundLineService.getOutboundLine(warehouseId, preOutboundNo, refDocNumber);
		long matchedCount = outboundLineList.stream().filter(a->a.getStatusId() == 47L).count();
		boolean isConditionMet = (matchedCount == outboundLineList.size());
		Long STATUS_ID_47 = 47L;
		if (isConditionMet) {
			//----------------Outbound Header update----------------------------------------------------------------------------------------
			outboundHeaderRepository.updateOutboundHeaderStatusAs47 (warehouseId, refDocNumber, STATUS_ID_47);
			log.info("OutboundHeader status updated as 47. ");
			
			//----------------Preoutbound Header--------------------------------------------------------------------------------------------
			AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
			StatusId idStatus = idmasterService.getStatus(STATUS_ID_47, warehouseId, authTokenForIDService.getAccess_token());
			preOutboundHeaderRepository.updatePreOutboundHeaderStatus(warehouseId, refDocNumber, STATUS_ID_47, idStatus.getStatus());
			log.info("PreOutbound Header status updated as 47.");
		}
	}

	/**
	 * 
	 * @param createdPreOutboundLine
	 * @return 
	 */
	private List<OutboundLine> createOutboundLine(List<PreOutboundLine> createdPreOutboundLine) {
		List<OutboundLine> outboundLines = new ArrayList<>();
		for (PreOutboundLine preOutboundLine : createdPreOutboundLine) {
			List<OrderManagementLine> orderManagementLine = orderManagementLineService.getOrderManagementLine(preOutboundLine.getPreOutboundNo(), 
					preOutboundLine.getLineNumber(), preOutboundLine.getItemCode());
			for (OrderManagementLine dbOrderManagementLine : orderManagementLine) {
				OutboundLine outboundLine = new OutboundLine();
				BeanUtils.copyProperties(preOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(preOutboundLine));
				outboundLine.setDeliveryQty(0D);
				outboundLine.setStatusId(dbOrderManagementLine.getStatusId());
				outboundLine.setCreatedBy(preOutboundLine.getCreatedBy());
				outboundLine.setCreatedOn(preOutboundLine.getCreatedOn());
				outboundLines.add(outboundLine);
			}
		}
		
		outboundLines = outboundLineRepository.saveAll(outboundLines);
		log.info("outboundLines created -----2------>: " + outboundLines);
		return outboundLines;
	}

	/**
	 * 
	 * @param createdPreOutboundHeader
	 * @param statusId
	 * @return 
	 * @throws ParseException 
	 */
	private OutboundHeader createOutboundHeader(PreOutboundHeader createdPreOutboundHeader, Long statusId) throws ParseException {
		OutboundHeader outboundHeader = new OutboundHeader();
		BeanUtils.copyProperties(createdPreOutboundHeader, outboundHeader, 
				CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
		OrderManagementHeader dbOrderManagementHeader = orderManagementHeaderService.getOrderManagementHeader (createdPreOutboundHeader.getPreOutboundNo());
		
		/*
		 * Setting up KuwaitTime
		 */
		Date kwtDate = DateUtils.getCurrentKWTDateTime();
		outboundHeader.setRefDocDate(kwtDate);	
		outboundHeader.setStatusId (dbOrderManagementHeader.getStatusId());
		outboundHeader.setCreatedBy(createdPreOutboundHeader.getCreatedBy());
		outboundHeader.setCreatedOn(createdPreOutboundHeader.getCreatedOn());
		outboundHeader = outboundHeaderRepository.save(outboundHeader);
		log.info ("Created outboundHeader : " + outboundHeader);
		return outboundHeader;
	}

	/**
	 * 
	 * @param createdPreOutboundHeader
	 * @return
	 */
	private OrderManagementHeader createOrderManagementHeader(PreOutboundHeader createdPreOutboundHeader) {
		OrderManagementHeader newOrderManagementHeader = new OrderManagementHeader();
		BeanUtils.copyProperties(createdPreOutboundHeader, newOrderManagementHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
		
		AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
		StatusId idStatus = idmasterService.getStatus(41L, createdPreOutboundHeader.getWarehouseId(), authTokenForIDService.getAccess_token());
		newOrderManagementHeader.setStatusId(41L);	// Hard Coded Value "41"
		newOrderManagementHeader.setReferenceField7(idStatus.getStatus());	
		OrderManagementHeader createdOrderMangementHeader = orderManagementHeaderRepository.save(newOrderManagementHeader);
		return createdOrderMangementHeader;
	}

	/**
	 * 
	 * @param companyCodeId
	 * @param plantId
	 * @param preOutboundNo
	 * @param outboundIntegrationHeader
	 * @param refField1ForOrderType 
	 * @return
	 * @throws ParseException 
	 */
	private PreOutboundHeader createPreOutboundHeader(String companyCodeId, String plantId, String preOutboundNo,
			OutboundIntegrationHeader outboundIntegrationHeader, String refField1ForOrderType) throws ParseException {
		AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
		PreOutboundHeader preOutboundHeader = new PreOutboundHeader();
		preOutboundHeader.setLanguageId("EN");											
		preOutboundHeader.setCompanyCodeId(companyCodeId);
		preOutboundHeader.setPlantId(plantId);
		preOutboundHeader.setWarehouseId(outboundIntegrationHeader.getWarehouseID());
		preOutboundHeader.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());
		preOutboundHeader.setPreOutboundNo(preOutboundNo);												// PRE_OB_NO
		preOutboundHeader.setPartnerCode(outboundIntegrationHeader.getPartnerCode());
		preOutboundHeader.setOutboundOrderTypeId(outboundIntegrationHeader.getOutboundOrderTypeID());	// Hardcoded value "0"
		preOutboundHeader.setReferenceDocumentType("SO");												// Hardcoded value "SO"
		
		/*
		 * Setting up KuwaitTime
		 */
		Date kwtDate = DateUtils.getCurrentKWTDateTime();
		preOutboundHeader.setRefDocDate(kwtDate);
		preOutboundHeader.setStatusId(39L);
		preOutboundHeader.setRequiredDeliveryDate(outboundIntegrationHeader.getRequiredDeliveryDate());
		
		// REF_FIELD_1
		preOutboundHeader.setReferenceField1(refField1ForOrderType); 
		
		// Status Description
		StatusId idStatus = idmasterService.getStatus(39L, outboundIntegrationHeader.getWarehouseID(), authTokenForIDService.getAccess_token());
		
		// REF_FIELD_10
		preOutboundHeader.setReferenceField10(idStatus.getStatus()); 
				
		preOutboundHeader.setDeletionIndicator(0L);
		preOutboundHeader.setCreatedBy("MSD_INT");
		preOutboundHeader.setCreatedOn(kwtDate);	
		PreOutboundHeader createdPreOutboundHeader = preOutboundHeaderRepository.save(preOutboundHeader);
		log.info("createdPreOutboundHeader : " + createdPreOutboundHeader);
		return createdPreOutboundHeader;
	}

	/**
	 * 
	 * @param companyCodeId
	 * @param plantId
	 * @param preOutboundNo
	 * @param outboundIntegrationHeader
	 * @param dbBomLine
	 * @param outboundIntegrationLine
	 * @return
	 */
	private PreOutboundLine createPreoutboundLineBOMBased(String companyCodeId, String plantId, String preOutboundNo,
			OutboundIntegrationHeader outboundIntegrationHeader, BomLine dbBomLine,
			OutboundIntegrationLine outboundIntegrationLine) {
		Warehouse warehouse = getWarehouse(outboundIntegrationHeader.getWarehouseID());
		
		PreOutboundLine preOutboundLine = new PreOutboundLine();
		preOutboundLine.setLanguageId(warehouse.getLanguageId());
		preOutboundLine.setCompanyCodeId(companyCodeId);
		preOutboundLine.setPlantId(plantId);

		// WH_ID
		preOutboundLine.setWarehouseId(outboundIntegrationHeader.getWarehouseID());
		
		// PRE_IB_NO
		preOutboundLine.setPreOutboundNo(preOutboundNo);
				
		// REF_DOC_NO
		preOutboundLine.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());
		
		// OB_ORD_TYP_ID
		preOutboundLine.setOutboundOrderTypeId(Long.valueOf(outboundIntegrationHeader.getOutboundOrderTypeID()));
		
		// IB__LINE_NO
		preOutboundLine.setLineNumber(outboundIntegrationLine.getLineReference());
		
		// ITM_CODE
		preOutboundLine.setItemCode(dbBomLine.getChildItemCode());
		
		// ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		//HAREESH 27-08-2022 - bom line creation get item description based on child item code change
		ImBasicData1 imBasicData1 = 
				mastersService.getImBasicData1ByItemCode(dbBomLine.getChildItemCode(),
						outboundIntegrationHeader.getWarehouseID(), authTokenForMastersService.getAccess_token());
		if (imBasicData1 != null) {
			preOutboundLine.setDescription(imBasicData1.getDescription());
		}
		
		// PARTNER_CODE
		preOutboundLine.setPartnerCode(outboundIntegrationHeader.getPartnerCode());
		
		// ORD_QTY
		double orderQuantity = outboundIntegrationLine.getOrderedQty() * dbBomLine.getChildItemQuantity();
		preOutboundLine.setOrderQty(orderQuantity);
		
		// ORD_UOM
		preOutboundLine.setOrderUom(outboundIntegrationLine.getUom());
		
		// REQ_DEL_DATE
		preOutboundLine.setRequiredDeliveryDate(outboundIntegrationHeader.getRequiredDeliveryDate());
		
		// MFR
		preOutboundLine.setManufacturerPartNo(imBasicData1.getManufacturerPartNo());
		
		// STCK_TYP_ID
		preOutboundLine.setStockTypeId(1L);
		
		// SP_ST_IND_ID
		preOutboundLine.setSpecialStockIndicatorId(1L);
		
		// STATUS_ID
		preOutboundLine.setStatusId(39L);
		
		// REF_FIELD_1
		preOutboundLine.setReferenceField1(outboundIntegrationLine.getRefField1ForOrderType());
		
		// REF_FIELD_2
		preOutboundLine.setReferenceField2("BOM");
		
		preOutboundLine.setDeletionIndicator(0L);
		preOutboundLine.setCreatedBy("MSD_INT");
		preOutboundLine.setCreatedOn(new Date());	
		return preOutboundLine;
	}
	
	/**
	 * 
	 * @param companyCode
	 * @param plantID
	 * @param preOutboundNo
	 * @param outboundIntegrationHeader
	 * @param outboundIntegrationLine
	 * @return
	 */
	private PreOutboundLine createPreOutboundLine(String companyCodeId, String plantId, String preOutboundNo,
			OutboundIntegrationHeader outboundIntegrationHeader, OutboundIntegrationLine outboundIntegrationLine) {
		PreOutboundLine preOutboundLine = new PreOutboundLine();
		
		preOutboundLine.setLanguageId("EN");
		preOutboundLine.setCompanyCodeId(companyCodeId);
		preOutboundLine.setPlantId(plantId);
		
		// WH_ID
		preOutboundLine.setWarehouseId(outboundIntegrationHeader.getWarehouseID());
		
		// REF DOC Number
		preOutboundLine.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());
		
		// PRE_IB_NO
		preOutboundLine.setPreOutboundNo(preOutboundNo);
		
		// PARTNER_CODE
		preOutboundLine.setPartnerCode(outboundIntegrationHeader.getPartnerCode());
		
		// IB__LINE_NO
		preOutboundLine.setLineNumber(outboundIntegrationLine.getLineReference());
		
		// ITM_CODE
		preOutboundLine.setItemCode(outboundIntegrationLine.getItemCode());
		
		// OB_ORD_TYP_ID
		preOutboundLine.setOutboundOrderTypeId(outboundIntegrationHeader.getOutboundOrderTypeID());
		
		// STATUS_ID
		preOutboundLine.setStatusId(39L);
		
		// STCK_TYP_ID
		preOutboundLine.setStockTypeId(1L);
		
		// SP_ST_IND_ID
		preOutboundLine.setSpecialStockIndicatorId(1L);
		
		// ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		ImBasicData1 imBasicData1 = 
				mastersService.getImBasicData1ByItemCode(outboundIntegrationLine.getItemCode(), 
						outboundIntegrationHeader.getWarehouseID(),
						authTokenForMastersService.getAccess_token());
		log.info("imBasicData1 : " + imBasicData1);
		if (imBasicData1 != null && imBasicData1.getDescription() != null) {
			preOutboundLine.setDescription(imBasicData1.getDescription());
		} else {
			preOutboundLine.setDescription(outboundIntegrationLine.getItemText());
		}
		
		// ORD_QTY
		preOutboundLine.setOrderQty(outboundIntegrationLine.getOrderedQty());
		
		// ORD_UOM
		preOutboundLine.setOrderUom(outboundIntegrationLine.getUom());
		
		// REQ_DEL_DATE
		preOutboundLine.setRequiredDeliveryDate(outboundIntegrationHeader.getRequiredDeliveryDate());
		
		// REF_FIELD_1
		preOutboundLine.setReferenceField1(outboundIntegrationLine.getRefField1ForOrderType());
		
		preOutboundLine.setDeletionIndicator(0L);
		preOutboundLine.setCreatedBy("MSD_INT");
		preOutboundLine.setCreatedOn(new Date());	
		
		log.info("preOutboundLine : " + preOutboundLine);
		return preOutboundLine;
	}
	
	/**
	 * ORDERMANAGEMENTLINE TABLE	
	 * @param companyCodeId
	 * @param plantId
	 * @param preOutboundNo
	 * @param outboundIntegrationHeader
	 * @param preOutboundLine
	 * @return
	 */
	private OrderManagementLine createOrderManagementLine(String companyCodeId, String plantId, String preOutboundNo,
			OutboundIntegrationHeader outboundIntegrationHeader, PreOutboundLine preOutboundLine) {
		OrderManagementLine orderManagementLine = new OrderManagementLine();
		BeanUtils.copyProperties(preOutboundLine, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLine));
		
		// INV_QTY
		/*
		 * 1. If OB_ORD_TYP_ID = 0,1,3
		 * Pass WH_ID/ITM_CODE in INVENTORY table and fetch ST_BIN. 
		 * Pass ST_BIN into STORAGEBIN table and filter ST_BIN values by ST_SEC_ID values of ZB,ZC,ZG,ZT and 
		 * PUTAWAY_BLOCK and PICK_BLOCK are false(Null).
		 * 
		 * 2. If OB_ORD_TYP_ID = 2
		 * Pass  WH_ID/ITM_CODE in INVENTORY table and fetch ST_BIN.
		 * Pass ST_BIN into STORAGEBIN table and filter ST_BIN values by ST_SEC_ID values of ZD and PUTAWAY_BLOCK 
		 * and PICK_BLOCK are false(Null).
		 * 
		 * Pass the filtered ST_BIN/WH_ID/ITM_CODE/BIN_CL_ID=01/STCK_TYP_ID=1 in Inventory table and fetch Sum(INV_QTY)"
		 */
		log.info("orderManagementLine : " + orderManagementLine);
		
		Long OB_ORD_TYP_ID = outboundIntegrationHeader.getOutboundOrderTypeID();
		if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
			List<String> storageSectionIds = Arrays.asList("ZB","ZC","ZG","ZT"); //ZB,ZC,ZG,ZT
			orderManagementLine = createOrderManagement (storageSectionIds, orderManagementLine, preOutboundLine.getWarehouseId(), 
					preOutboundLine.getItemCode(), preOutboundLine.getOrderQty());
		}
		
		if (OB_ORD_TYP_ID == 2L) {
			List<String> storageSectionIds = Arrays.asList("ZD"); //ZD
			orderManagementLine = createOrderManagement (storageSectionIds, orderManagementLine, preOutboundLine.getWarehouseId(), 
					preOutboundLine.getItemCode(), preOutboundLine.getOrderQty());
		}
		
		// PROP_ST_BIN
		return orderManagementLine;
	}

	/**
	 * 
	 * @param warehouseId
	 * @return
	 */
	private String getPreOutboundNo (String warehouseId) {
		/*
		 * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE=9  in NUMBERRANGE table and 
		 * fetch NUM_RAN_CURRENT value of FISCALYEAR=CURRENT YEAR and add +1and then update in PREOUTBOUNDHEADER table
		 */
		try {
			AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
			return getNextRangeNumber(9, warehouseId, authTokenForIDMasterService.getAccess_token());
		} catch (Exception e) {
			throw new BadRequestException("Error on Number Range");
		}
	}
	
	
	
	
	/**
	 * 
	 * @param storageSectionIds
	 * @param orderManagementLine
	 * @param warehouseId
	 * @param itemCode
	 * @param ORD_QTY 
	 * @return 
	 */
	private OrderManagementLine createOrderManagement (List<String> storageSectionIds, OrderManagementLine orderManagementLine,
	String warehouseId, String itemCode, Double ORD_QTY) {
		List<Inventory> stockType1InventoryList = inventoryService.getInventoryForOrderManagement (warehouseId, itemCode, 1L, 1L);
		log.info("---Global---stockType1InventoryList-------> : " + stockType1InventoryList);
		if (stockType1InventoryList.isEmpty()) {
			return createEMPTYOrderManagementLine(orderManagementLine);
		}
		return orderManagementLineService.updateAllocation(orderManagementLine, storageSectionIds, ORD_QTY, warehouseId, itemCode, "ORDER_PLACED");
	}
	
	/**
	 * 
	 * @param orderManagementLine
	 * @return
	 */
	private OrderManagementLine createEMPTYOrderManagementLine(OrderManagementLine orderManagementLine) {
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
	 * @param inbound
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws ParseException 
	 */
	public OutboundIntegrationLog createOutboundIntegrationLog (OutboundIntegrationHeader outbound) 
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Warehouse warehouse = getWarehouse(outbound.getWarehouseID());
		OutboundIntegrationLog dbOutboundIntegrationLog = new OutboundIntegrationLog();
		dbOutboundIntegrationLog.setLanguageId("EN");
		dbOutboundIntegrationLog.setCompanyCodeId(warehouse.getCompanyCode());
		dbOutboundIntegrationLog.setPlantId(warehouse.getPlantId());
		dbOutboundIntegrationLog.setWarehouseId(warehouse.getWarehouseId());
		dbOutboundIntegrationLog.setIntegrationLogNumber(outbound.getRefDocumentNo());
		dbOutboundIntegrationLog.setRefDocNumber(outbound.getRefDocumentNo());
		dbOutboundIntegrationLog.setOrderReceiptDate(outbound.getOrderProcessedOn());
		dbOutboundIntegrationLog.setIntegrationStatus("FAILED");
		dbOutboundIntegrationLog.setOrderReceiptDate(outbound.getOrderProcessedOn());
		dbOutboundIntegrationLog.setDeletionIndicator(0L);
		dbOutboundIntegrationLog.setCreatedBy("MSD_API");
		/*
		 * Setting up KuwaitTime
		 */
		Date kwtDate = DateUtils.getCurrentKWTDateTime();
		dbOutboundIntegrationLog.setCreatedOn(kwtDate);
		dbOutboundIntegrationLog = outboundIntegrationLogRepository.save(dbOutboundIntegrationLog);
		log.info("dbOutboundIntegrationLog : " + dbOutboundIntegrationLog);
		return dbOutboundIntegrationLog;
	}
	
//	private OrderManagementLine createOrderManagement (List<String> storageSectionIds, OrderManagementLine orderManagementLine,
//	String warehouseId, String itemCode, Double ORD_QTY) {
//List<Inventory> stBinInventoryList = inventoryService.getInventory(warehouseId, itemCode);
//log.info("---Global---stBinInventoryList-------> : " + stBinInventoryList);
//
///*
// * If the Inventory doesn't exists in the Table then inserting 0th record in Ordermanagementline table
// */
//if (stBinInventoryList.isEmpty()) {
//	return createEMPTYOrderManagementLine(orderManagementLine);
//}
//
///* To obtain the SumOfInvQty */
//List<String> stBins = stBinInventoryList.stream().map(Inventory::getStorageBin).collect(Collectors.toList());
//log.info("---Filtered---stBins -----> : " + stBins);
//
//List<Inventory> finalInventoryList = new ArrayList<>();
//AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
//storageBinPutAway.setStorageBin(stBins);
//storageBinPutAway.setStorageSectionIds(storageSectionIds);
//storageBinPutAway.setWarehouseId(warehouseId);
//
//StorageBin[] storageBin = mastersService.getStorageBin(storageBinPutAway, authTokenForMastersService.getAccess_token());
//log.info("---1----selected----storageBins---from---masters-----> : " + storageBin);
//
//// If the StorageBin returns null, then creating OrderManagementLine table with Zero Alloc_qty and Inv_Qty
//if (storageBin == null) {
//	return createEMPTYOrderManagementLine(orderManagementLine);
//}
//
//if (storageBin != null && storageBin.length > 0) {
//	log.info("----2----selected----storageBins---from---masters-----> : " + Arrays.asList(storageBin));
//	
//	// Pass the filtered ST_BIN/WH_ID/ITM_CODE/BIN_CL_ID=01/STCK_TYP_ID=1 in Inventory table and fetch SUM (INV_QTY)
//	for (StorageBin dbStorageBin : storageBin) {
//		List<Inventory> listInventory = 
//				inventoryService.getInventoryForOrderMgmt (warehouseId, itemCode, 1L, dbStorageBin.getStorageBin(), 1L);
//		log.info("----Selected--Inventory--by--stBin--wise----> : " + listInventory);
//		if (listInventory != null) {
//			finalInventoryList.addAll(listInventory);
//		}
//	}
//}
//log.info("Final inventory list###########---> : " + finalInventoryList);
//
///*
// * If the Inventory doesn't exists in the Table then inserting 0th record in Ordermanamentline table
// */
//if (finalInventoryList.isEmpty()) {
//	return createEMPTYOrderManagementLine(orderManagementLine);
//}
//
//Double tempMaxQty = 0D;
//for (Inventory inventory : finalInventoryList) {
//	if (tempMaxQty < inventory.getInventoryQuantity()) {
//		tempMaxQty = inventory.getInventoryQuantity();
//	}
//}
//
//Inventory maxQtyHoldsInventory = new Inventory();
//for (Inventory inventory : finalInventoryList) {
////	if (inventory.getInventoryQuantity() == tempMaxQty) { // Commenting this ...............
//	if (inventory.getInventoryQuantity() == ORD_QTY) { // This is avoid the If max condition so that always else block will execute
//		BeanUtils.copyProperties(inventory, maxQtyHoldsInventory, CommonUtils.getNullPropertyNames(inventory));
//	}
//}
//log.info("Found ------tempMaxQty-----> : " + tempMaxQty);
//log.info("Found ------tempMaxQty--Inventory---> : " + maxQtyHoldsInventory);
//
///*
// * Sorting the list
// */
//Collections.sort(finalInventoryList, new Comparator<Inventory>() {
//    public int compare(Inventory s1, Inventory s2) {
//        return ((Double)s1.getInventoryQuantity()).compareTo(s2.getInventoryQuantity());
//    }
//});
//
//for (Inventory inventory : finalInventoryList) {
//	log.info("Collections------sort-----> : " + inventory.getInventoryQuantity());
//}
//
//if (maxQtyHoldsInventory.getInventoryQuantity() != null && ORD_QTY < maxQtyHoldsInventory.getInventoryQuantity()) {
//	Long STATUS_ID = 0L;
//	Double ALLOC_QTY = 0D;
//	
//	Double INV_QTY = maxQtyHoldsInventory.getInventoryQuantity();
//	
//	// INV_QTY
//	orderManagementLine.setInventoryQty(INV_QTY);
//	
//	if (ORD_QTY < INV_QTY) {
//		ALLOC_QTY = ORD_QTY;
//	} else if (ORD_QTY > INV_QTY) {
//		ALLOC_QTY = INV_QTY;
//	} else if (INV_QTY == 0) {
//		ALLOC_QTY = 0D;
//	}
//	log.info ("ALLOC_QTY -----@@--->: " + ALLOC_QTY);
//	
//	orderManagementLine.setAllocatedQty(ALLOC_QTY);
//	orderManagementLine.setReAllocatedQty(ALLOC_QTY);
//				
//	// STATUS_ID 
//	/* if ORD_QTY> ALLOC_QTY, then STATUS_ID is hardcoded as "42" */
//	if (ORD_QTY > ALLOC_QTY) {
//		STATUS_ID = 42L;
//	}
//	
//	/* if ORD_QTY=ALLOC_QTY, then STATUS_ID is hardcoded as "43" */
//	if (ORD_QTY == ALLOC_QTY) {
//		STATUS_ID = 43L;
//	}
//	
//	orderManagementLine.setStatusId(STATUS_ID);
//	orderManagementLine.setPickupCreatedBy("MSD_INT");
//	orderManagementLine.setPickupCreatedOn(new Date());
//	orderManagementLine.setProposedStorageBin(maxQtyHoldsInventory.getStorageBin());
//	orderManagementLine.setProposedPackBarCode(maxQtyHoldsInventory.getPackBarcodes());
//	
//	// Ref_Field_9 for storing ST_SEC_ID
//	orderManagementLine.setReferenceField9(maxQtyHoldsInventory.getReferenceField10());
//	
//	// Span ID
//	// Getting StorageBin by WarehouseId
//	StorageBin spanIdStorageBin = mastersService.getStorageBin(orderManagementLine.getProposedStorageBin(), orderManagementLine.getWarehouseId(),
//					authTokenForMastersService.getAccess_token());
//	
//	// Ref_Field_10 for storing SPAN_ID
//	orderManagementLine.setReferenceField10(spanIdStorageBin.getSpanId());
//	
//	OrderManagementLine createdOrderManagementLine = orderManagementLineRepository.save(orderManagementLine);
//	log.info("---1--orderManagementLine created------: " + createdOrderManagementLine);
//	
//	if (orderManagementLine.getAllocatedQty() > 0) {
//		// Update Inventory table
//		Inventory inventoryForUpdate = inventoryService.getInventory(warehouseId, orderManagementLine.getProposedPackBarCode(), 
//				itemCode, orderManagementLine.getProposedStorageBin());
//		log.info("-----inventoryForUpdate------> : " + inventoryForUpdate);
//		if (inventoryForUpdate == null) {
//			throw new BadRequestException("Inventory found as null.");
//		}
//		
//		double dbInventoryQty = 0;
//		double dbInvAllocatedQty = 0;
//		
//		if (inventoryForUpdate.getAllocatedQuantity() != null) {
//			dbInvAllocatedQty = inventoryForUpdate.getAllocatedQuantity();
//		}
//		
//		if (inventoryForUpdate.getInventoryQuantity() != null) {
//			dbInventoryQty = inventoryForUpdate.getInventoryQuantity();
//		}
//		
//		double inventoryQty = dbInventoryQty - orderManagementLine.getAllocatedQty();
//		double allocatedQty = dbInvAllocatedQty + orderManagementLine.getAllocatedQty();
//		inventoryForUpdate.setInventoryQuantity(inventoryQty);
//		inventoryForUpdate.setAllocatedQuantity(allocatedQty);
//		inventoryForUpdate = inventoryRepository.save(inventoryForUpdate);
//		log.info("inventoryForUpdate updated: " + inventoryForUpdate);
//		
//		if (inventoryQty == 0 && allocatedQty == 0) {
//			log.info("inventoryForUpdate inventoryQty became zero." + inventoryQty);
//			inventoryRepository.delete(inventoryForUpdate);
//		}
//	}
//} else { // This is for ORD_QTY > maxQtyHoldsInventory.getInventoryQuantity(), so, creating new ordermanagementline records
//	for (Inventory stBinInventory : finalInventoryList) {
//		OrderManagementLine newOrderManagementLine = new OrderManagementLine();
//		BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine, CommonUtils.getNullPropertyNames(orderManagementLine));
//		
//		Long STATUS_ID = 0L;
//		Double ALLOC_QTY = 0D;
//		
//		log.info("\nfinalListInventory Inventory ---->: " + stBinInventory + "\n");
//		log.info("\nBin-wise Inventory : " + stBinInventory + "\n");
//		
//		/*	
//		 * ALLOC_QTY
//		 * 1. If ORD_QTY< INV_QTY , then ALLOC_QTY = ORD_QTY.
//		 * 2. If ORD_QTY>INV_QTY, then ALLOC_QTY = INV_QTY.
//		 * If INV_QTY = 0, Auto fill ALLOC_QTY=0
//		 */
//		Double INV_QTY = stBinInventory.getInventoryQuantity();
//		
//		// INV_QTY
//		newOrderManagementLine.setInventoryQty(INV_QTY);
//		
//		if (ORD_QTY <= INV_QTY) {
//			ALLOC_QTY = ORD_QTY;
//		} else if (ORD_QTY > INV_QTY) {
//			ALLOC_QTY = INV_QTY;
//		} else if (INV_QTY == 0) {
//			ALLOC_QTY = 0D;
//		}
//		log.info ("ALLOC_QTY -----1--->: " + ALLOC_QTY);
//		
//		newOrderManagementLine.setAllocatedQty(ALLOC_QTY);
//		newOrderManagementLine.setReAllocatedQty(ALLOC_QTY);
//					
//		// STATUS_ID 
//		/* if ORD_QTY> ALLOC_QTY , then STATUS_ID is hardcoded as "42" */
//		if (ORD_QTY > ALLOC_QTY) {
//			STATUS_ID = 42L;
//		}
//		
//		/* if ORD_QTY=ALLOC_QTY,  then STATUS_ID is hardcoded as "43" */
//		if (ORD_QTY == ALLOC_QTY) {
//			STATUS_ID = 43L;
//		}
//		
//		newOrderManagementLine.setStatusId(STATUS_ID);
//		newOrderManagementLine.setPickupCreatedBy("MSD_INT");
//		newOrderManagementLine.setPickupCreatedOn(new Date());
//		if (stBinInventory.getStorageBin() == null) {
//			newOrderManagementLine.setProposedStorageBin(null);
//		} else {
//			newOrderManagementLine.setProposedStorageBin(stBinInventory.getStorageBin());
//		}
//		
//		if (stBinInventory.getPackBarcodes() == null) {
//			newOrderManagementLine.setProposedPackBarCode(null);
//		} else {
//			newOrderManagementLine.setProposedPackBarCode(stBinInventory.getPackBarcodes());
//		}
//		
//		OrderManagementLine createdOrderManagementLine = orderManagementLineRepository.save(newOrderManagementLine);
//		log.info("-----2------orderManagementLine created:-------> " + createdOrderManagementLine);
//		
//		if (ORD_QTY > ALLOC_QTY) {
//			ORD_QTY = ORD_QTY - ALLOC_QTY;
//		}
//		
//		if (createdOrderManagementLine.getAllocatedQty() > 0) {
//			// Update Inventory table
//			Inventory inventoryForUpdate = inventoryService.getInventory(warehouseId, createdOrderManagementLine.getProposedPackBarCode(), 
//					itemCode, createdOrderManagementLine.getProposedStorageBin());
//			
//			double dbInventoryQty = 0;
//			double dbInvAllocatedQty = 0;
//			
//			if (inventoryForUpdate.getInventoryQuantity() != null) {
//				dbInventoryQty = inventoryForUpdate.getInventoryQuantity();
//			}
//			
//			if (inventoryForUpdate.getAllocatedQuantity() != null) {
//				dbInvAllocatedQty = inventoryForUpdate.getAllocatedQuantity();
//			}
//			
//			double inventoryQty = dbInventoryQty - createdOrderManagementLine.getAllocatedQty();
//			double allocatedQty = dbInvAllocatedQty + createdOrderManagementLine.getAllocatedQty();
//			inventoryForUpdate.setInventoryQuantity(inventoryQty);
//			inventoryForUpdate.setAllocatedQuantity(allocatedQty);
//			inventoryForUpdate = inventoryRepository.save(inventoryForUpdate);
//			log.info("inventoryForUpdate updated: " + inventoryForUpdate);
//			
//			if (inventoryQty == 0 && allocatedQty == 0) {
//				log.info("inventoryForUpdate inventoryQty became zero." + inventoryQty);
//				inventoryRepository.delete(inventoryForUpdate);
//			}
//		}
//		
//		if (ORD_QTY == ALLOC_QTY) {
//			log.info("ORD_QTY fully allocated: " + ORD_QTY);
//			break; //If the Inventory satisfied the Ord_qty
//		}
//	}
//}
//
//log.info("orderManagementLine========> : " + orderManagementLine);
//return orderManagementLine;
//}
}
