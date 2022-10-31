package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.tekclover.wms.api.transaction.model.dto.UserManagement;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.transaction.model.inbound.InboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.transaction.model.inbound.UpdateInboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.AddPreInboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.AddPreInboundLine;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationLine;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationLog;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.PreInboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.PreInboundHeaderEntity;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.PreInboundLine;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.PreInboundLineEntity;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.SearchPreInboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.UpdatePreInboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.UpdatePreInboundLine;
import com.tekclover.wms.api.transaction.model.inbound.staging.StagingHeader;
import com.tekclover.wms.api.transaction.repository.InboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.InboundIntegrationLogRepository;
import com.tekclover.wms.api.transaction.repository.InboundLineRepository;
//import com.tekclover.wms.api.transaction.repository.MongoInboundRepository;
import com.tekclover.wms.api.transaction.repository.PreInboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PreInboundLineRepository;
import com.tekclover.wms.api.transaction.repository.StagingHeaderRepository;
import com.tekclover.wms.api.transaction.repository.specification.PreInboundHeaderSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PreInboundHeaderService extends BaseService {
	
	private static String WAREHOUSEID_NUMBERRANGE = "110";

//	@Autowired
//	MongoInboundRepository mongoRepository;
	
	@Autowired
	private PreInboundHeaderRepository preInboundHeaderRepository;
	
	@Autowired
	private PreInboundLineRepository preInboundLineRepository;
	
	@Autowired
	private InboundHeaderRepository inboundHeaderRepository;
	
	@Autowired
	private InboundLineRepository inboundLineRepository;
	
	@Autowired
	private StagingHeaderRepository stagingHeaderRepository;
	
	@Autowired
	private AuthTokenService authTokenService;

	@Autowired
	private IDMasterService idmasterService;
	
	@Autowired
	private MastersService mastersService;
	
	@Autowired
	private PreInboundLineService preInboundLineService;
	
	@Autowired
	private InboundHeaderService inboundHeaderService;
	
	@Autowired
	private InboundLineService inboundLineService;
	
	@Autowired
	InboundIntegrationLogRepository inboundIntegrationLogRepository;
	
	@Autowired
	OrderService orderService;
	
	/**
	 * getPreInboundHeaders
	 * @return
	 */
	public List<PreInboundHeader> getPreInboundHeaders () {
		List<PreInboundHeaderEntity> preInboundHeaderEntityList = preInboundHeaderRepository.findAll();
		preInboundHeaderEntityList = preInboundHeaderEntityList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		
		List<PreInboundHeader> headerList = new ArrayList<PreInboundHeader>();
		for (PreInboundHeaderEntity preInboundHeaderEntity : preInboundHeaderEntityList) {
			List<PreInboundLineEntity> lineEntityList = preInboundLineRepository.findByPreInboundNoAndDeletionIndicator(preInboundHeaderEntity.getPreInboundNo(), 0L);
			
			List<PreInboundLine> preInboundLineList = new ArrayList<PreInboundLine>();
			for (PreInboundLineEntity lineEntity : lineEntityList) {
				PreInboundLine line = new PreInboundLine();
				BeanUtils.copyProperties(lineEntity, line, CommonUtils.getNullPropertyNames(lineEntity));
				preInboundLineList.add(line);
			}
			
			PreInboundHeader header = new PreInboundHeader();
			BeanUtils.copyProperties(preInboundHeaderEntity, header, CommonUtils.getNullPropertyNames(preInboundHeaderEntity));
			header.setPreInboundLine(preInboundLineList);
			headerList.add(header);
		}
		
		return headerList;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumner
	 * @return
	 */
	public PreInboundHeader getPreInboundHeader (String warehouseId, String preInboundNo, String refDocNumner) {
		Optional<PreInboundHeaderEntity> preInboundHeaderEntity = 
				preInboundHeaderRepository.findByWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(warehouseId, preInboundNo, refDocNumner, 0L);
		
		if (preInboundHeaderEntity.isEmpty()) {
			throw new BadRequestException("The given PreInboundHeader ID : preInboundNo : " + preInboundNo +
										", warehouseId: " + warehouseId + " doesn't exist.");
		}
		PreInboundHeader preInboundHeader = getPreInboundLineItems (preInboundHeaderEntity.get());
		return preInboundHeader;
	}
	
	/**
	 * 
	 * @param preInboundNo
	 * @param warehouseId 
	 * @return
	 */
	public PreInboundHeader getPreInboundHeader (String preInboundNo, String warehouseId) {
		Optional<PreInboundHeaderEntity> preInboundHeaderEntity = 
				preInboundHeaderRepository.findByPreInboundNoAndWarehouseIdAndDeletionIndicator(preInboundNo, warehouseId, 0L);
		if (preInboundHeaderEntity.isEmpty()) {
			throw new BadRequestException("The given PreInboundHeader ID : preInboundNo : " + preInboundNo +
										", warehouseId: " + warehouseId + 
										" doesn't exist.");
		} 
		PreInboundHeader preInboundHeader = getPreInboundLineItems (preInboundHeaderEntity.get());
		return preInboundHeader;
	}
	
	/**
	 * 
	 * @param preInboundNo
	 * @param warehouseId
	 * @return
	 */
	public PreInboundHeader getPreInboundHeaderByPreInboundNo (String preInboundNo) {
		Optional<PreInboundHeaderEntity> preInboundHeaderEntity = 
				preInboundHeaderRepository.findByPreInboundNoAndDeletionIndicator(preInboundNo, 0L);
		if (preInboundHeaderEntity.isEmpty()) {
			throw new BadRequestException("The given PreInboundHeader ID : preInboundNo : " + preInboundNo +
										" doesn't exist.");
		} 
		PreInboundHeader preInboundHeader = getPreInboundLineItems (preInboundHeaderEntity.get());
		return preInboundHeader;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @return
	 */
	public List<PreInboundHeader> getPreInboundHeaderWithStatusId (String warehouseId) {
		List<PreInboundHeaderEntity> preInboundHeaderEntity = 
				preInboundHeaderRepository.findByWarehouseIdAndStatusIdAndDeletionIndicator(warehouseId, 24L, 0L);
		if (preInboundHeaderEntity.isEmpty()) {
			throw new BadRequestException("The given PreInboundHeader with StatusID=24 & " +
										" warehouseId: " + warehouseId + 
										" doesn't exist.");
		} 
		List<PreInboundHeader> preInboundHeaderList = new ArrayList<PreInboundHeader>();
		for (PreInboundHeaderEntity dbPreInboundHeaderEntity : preInboundHeaderEntity) {
			PreInboundHeader preInboundHeader = getPreInboundLineItems (dbPreInboundHeaderEntity);
			preInboundHeaderList.add(preInboundHeader);
		}
		
		return preInboundHeaderList;
	}
	
	/**
	 * 
	 * @param warehousId
	 * @return
	 */
	public PreInboundHeader getPreInboundHeaderByWarehouseId (String warehouseId) {
		PreInboundHeaderEntity preInboundHeaderEntity = preInboundHeaderRepository.findByWarehouseId(warehouseId);
		if (preInboundHeaderEntity == null) {
			throw new BadRequestException("The given PreInboundHeader ID : " +
										", warehouseId: " + warehouseId + 
										" doesn't exist.");
		}
		
		if (preInboundHeaderEntity != null && preInboundHeaderEntity.getDeletionIndicator() != null 
				&& preInboundHeaderEntity.getDeletionIndicator() == 0) {
			PreInboundHeader preInboundHeader = getPreInboundLineItems (preInboundHeaderEntity);
			return preInboundHeader;
		}
		return null;
	}
	
	/**
	 * 
	 * @param preInboundHeaderEntity
	 * @return
	 */
	private PreInboundHeader getPreInboundLineItems (PreInboundHeaderEntity preInboundHeaderEntity) {
		PreInboundHeader header = new PreInboundHeader();
		BeanUtils.copyProperties(preInboundHeaderEntity, header, CommonUtils.getNullPropertyNames(preInboundHeaderEntity));
		List<PreInboundLineEntity> lineEntityList = preInboundLineRepository.findByWarehouseIdAndPreInboundNoAndDeletionIndicator(
				preInboundHeaderEntity.getWarehouseId(), preInboundHeaderEntity.getPreInboundNo(), 0L);
		log.info("lineEntityList : " + lineEntityList);
		
		if (!lineEntityList.isEmpty()) {
			List<PreInboundLine> preInboundLineList = new ArrayList<PreInboundLine>();
			for (PreInboundLineEntity lineEntity : lineEntityList) {
				PreInboundLine line = new PreInboundLine();
				BeanUtils.copyProperties(lineEntity, line, CommonUtils.getNullPropertyNames(lineEntity));
				preInboundLineList.add(line);
			}
			
			header.setPreInboundLine(preInboundLineList);
		}
		return header;
	}
	
	/**
	 * 
	 * @param searchPreInboundHeader
	 * @return
	 * @throws ParseException 
	 */
	public List<PreInboundHeaderEntity> findPreInboundHeader(SearchPreInboundHeader searchPreInboundHeader) throws ParseException {
		if (searchPreInboundHeader.getStartCreatedOn() != null && searchPreInboundHeader.getStartCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreInboundHeader.getStartCreatedOn(), searchPreInboundHeader.getEndCreatedOn());
			searchPreInboundHeader.setStartCreatedOn(dates[0]);
			searchPreInboundHeader.setEndCreatedOn(dates[1]);
		}
		
		if (searchPreInboundHeader.getStartRefDocDate() != null && searchPreInboundHeader.getStartRefDocDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreInboundHeader.getStartRefDocDate(), searchPreInboundHeader.getEndRefDocDate());
			searchPreInboundHeader.setStartRefDocDate(dates[0]);
			searchPreInboundHeader.setEndRefDocDate(dates[1]);
		}
		
		PreInboundHeaderSpecification spec = new PreInboundHeaderSpecification(searchPreInboundHeader);
		List<PreInboundHeaderEntity> results = preInboundHeaderRepository.findAll(spec);
//		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createPreInboundHeader
	 * @param newPreInboundHeader
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PreInboundHeaderEntity createPreInboundHeader (AddPreInboundHeader newPreInboundHeader, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		
		PreInboundHeaderEntity dbPreInboundHeader = new PreInboundHeaderEntity();
		BeanUtils.copyProperties(newPreInboundHeader, dbPreInboundHeader, CommonUtils.getNullPropertyNames(newPreInboundHeader));
		
		// Fetch WH_ID value from INTEGRATIONINBOUND table and insert 
		//	1. If WH_ID value is null in INTEGRATIONINBOUND table , insert a Harcoded value""110"""
		AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
		Warehouse dbWarehouse = idmasterService.getWarehouse(newPreInboundHeader.getWarehouseId(), authTokenForIDMasterService.getAccess_token());
		
		dbPreInboundHeader.setWarehouseId(dbWarehouse.getWarehouseId());
		dbPreInboundHeader.setLanguageId(dbWarehouse.getLanguageId());		// LANG_ID
		dbPreInboundHeader.setCompanyCode(dbWarehouse.getCompanyCode());	// C_ID
		dbPreInboundHeader.setPlantId(dbWarehouse.getPlantId());			// PLANT_ID
		dbPreInboundHeader.setDeletionIndicator(0L);
		dbPreInboundHeader.setCreatedBy(loginUserID);
		dbPreInboundHeader.setUpdatedBy(loginUserID);
		dbPreInboundHeader.setCreatedOn(new Date());
		dbPreInboundHeader.setUpdatedOn(new Date());
		return preInboundHeaderRepository.save(dbPreInboundHeader);
	}
	
	/**
	 * updatePreInboundHeader
	 * @param loginUserId 
	 * @param preInboundNo
	 * @param updatePreInboundHeader
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PreInboundHeader updatePreInboundHeader (String preInboundNo, String warehouseId, 
			UpdatePreInboundHeader updatePreInboundHeader, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PreInboundHeader dbPreInboundHeader = getPreInboundHeader(preInboundNo, warehouseId);
		PreInboundHeaderEntity dbEntity = copyBeanToHeaderEntity(dbPreInboundHeader);
		BeanUtils.copyProperties(updatePreInboundHeader, dbEntity, CommonUtils.getNullPropertyNames(updatePreInboundHeader));
		
		if (updatePreInboundHeader.getStatusId() == null) {
			dbEntity.setStatusId(7L); // Hardcoded as 7 during update
		}
		dbEntity.setUpdatedBy(loginUserID);
		dbEntity.setUpdatedOn(new Date());
		dbEntity = preInboundHeaderRepository.save(dbEntity);
		
		List<PreInboundLine> updatedPreInboundLineList = new ArrayList<>();
		for (PreInboundLine lineItem : updatePreInboundHeader.getPreInboundLine()) {
			// Get Lines items from DB
			UpdatePreInboundLine updatePreInboundLine = new UpdatePreInboundLine();
			BeanUtils.copyProperties(lineItem, updatePreInboundLine, CommonUtils.getNullPropertyNames(lineItem));
			
			// CONT_NO value entered in PREINBOUNDHEADER
			updatePreInboundLine.setContainerNo(dbEntity.getContainerNo());
			if (updatePreInboundHeader.getStatusId() == null) {
				updatePreInboundLine.setStatusId(7L); // Hardcoded as 7 during update
			}
			
			PreInboundLineEntity updatedLineEntity = preInboundLineService.updatePreInboundLine (preInboundNo, warehouseId, 
					lineItem.getRefDocNumber(), lineItem.getLineNo(), lineItem.getItemCode(), 
					updatePreInboundLine, loginUserID);
			lineItem = copyLineEntityToBean(updatedLineEntity);
			updatedPreInboundLineList.add(lineItem);
		}
		
		dbPreInboundHeader = copyHeaderEntityToBean(dbEntity);
		dbPreInboundHeader.setPreInboundLine(updatedPreInboundLineList);
		return dbPreInboundHeader;
	}
	
	/**
	 * 
	 * @param preInboundNo
	 * @param warehousId
	 * @param statusId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PreInboundHeader updatePreInboundHeader (String preInboundNo, String warehousId, String refDocNumner, Long statusId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PreInboundHeader dbPreInboundHeader = getPreInboundHeader(warehousId, preInboundNo, refDocNumner);
		PreInboundHeaderEntity dbEntity = copyBeanToHeaderEntity(dbPreInboundHeader);
		dbEntity.setStatusId(statusId);	
		dbEntity.setUpdatedBy(loginUserID);
		dbEntity.setUpdatedOn(new Date());
		dbEntity = preInboundHeaderRepository.save(dbEntity);
		dbPreInboundHeader = copyHeaderEntityToBean(dbEntity);
		return dbPreInboundHeader;
	}
	
	/**
	 * deletePreInboundHeader
	 * @param loginUserID 
	 * @param preInboundNo
	 */
	public void deletePreInboundHeader (String preInboundNo, String warehousId, String loginUserID) {
		PreInboundHeader preInboundHeader = getPreInboundHeader(preInboundNo, warehousId);
		PreInboundHeaderEntity dbEntity = copyBeanToHeaderEntity(preInboundHeader);
		if ( dbEntity != null) {
			dbEntity.setDeletionIndicator(1L);
			dbEntity.setUpdatedBy(loginUserID);
			preInboundHeaderRepository.save(dbEntity);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + preInboundNo);
		}
	}

	/**
	 * 
	 * @param refDocNumber 
	 * @param inboundIntegrationHeader 
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@Transactional
	public InboundHeader processInboundReceived (String refDocNumber, InboundIntegrationHeader inboundIntegrationHeader) 
			throws IllegalAccessException, InvocationTargetException, BadRequestException, Exception {
		/*
		 * Checking whether received refDocNumber processed already.
		 */
		Optional<PreInboundHeaderEntity> orderProcessedStatus = preInboundHeaderRepository.findByRefDocNumberAndDeletionIndicator(refDocNumber, 0);
		if (!orderProcessedStatus.isEmpty()) {
			orderService.updateProcessedInboundOrder(refDocNumber);
			throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
		}
		
		String warehouseId = inboundIntegrationHeader.getWarehouseID();
		log.info("warehouseId : " + warehouseId);
		
		// Fetch ITM_CODE inserted in INBOUNDINTEGRATION table and pass the ITM_CODE in IMBASICDATA1 table and 
		// validate the ITM_CODE result is Not Null
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		log.info("authTokenForMastersService : " + authTokenForMastersService);
		AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
		
		Warehouse warehouse = idmasterService.getWarehouse(warehouseId, authTokenForIDMasterService.getAccess_token());
		log.info("warehouse : " + warehouse);
		if (warehouse == null) {
			log.info("warehouse not found.");
			throw new BadRequestException("Warehouse cannot be null.");
		}
		
		// Getting PreInboundNo from NumberRangeTable
		String preInboundNo = getPreInboundNo(warehouseId);
		
		List<PreInboundLineEntity> overallCreatedPreInboundLineList = new ArrayList<>();
		for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
			log.info("inboundIntegrationLine : " + inboundIntegrationLine);
			ImBasicData1 imBasicData1 = 
					mastersService.getImBasicData1ByItemCode(inboundIntegrationLine.getItemCode(), warehouseId,
							authTokenForMastersService.getAccess_token());
			log.info("imBasicData1 exists: " + imBasicData1);
			
			// If ITM_CODE value is Null, then insert a record in IMBASICDATA1 table as below
			if (imBasicData1 == null) {
				imBasicData1 = new ImBasicData1(); 											
				imBasicData1.setLanguageId("EN");											// LANG_ID
				imBasicData1.setWarehouseId(warehouseId);									// WH_ID
				imBasicData1.setCompanyCodeId(warehouse.getCompanyCode());					// C_ID
				imBasicData1.setPlantId(warehouse.getPlantId());							// PLANT_ID
				imBasicData1.setItemCode(inboundIntegrationLine.getItemCode());				// ITM_CODE
				imBasicData1.setUomId(inboundIntegrationLine.getUom()); 					// UOM_ID
				imBasicData1.setDescription(inboundIntegrationLine.getItemText());			// ITEM_TEXT
				imBasicData1.setManufacturerPartNo(inboundIntegrationLine.getManufacturerPartNo()); 		// MFR_PART
				imBasicData1.setStatusId(1L); 												// STATUS_ID
				ImBasicData1 createdImBasicData1 = 
						mastersService.createImBasicData1(imBasicData1, "MSD_INT", authTokenForMastersService.getAccess_token());
				log.info("ImBasicData1 created: " + createdImBasicData1);	
			}
		
			/*-------------Insertion of BOM item in PREINBOUNDLINE table---------------------------------------------------------*/
			/*
			 * Before inserting the record into Preinbound table, fetch ITM_CODE from InboundIntegrationHeader (MONGO) table and 
			 * pass into BOMHEADER table as PAR_ITM_CODE and validate record is Not Null
			 */
			BomHeader bomHeader = mastersService.getBomHeader(inboundIntegrationLine.getItemCode(), warehouseId, authTokenForMastersService.getAccess_token());
			log.info("bomHeader [BOM] : " + bomHeader);
			if (bomHeader != null) {
				BomLine[] bomLine = mastersService.getBomLine(bomHeader.getBomNumber(), bomHeader.getWarehouseId(), 
						authTokenForMastersService.getAccess_token());
				List<PreInboundLineEntity> toBeCreatedPreInboundLineList = new ArrayList<>();
				for (BomLine dbBomLine : bomLine) {
					PreInboundLineEntity preInboundLineEntity = createPreInboundLineBOMBased (warehouse.getCompanyCode(), 
							warehouse.getPlantId(), preInboundNo , inboundIntegrationHeader, dbBomLine, inboundIntegrationLine);
					log.info("preInboundLineEntity [BOM] : " + preInboundLineEntity);
					toBeCreatedPreInboundLineList.add(preInboundLineEntity);
				}
				
				// Batch Insert - PreInboundLines
				if (!toBeCreatedPreInboundLineList.isEmpty()) {
					List<PreInboundLineEntity> createdPreInboundLine = preInboundLineRepository.saveAll(toBeCreatedPreInboundLineList);
					log.info("createdPreInboundLine [BOM] : " + createdPreInboundLine);
					overallCreatedPreInboundLineList.addAll(createdPreInboundLine);
				}
			}
		}
		
		/*
		 * Append PREINBOUNDLINE table through below logic
		 */
		List<PreInboundLineEntity> toBeCreatedPreInboundLineList = new ArrayList<>();
		for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
			toBeCreatedPreInboundLineList.add(createPreInboundLine (warehouse.getCompanyCode(), 
					warehouse.getPlantId(), preInboundNo, inboundIntegrationHeader, inboundIntegrationLine));
		}
		
		log.info("toBeCreatedPreInboundLineList [API] : " + toBeCreatedPreInboundLineList);
		
		// Batch Insert - PreInboundLines
		if (!toBeCreatedPreInboundLineList.isEmpty()) {
			List<PreInboundLineEntity> createdPreInboundLine = preInboundLineRepository.saveAll(toBeCreatedPreInboundLineList);
			log.info("createdPreInboundLine [API] : " + createdPreInboundLine);
			overallCreatedPreInboundLineList.addAll(createdPreInboundLine);
		}
		
		/*------------------Insert into PreInboundHeader table-----------------------------*/
		PreInboundHeaderEntity createdPreInboundHeader = createPreInboundHeader (warehouse.getCompanyCode(), warehouse.getPlantId(), preInboundNo, 
				inboundIntegrationHeader);
		log.info("preInboundHeader Created : " + createdPreInboundHeader);
		
		/*------------------Insert into Inbound Header And Line----------------------------*/
		InboundHeader createdInboundHeader = createInbounHeaderAndLine (createdPreInboundHeader, overallCreatedPreInboundLineList);
		
		// Inserting into InboundLog Table.
		InboundIntegrationLog createdInboundIntegrationLog = createInboundIntegrationLog (createdPreInboundHeader);
		
//		if (createdInboundIntegrationLog != null) {
//			inboundIntegrationHeader.setProcessedStatusId(1L);
//			inboundIntegrationHeader.setOrderProcessedOn(new Date());
//			mongoRepository.save(inboundIntegrationHeader);
//		}
		
		return createdInboundHeader;
	}
	
	/**
	 * 
	 * @param companyCode
	 * @param plantID
	 * @param preInboundNo
	 * @param inboundIntegrationHeader
	 * @param bomHeader
	 * @param bomLine
	 * @param inboundIntegrationLine
	 * @return
	 * @throws ParseException 
	 */
	private PreInboundLineEntity createPreInboundLineBOMBased(String companyCode, String plantID, String preInboundNo, 
			InboundIntegrationHeader inboundIntegrationHeader, BomLine bomLine, InboundIntegrationLine inboundIntegrationLine) throws ParseException {
		PreInboundLineEntity preInboundLine = new PreInboundLineEntity();
		Warehouse warehouse = getWarehouse(inboundIntegrationHeader.getWarehouseID());
		
		preInboundLine.setLanguageId(warehouse.getLanguageId());
		preInboundLine.setCompanyCode(companyCode);
		preInboundLine.setPlantId(plantID);
		preInboundLine.setWarehouseId(inboundIntegrationHeader.getWarehouseID());
		preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
		preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());
		
		// PRE_IB_NO
		preInboundLine.setPreInboundNo(preInboundNo);
		
		// IB__LINE_NO
		preInboundLine.setLineNo(Long.valueOf(inboundIntegrationLine.getLineReference()));
		
		// ITM_CODE
		preInboundLine.setItemCode(bomLine.getChildItemCode());
		
		// ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		ImBasicData1 imBasicData1 = 
				mastersService.getImBasicData1ByItemCode(inboundIntegrationLine.getItemCode(), 
						inboundIntegrationHeader.getWarehouseID(),
						authTokenForMastersService.getAccess_token());
		preInboundLine.setItemDescription(imBasicData1.getDescription());
		
		// MFR
		preInboundLine.setManufacturerPartNo(imBasicData1.getManufacturerPartNo());
		
		// ORD_QTY
		double orderQuantity = inboundIntegrationLine.getOrderedQty() * bomLine.getChildItemQuantity();
		preInboundLine.setOrderQty(orderQuantity);
		
		// ORD_UOM
		preInboundLine.setOrderUom(inboundIntegrationLine.getUom());
		
		// EA_DATE
		preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());
		
		// STCK_TYP_ID
		preInboundLine.setStockTypeId(1L);
		
		// SP_ST_IND_ID
		preInboundLine.setSpecialStockIndicatorId(1L);
		
		// STATUS_ID
		preInboundLine.setStatusId(6L);
		
		// REF_FIELD_1
		preInboundLine.setReferenceField1("CHILD ITEM");
		
		// REF_FIELD_2
		preInboundLine.setReferenceField2("BOM ITEM");
		
		// REF_FIELD_4
		preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());
		
		preInboundLine.setDeletionIndicator(0L);
		preInboundLine.setCreatedBy("MSD_INT");
		preInboundLine.setCreatedOn(new Date());	
		return preInboundLine;
	}
	
	/**
	 * PreInboundLine Insert
	 * @param companyCode
	 * @param plantID
	 * @param preInboundNo
	 * @param inboundIntegrationHeader
	 * @return
	 * @throws ParseException 
	 */
	private PreInboundLineEntity createPreInboundLine (String companyCode, String plantID, String preInboundNo, 
			InboundIntegrationHeader inboundIntegrationHeader, InboundIntegrationLine inboundIntegrationLine) throws ParseException {
		PreInboundLineEntity preInboundLine = new PreInboundLineEntity();
		Warehouse warehouse = getWarehouse(inboundIntegrationHeader.getWarehouseID());
		
		preInboundLine.setLanguageId(warehouse.getLanguageId());
		preInboundLine.setCompanyCode(companyCode);
		preInboundLine.setPlantId(plantID);
		preInboundLine.setWarehouseId(inboundIntegrationHeader.getWarehouseID());
		preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
		preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());
		
		// PRE_IB_NO
		preInboundLine.setPreInboundNo(preInboundNo);
		
		// IB__LINE_NO
		preInboundLine.setLineNo(Long.valueOf(inboundIntegrationLine.getLineReference()));
		
		// ITM_CODE
		preInboundLine.setItemCode(inboundIntegrationLine.getItemCode());
		
		// ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		ImBasicData1 imBasicData1 = 
				mastersService.getImBasicData1ByItemCode(inboundIntegrationLine.getItemCode(), 
						inboundIntegrationHeader.getWarehouseID(),
						authTokenForMastersService.getAccess_token());
		preInboundLine.setItemDescription(imBasicData1.getDescription());
		
		// MFR_PART
		preInboundLine.setManufacturerPartNo(inboundIntegrationLine.getManufacturerPartNo());
		
		// PARTNER_CODE
		preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());
		
		// ORD_QTY
		preInboundLine.setOrderQty(inboundIntegrationLine.getOrderedQty());
		
		// ORD_UOM
		preInboundLine.setOrderUom(inboundIntegrationLine.getUom());
		
		// STCK_TYP_ID
		preInboundLine.setStockTypeId(1L);
		
		// SP_ST_IND_ID
		preInboundLine.setSpecialStockIndicatorId(1L);
		
		// EA_DATE 
		log.info("inboundIntegrationLine.getExpectedDate() : " + inboundIntegrationLine.getExpectedDate());
		preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());
		
		// ITM_CASE_QTY
		preInboundLine.setItemCaseQty(inboundIntegrationLine.getItemCaseQty());
		
		// REF_FIELD_4
		preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());
		
		// STATUS_ID
		preInboundLine.setStatusId(6L);
		preInboundLine.setDeletionIndicator(0L);
		preInboundLine.setCreatedBy("MSD_INT");
		preInboundLine.setCreatedOn(new Date());	
		
		log.info("preInboundLine : " + preInboundLine);
		return preInboundLine;
	}
	
	/**
	 * 
	 * @param inboundIntegration
	 * @param warehouse
	 * @return
	 */
	private PreInboundHeaderEntity createPreInboundHeader (String companyCode, String plantID, String preInboundNo, 
			InboundIntegrationHeader inboundIntegrationHeader) {
		PreInboundHeaderEntity preInboundHeader = new PreInboundHeaderEntity();
		Warehouse warehouse = getWarehouse(inboundIntegrationHeader.getWarehouseID());
		
		preInboundHeader.setLanguageId(warehouse.getLanguageId());									// LANG_ID
		preInboundHeader.setWarehouseId(inboundIntegrationHeader.getWarehouseID());
		preInboundHeader.setCompanyCode(companyCode);
		preInboundHeader.setPlantId(plantID);
		preInboundHeader.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
		preInboundHeader.setPreInboundNo(preInboundNo);												// PRE_IB_NO
		preInboundHeader.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());	// REF_DOC_TYP - Hard Coded Value "ASN"
		preInboundHeader.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId()); 	// IB_ORD_TYP_ID
		preInboundHeader.setRefDocDate(inboundIntegrationHeader.getOrderReceivedOn());				// REF_DOC_DATE
		preInboundHeader.setStatusId(6L);
		preInboundHeader.setDeletionIndicator(0L);
		preInboundHeader.setCreatedBy("MSD_INT");
		preInboundHeader.setCreatedOn(new Date());	
		PreInboundHeaderEntity createdPreInboundHeader = preInboundHeaderRepository.save(preInboundHeader);
		log.info("createdPreInboundHeader : " + createdPreInboundHeader);
		return createdPreInboundHeader;
	}
	
	/**
	 * 
	 * @param preInboundHeader
	 * @param preInboundLine
	 * @return 
	 */
	private InboundHeader createInbounHeaderAndLine(PreInboundHeaderEntity preInboundHeader, List<PreInboundLineEntity> preInboundLine) {
		InboundHeader inboundHeader = new InboundHeader();
		BeanUtils.copyProperties(preInboundHeader, inboundHeader, CommonUtils.getNullPropertyNames(preInboundHeader));

		// Status ID
		inboundHeader.setStatusId(6L);
		inboundHeader.setDeletionIndicator(0L);
		inboundHeader.setCreatedBy(preInboundHeader.getCreatedBy());
		inboundHeader.setCreatedOn(preInboundHeader.getCreatedOn());
		InboundHeader createdInboundHeader = inboundHeaderRepository.save(inboundHeader);
		log.info("createdInboundHeader : " + createdInboundHeader);
		
		/*
		 * Inbound Line Table Insert
		 */
		List<InboundLine> toBeCreatedInboundLineList = new ArrayList<>();
		for (PreInboundLineEntity createdPreInboundLine : preInboundLine) {
			InboundLine inboundLine = new InboundLine();
			BeanUtils.copyProperties(createdPreInboundLine, inboundLine, CommonUtils.getNullPropertyNames(createdPreInboundLine));
			
			inboundLine.setOrderedQuantity(createdPreInboundLine.getOrderQty());
			inboundLine.setOrderedUnitOfMeasure(createdPreInboundLine.getOrderUom());
			inboundLine.setDescription(createdPreInboundLine.getItemDescription());
			inboundLine.setVendorCode(createdPreInboundLine.getBusinessPartnerCode());
			inboundLine.setReferenceField4(createdPreInboundLine.getReferenceField4());
			inboundLine.setDeletionIndicator(0L);
			inboundLine.setCreatedBy(preInboundHeader.getCreatedBy());
			inboundLine.setCreatedOn(preInboundHeader.getCreatedOn());
			toBeCreatedInboundLineList.add(inboundLine);
		}
		
		List<InboundLine> createdInboundLine = inboundLineRepository.saveAll(toBeCreatedInboundLineList);
		log.info("createdInboundLine : " + createdInboundLine);
		
		return createdInboundHeader;
	}

	/**
	 * 
	 * @param inputPreInboundLines
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StagingHeader processASN(List<AddPreInboundLine> inputPreInboundLines, String loginUserID) 
				throws IllegalAccessException, InvocationTargetException {
		boolean isPreInboundHeaderUpdated = false;
		boolean isPreInboundLineUpdated = false;
		
		// Update PREINBOUNDHEADER and PREINBOUNDLINE table with STATUS_ID = 05 and update the other fields from UI
		// PREINBOUNDLINE Update
		String preInboundNo = null;
		String containerNo = null;
		String warehouseId = null;
		for (AddPreInboundLine preInboundLine : inputPreInboundLines) {
			PreInboundLineEntity objUpdatePreInboundLine = new PreInboundLineEntity();
			BeanUtils.copyProperties(preInboundLine, objUpdatePreInboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
			objUpdatePreInboundLine.setCompanyCode(getCompanyCode());
			objUpdatePreInboundLine.setStatusId(5L);
			objUpdatePreInboundLine.setUpdatedBy(loginUserID);
			objUpdatePreInboundLine.setUpdatedOn(new Date());
			PreInboundLineEntity updatedPreInboundLine = preInboundLineRepository.save(objUpdatePreInboundLine);
			log.info("preInboundLine updated: " + updatedPreInboundLine);
			if (updatedPreInboundLine != null) {
				isPreInboundLineUpdated = true;
				preInboundNo = updatedPreInboundLine.getPreInboundNo();
				containerNo = updatedPreInboundLine.getContainerNo();
				warehouseId = updatedPreInboundLine.getWarehouseId();
			}
		}
				
		// PREINBOUNDHEADER Update
//		PreInboundHeader preInboundHeader = getPreInboundHeaderByPreInboundNo (preInboundNo);
		PreInboundHeader preInboundHeader = getPreInboundHeader (preInboundNo, warehouseId);
		log.info("preInboundHeader---found-------> : " + preInboundHeader);
		
		PreInboundHeaderEntity preInboundHeaderEntity = copyBeanToHeaderEntity(preInboundHeader);
		preInboundHeaderEntity.setContainerNo(containerNo);
		preInboundHeaderEntity.setStatusId(5L);
		PreInboundHeaderEntity updatedPreInboundHeaderEntity = preInboundHeaderRepository.save(preInboundHeaderEntity);
		log.info("updatedPreInboundHeaderEntity---@------> : " + updatedPreInboundHeaderEntity);
		if (updatedPreInboundHeaderEntity != null) {
			isPreInboundHeaderUpdated = true;
		}
		
		// Update INBOUNDHEADER and INBOUNDLINE table
		if (isPreInboundHeaderUpdated && isPreInboundLineUpdated) {
			UpdateInboundHeader updateInboundHeader = new UpdateInboundHeader();
			updateInboundHeader.setContainerNo(containerNo);
			updateInboundHeader.setStatusId(5L);
			
			// warehouseId, refDocNumber, preInboundNo, loginUserID, updateInboundHeader
			InboundHeader updatedInboundHeader = 
					inboundHeaderService.updateInboundHeader(preInboundHeader.getWarehouseId(), preInboundHeader.getRefDocNumber(), 
							preInboundNo, loginUserID, updateInboundHeader);
			log.info("updatedInboundHeader : " + updatedInboundHeader);
		
			// INBOUNDLINE table update
			for (AddPreInboundLine dbPreInboundLine : inputPreInboundLines) {
				InboundLine inboundLine = inboundLineService.getInboundLine(preInboundHeader.getWarehouseId(), preInboundHeader.getRefDocNumber(), 
						preInboundNo, dbPreInboundLine.getLineNo(), dbPreInboundLine.getItemCode());
				inboundLine.setContainerNo(dbPreInboundLine.getContainerNo());
				inboundLine.setInvoiceNo(dbPreInboundLine.getInvoiceNo());
				inboundLine.setStatusId(5L);
				
				// warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, loginUserID, updateInboundLine
				InboundLine updatedInboundLine = inboundLineRepository.save(inboundLine);
				log.info("updatedInboundLine updated: " + updatedInboundLine);
			}
		}
		
		StagingHeader stagingHeader = new StagingHeader();
		BeanUtils.copyProperties(preInboundHeader, stagingHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
		stagingHeader.setCompanyCode(getCompanyCode());
		
		// STG_NO
		AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
		UserManagement userManagement = getUserManagement(loginUserID, preInboundHeader.getWarehouseId());
		
		long NUMBER_RANGE_CODE = 3;
		WAREHOUSEID_NUMBERRANGE = userManagement.getWarehouseId();
		String nextRangeNumber = getNextRangeNumber(NUMBER_RANGE_CODE, WAREHOUSEID_NUMBERRANGE, authTokenForIDMasterService.getAccess_token());
		stagingHeader.setStagingNo(nextRangeNumber);
		
		// GR_MTD
		stagingHeader.setGrMtd("INTEGRATION");
		
		// STATUS_ID
		stagingHeader.setStatusId(12L);
		stagingHeader.setCreatedBy(preInboundHeader.getCreatedBy());
		stagingHeader.setCreatedOn(preInboundHeader.getCreatedOn());
		return stagingHeaderRepository.save(stagingHeader);
	}
	
	/**
	 * 
	 * @param refDocNumber
	 */
	public void updateASN (String refDocNumber) {
		List<PreInboundHeader> preInboundHeaders = getPreInboundHeaders();
		List<PreInboundHeaderEntity> preInboundHeadersEntityList = createEntityList (preInboundHeaders);
		preInboundHeaders.forEach(preibheaders -> preibheaders.setReferenceField1(refDocNumber));
		preInboundHeaderRepository.saveAll(preInboundHeadersEntityList);
	}
	
	/**
	 * 
	 * @return
	 */
	private String getPreInboundNo (String warehouseId) {
		/*
		 * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE = 2 values in NUMBERRANGE table and 
		 * fetch NUM_RAN_CURRENT value of FISCALYEAR = CURRENT YEAR and add +1and then 
		 * update in PREINBOUNDHEADER table
		 */
		try {
			AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
			String nextRangeNumber = getNextRangeNumber(2, warehouseId, authTokenForIDMasterService.getAccess_token());
			return nextRangeNumber;
		} catch (Exception e) {
			throw new BadRequestException("Error on Number generation." + e.toString());
		}
	}
	
	/**
	 * 
	 * @param createdPreInboundHeader
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public InboundIntegrationLog createInboundIntegrationLog (PreInboundHeaderEntity createdPreInboundHeader) 
			throws IllegalAccessException, InvocationTargetException {
		InboundIntegrationLog dbInboundIntegrationLog = new InboundIntegrationLog();
		dbInboundIntegrationLog.setLanguageId("EN");
		dbInboundIntegrationLog.setCompanyCodeId(createdPreInboundHeader.getCompanyCode());
		dbInboundIntegrationLog.setPlantId(createdPreInboundHeader.getPlantId());
		dbInboundIntegrationLog.setWarehouseId(createdPreInboundHeader.getWarehouseId());
		dbInboundIntegrationLog.setIntegrationLogNumber(createdPreInboundHeader.getPreInboundNo());
		dbInboundIntegrationLog.setRefDocNumber(createdPreInboundHeader.getRefDocNumber());
		dbInboundIntegrationLog.setOrderReceiptDate(createdPreInboundHeader.getCreatedOn());
		dbInboundIntegrationLog.setIntegrationStatus("SUCCESS");
		dbInboundIntegrationLog.setOrderReceiptDate(createdPreInboundHeader.getCreatedOn());
		dbInboundIntegrationLog.setDeletionIndicator(0L);
		dbInboundIntegrationLog.setCreatedBy(createdPreInboundHeader.getCreatedBy());
		dbInboundIntegrationLog.setCreatedOn(new Date());
		dbInboundIntegrationLog = inboundIntegrationLogRepository.save(dbInboundIntegrationLog);
		log.info("dbInboundIntegrationLog : " + dbInboundIntegrationLog);
		return dbInboundIntegrationLog;
	}
	
	/**
	 * 
	 * @param inbound
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public InboundIntegrationLog createInboundIntegrationLog (InboundIntegrationHeader inbound) 
			throws IllegalAccessException, InvocationTargetException {
		Warehouse warehouse = getWarehouse(inbound.getWarehouseID());
		if (warehouse == null) {
			throw new BadRequestException("Warehouse not found : " + inbound.getWarehouseID());
		}
		InboundIntegrationLog dbInboundIntegrationLog = new InboundIntegrationLog();
		dbInboundIntegrationLog.setLanguageId("EN");
		dbInboundIntegrationLog.setCompanyCodeId(warehouse.getCompanyCode());
		dbInboundIntegrationLog.setPlantId(warehouse.getPlantId());
		dbInboundIntegrationLog.setWarehouseId(warehouse.getWarehouseId());
		dbInboundIntegrationLog.setIntegrationLogNumber(inbound.getRefDocumentNo());
		dbInboundIntegrationLog.setRefDocNumber(inbound.getRefDocumentNo());
		dbInboundIntegrationLog.setOrderReceiptDate(inbound.getOrderProcessedOn());
		dbInboundIntegrationLog.setIntegrationStatus("FAILED");
		dbInboundIntegrationLog.setOrderReceiptDate(inbound.getOrderProcessedOn());
		dbInboundIntegrationLog.setDeletionIndicator(0L);
		dbInboundIntegrationLog.setCreatedBy("MSD_API");
		dbInboundIntegrationLog.setCreatedOn(new Date());
		dbInboundIntegrationLog = inboundIntegrationLogRepository.save(dbInboundIntegrationLog);
		log.info("dbInboundIntegrationLog : " + dbInboundIntegrationLog);
		return dbInboundIntegrationLog;
	}
	
	/**
	 * 
	 * @param preInboundHeaderList
	 * @return
	 */
	private List<PreInboundHeader> createBeanList(List<PreInboundHeaderEntity> preInboundHeaderList) {
		List<PreInboundHeader> listPreInboundHeader = new ArrayList<>();
		for (PreInboundHeaderEntity preInboundHeaderEntity : preInboundHeaderList) {
			PreInboundHeader preInboundHeader = copyHeaderEntityToBean(preInboundHeaderEntity);
			listPreInboundHeader.add(preInboundHeader);
		}
		return listPreInboundHeader;
	}
	
	private List<PreInboundLine> createBeanListAsLine(List<PreInboundLineEntity> preInboundLineEntityList) {
		List<PreInboundLine> listPreInboundLine = new ArrayList<>();
		for (PreInboundLineEntity preInboundLineEntity : preInboundLineEntityList) {
			PreInboundLine preInboundLine = copyLineEntityToBean(preInboundLineEntity);
			listPreInboundLine.add(preInboundLine);
		}
		return listPreInboundLine;
	}
	
	private List<PreInboundHeaderEntity> createEntityList(List<PreInboundHeader> preInboundHeaderList) {
		List<PreInboundHeaderEntity> listPreInboundHeaderEntity = new ArrayList<>();
		for (PreInboundHeader preInboundHeader : preInboundHeaderList) {
			PreInboundHeaderEntity newPreInboundHeaderEntity = copyBeanToHeaderEntity(preInboundHeader);
			listPreInboundHeaderEntity.add(newPreInboundHeaderEntity);
		}
		return listPreInboundHeaderEntity;
	}
	
	private PreInboundHeader copyHeaderEntityToBean(PreInboundHeaderEntity preInboundHeaderEntity) {
		PreInboundHeader preInboundHeader = new PreInboundHeader();
		BeanUtils.copyProperties(preInboundHeaderEntity, preInboundHeader, CommonUtils.getNullPropertyNames(preInboundHeaderEntity));
		return preInboundHeader;
	}
	
	private PreInboundLine copyLineEntityToBean(PreInboundLineEntity preInboundLineEntity) {
		PreInboundLine preInboundLine = new PreInboundLine();
		BeanUtils.copyProperties(preInboundLineEntity, preInboundLine, CommonUtils.getNullPropertyNames(preInboundLineEntity));
		return preInboundLine;
	}
	
	private PreInboundHeaderEntity copyBeanToHeaderEntity(PreInboundHeader preInboundHeader) {
		PreInboundHeaderEntity preInboundHeaderEntity = new PreInboundHeaderEntity();
		BeanUtils.copyProperties(preInboundHeader, preInboundHeaderEntity, CommonUtils.getNullPropertyNames(preInboundHeader));
		return preInboundHeaderEntity;
	}
	
	private PreInboundLineEntity copyBeanToLineEntity(PreInboundLine preInboundLine) {
		PreInboundLineEntity preInboundLineEntity = new PreInboundLineEntity();
		BeanUtils.copyProperties(preInboundLine, preInboundLineEntity, CommonUtils.getNullPropertyNames(preInboundLine));
		return preInboundLineEntity;
	}
}
