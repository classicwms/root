package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AXAuthToken;
import com.tekclover.wms.api.transaction.model.inbound.AddInboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.InboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.InboundHeaderEntity;
import com.tekclover.wms.api.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.transaction.model.inbound.InboundLineEntity;
import com.tekclover.wms.api.transaction.model.inbound.SearchInboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.UpdateInboundHeader;
import com.tekclover.wms.api.transaction.model.integration.IntegrationApiResponse;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.ASN;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.ASNHeader;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.ASNLine;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.AXApiResponse;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.InterWarehouseTransfer;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.InterWarehouseTransferHeader;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.InterWarehouseTransferLine;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.SOReturn;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.SOReturnHeader;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.SOReturnLine;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.StoreReturn;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.StoreReturnHeader;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.StoreReturnLine;
import com.tekclover.wms.api.transaction.repository.GrHeaderRepository;
import com.tekclover.wms.api.transaction.repository.InboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.InboundLineRepository;
import com.tekclover.wms.api.transaction.repository.IntegrationApiResponseRepository;
import com.tekclover.wms.api.transaction.repository.PreInboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PreInboundLineRepository;
import com.tekclover.wms.api.transaction.repository.StagingHeaderRepository;
import com.tekclover.wms.api.transaction.repository.specification.InboundHeaderSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InboundHeaderService extends BaseService {
	
	@Autowired
	private InboundHeaderRepository inboundHeaderRepository;
	
	@Autowired
	private InboundLineRepository inboundLineRepository;
	
	@Autowired
	private PreInboundHeaderRepository preInboundHeaderRepository;
	
	@Autowired
	private PreInboundLineRepository preInboundLineRepository;
	
	@Autowired
	private PreInboundHeaderService preInboundHeaderService;
	
	@Autowired
	private PreInboundLineService preInboundLineService;
	
	@Autowired
	private StagingHeaderService stagingHeaderService;
	
	@Autowired
	private StagingLineService stagingLineService;
	
	@Autowired
	private StagingHeaderRepository stagingHeaderRepository;
	
	@Autowired
	private GrHeaderService grHeaderService;
	
	@Autowired
	private GrHeaderRepository grHeaderRepository;
	
	@Autowired
	private GrLineService grLineService;
	
	@Autowired
	private PutAwayHeaderService putAwayHeaderService;
	
	@Autowired
	private PutAwayLineService putAwayLineService;
	
	@Autowired
	private InboundLineService inboundLineService;
	
	@Autowired
	private AuthTokenService authTokenService;
	
	@Autowired
	private WarehouseService warehouseService;
	
	@Autowired
	PropertiesConfig propertiesConfig;
	
	@Autowired
	private IntegrationApiResponseRepository integrationApiResponseRepository;
	
	/**
	 * getInboundHeaders
	 * @return
	 */
	public List<InboundHeader> getInboundHeaders () {
		List<InboundHeader> containerReceiptList =  inboundHeaderRepository.findAll();
		containerReceiptList = containerReceiptList
				.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return containerReceiptList;
	}
	
	/**
	 * getInboundHeader
	 * @param refDocNumber
	 * @return
	 */
	public InboundHeaderEntity getInboundHeader (String warehouseId, String refDocNumber, String preInboundNo) {
		Optional<InboundHeader> optInboundHeader = 
				inboundHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
						getLanguageId(),
						getCompanyCode(),
						getPlantId(),
						warehouseId,
						refDocNumber,
						preInboundNo,
						0L);
		if (optInboundHeader.isEmpty()) {
			throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
					",refDocNumber: " + refDocNumber + 
					",preInboundNo: " + preInboundNo + 
					" doesn't exist.");
		} 
		InboundHeader inboundHeader = optInboundHeader.get();
		
		List<InboundHeaderEntity> listInboundHeaderEntity = new ArrayList<>();
		List<InboundLine> inboundLineList = inboundLineService.getInboundLine(inboundHeader.getWarehouseId(), 
												inboundHeader.getRefDocNumber(), inboundHeader.getPreInboundNo());
		log.info("inboundLineList found: " + inboundLineList);
		
		List<InboundLineEntity> listInboundLineEntity = new ArrayList<>();
		for (InboundLine inboundLine : inboundLineList) {
			InboundLineEntity inboundLineEntity = new InboundLineEntity();
			BeanUtils.copyProperties(inboundLine, inboundLineEntity, CommonUtils.getNullPropertyNames(inboundLine));
			inboundLineEntity.setOrderQty(inboundLine.getOrderedQuantity());
			inboundLineEntity.setOrderUom(inboundLine.getOrderedUnitOfMeasure());
			listInboundLineEntity.add(inboundLineEntity);
		}
		
		InboundHeaderEntity inboundHeaderEntity = new InboundHeaderEntity();
		BeanUtils.copyProperties(inboundHeader, inboundHeaderEntity, CommonUtils.getNullPropertyNames(inboundHeader));
		inboundHeaderEntity.setInboundLine(listInboundLineEntity);
		listInboundHeaderEntity.add(inboundHeaderEntity);
			
		return inboundHeaderEntity;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param refDocNumber
	 * @param preInboundNo
	 * @return
	 */
	public InboundHeader getInboundHeaderByEntity (String warehouseId, String refDocNumber, String preInboundNo) {
		Optional<InboundHeader> optInboundHeader = 
				inboundHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
						getLanguageId(),
						getCompanyCode(),
						getPlantId(),
						warehouseId,
						refDocNumber,
						preInboundNo,
						0L);
		if (optInboundHeader.isEmpty()) {
			throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
					",refDocNumber: " + refDocNumber + 
					",preInboundNo: " + preInboundNo + 
					" doesn't exist.");
		} 
		return optInboundHeader.get();
	}
	
	/**
	 * 
	 * @param refDocNumber
	 * @param preInboundNo
	 * @return
	 */
	public List<InboundHeader> getInboundHeader (String refDocNumber, String preInboundNo) {
		List<InboundHeader> inboundHeader = 
				inboundHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
						getLanguageId(),
						getCompanyCode(),
						getPlantId(),
						refDocNumber,
						preInboundNo,
						0L);
		if (inboundHeader.isEmpty()) {
			throw new BadRequestException("The given values: " + 
					",refDocNumber: " + refDocNumber + 
					",preInboundNo: " + preInboundNo + 
					" doesn't exist.");
		} 
		return inboundHeader;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @return
	 */
	public List<InboundHeaderEntity> getInboundHeaderWithStatusId (String warehouseId) {
		List<InboundHeader> inboundHeaderList = 
				inboundHeaderRepository.findByWarehouseIdAndDeletionIndicator(warehouseId, 0L);
		if (inboundHeaderList.isEmpty()) {
			throw new BadRequestException("The given InboundHeader :" +
										" warehouseId: " + warehouseId + 
										" doesn't exist.");
		}
		
		List<InboundHeaderEntity> listInboundHeaderEntity = new ArrayList<>();
		for (InboundHeader inboundHeader : inboundHeaderList) {
			List<InboundLine> inboundLineList = inboundLineService.getInboundLine(inboundHeader.getWarehouseId(), 
					inboundHeader.getRefDocNumber(), inboundHeader.getPreInboundNo());
			log.info("inboundLineList found: " + inboundLineList);
			
			List<InboundLineEntity> listInboundLineEntity = new ArrayList<>();
			for (InboundLine inboundLine : inboundLineList) {
				InboundLineEntity inboundLineEntity = new InboundLineEntity();
				BeanUtils.copyProperties(inboundLine, inboundLineEntity, CommonUtils.getNullPropertyNames(inboundLine));
				listInboundLineEntity.add(inboundLineEntity);
			}
			
			InboundHeaderEntity inboundHeaderEntity = new InboundHeaderEntity();
			BeanUtils.copyProperties(inboundHeader, inboundHeaderEntity, CommonUtils.getNullPropertyNames(inboundHeader));
			inboundHeaderEntity.setInboundLine(listInboundLineEntity);
			listInboundHeaderEntity.add(inboundHeaderEntity);
		}
		return listInboundHeaderEntity;
	}
	
	/**
	 * 
	 * @param searchInboundHeader
	 * @return
	 * @throws ParseException
	 */
	public List<InboundHeader> findInboundHeader(SearchInboundHeader searchInboundHeader) throws Exception {
		if (searchInboundHeader.getStartCreatedOn() != null && searchInboundHeader.getStartCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchInboundHeader.getStartCreatedOn(), searchInboundHeader.getEndCreatedOn());
			searchInboundHeader.setStartCreatedOn(dates[0]);
			searchInboundHeader.setEndCreatedOn(dates[1]);
		}
		
		if (searchInboundHeader.getStartConfirmedOn() != null && searchInboundHeader.getStartConfirmedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchInboundHeader.getStartConfirmedOn(), searchInboundHeader.getEndConfirmedOn());
			searchInboundHeader.setStartConfirmedOn(dates[0]);
			searchInboundHeader.setEndConfirmedOn(dates[1]);
		}
		
		InboundHeaderSpecification spec = new InboundHeaderSpecification(searchInboundHeader);
		List<InboundHeader> results = inboundHeaderRepository.findAll(spec);
//		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createInboundHeader
	 * @param newInboundHeader
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public InboundHeader createInboundHeader (AddInboundHeader newInboundHeader, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Optional<InboundHeader> inboundheader = 
				inboundHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
						getLanguageId(),
						getCompanyCode(),
						getPlantId(),
						newInboundHeader.getWarehouseId(),
						newInboundHeader.getRefDocNumber(),
						newInboundHeader.getPreInboundNo(),
						0L);
		if (!inboundheader.isEmpty()) {
			throw new BadRequestException("Record is getting duplicated with the given values");
		}
		InboundHeader dbInboundHeader = new InboundHeader();
		log.info("newInboundHeader : " + newInboundHeader);
		BeanUtils.copyProperties(newInboundHeader, dbInboundHeader, CommonUtils.getNullPropertyNames(newInboundHeader));
		dbInboundHeader.setDeletionIndicator(0L);
		dbInboundHeader.setCreatedBy(loginUserID);
		dbInboundHeader.setUpdatedBy(loginUserID);
		dbInboundHeader.setCreatedOn(new Date());
		dbInboundHeader.setUpdatedOn(new Date());
		return inboundHeaderRepository.save(dbInboundHeader);
	}
	
	/**
	 * 
	 * @param refDocNumber
	 * @param preInboundNo
	 * @param asnNumber
	 * @return 
	 * @return
	 */
	public Boolean replaceASN(String refDocNumber, String preInboundNo, String asnNumber) {
		List<InboundHeader> inboundHeader = getInboundHeader(refDocNumber, preInboundNo);
		if (inboundHeader != null ) {
			// PREINBOUNDHEADER
			preInboundHeaderService.updateASN (asnNumber);
			
			// PREINBOUNDLINE
			preInboundLineService.updateASN(asnNumber);
			
			// STAGINGHEADER
			stagingHeaderService.updateASN(asnNumber);
			
			// STAGINGLINE
			stagingLineService.updateASN(asnNumber);
			
			// GRHEADER
			grHeaderService.updateASN(asnNumber);
			
			// GRLINE
			grLineService.updateASN(asnNumber);
			
			//PUTAWAYHEADER
			putAwayHeaderService.updateASN(asnNumber);
			
			//PUTAWAYLINE
			putAwayLineService.updateASN(asnNumber);
			
			// INBOUNDHEADER
			updateASN(asnNumber);
			
			// INBOUNDLINE
			inboundLineService.updateASN(asnNumber);
			return Boolean.TRUE;
		}
		return null;
	}
	
	/**
	 * updateInboundHeader
	 * @param loginUserId 
	 * @param refDocNumber
	 * @param updateInboundHeader
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public InboundHeader updateInboundHeader (String warehouseId, String refDocNumber, String preInboundNo, 
			String loginUserID, UpdateInboundHeader updateInboundHeader) 
			throws IllegalAccessException, InvocationTargetException {
		InboundHeader dbInboundHeader = getInboundHeaderByEntity(warehouseId, refDocNumber, preInboundNo);
		BeanUtils.copyProperties(updateInboundHeader, dbInboundHeader, CommonUtils.getNullPropertyNames(updateInboundHeader));
		dbInboundHeader.setUpdatedBy(loginUserID);
		dbInboundHeader.setUpdatedOn(new Date());
		return inboundHeaderRepository.save(dbInboundHeader);
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param lineNo
	 * @param itemCode
	 * @param updateInboundHeader
	 * @param loginUserID
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public AXApiResponse updateInboundHeaderConfirm(String warehouseId, String preInboundNo, String refDocNumber, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
//		List<InboundLine> dbInboundLines = inboundLineService.getInboundLine (warehouseId, refDocNumber, preInboundNo);
		List<Boolean> validationStatusList = new ArrayList<>();
		
		// PutawayLine Validation
		long putAwayLineStatusIdCount = putAwayLineService.getPutAwayLineByStatusId(warehouseId, preInboundNo, refDocNumber);
		log.info("PutAwayLine status----> : " + putAwayLineStatusIdCount);
		
		if (putAwayLineStatusIdCount == 0) {
			throw new BadRequestException("Error on Inbound Confirmation: PutAwayLines are NOT processed completely.");
		} 
		validationStatusList.add(true);
		
		// PutawayHeader Validation
		long putAwayHeaderStatusIdCount = putAwayHeaderService.getPutawayHeaderByStatusId(warehouseId, preInboundNo, refDocNumber);
		log.info("PutAwayHeader status----> : " + putAwayHeaderStatusIdCount);
		
		if (putAwayHeaderStatusIdCount == 0) {
			throw new BadRequestException("Error on Inbound Confirmation: PutAwayHeader are NOT processed completely.");
		} 
		validationStatusList.add(true);
		
		// StagingLine Validation
		long stagingLineStatusIdCount = stagingLineService.getStagingLineByStatusId(warehouseId, preInboundNo, refDocNumber);
		log.info("stagingLineStatusIdCount status----> : " + stagingLineStatusIdCount);
		
		if (stagingLineStatusIdCount == 0) {
			throw new BadRequestException("Error on Inbound Confirmation: StagingLine are NOT processed completely.");
		} 
		validationStatusList.add(true);
		
		boolean sendConfirmationToAX = false;
		for (boolean isConditionMet : validationStatusList) {
			if (isConditionMet) {
				sendConfirmationToAX = true;
			}
		}
		
		log.info("sendConfirmationToAX ----> : " + sendConfirmationToAX);
		if (!sendConfirmationToAX) {
			throw new BadRequestException("Order is NOT completely processed for OrderNumber : " + refDocNumber);
		}
		
		// Checking relevant tables for sending confirmation to AX
//		for (InboundLine dbInboundLine : dbInboundLines) {
//			long matchedCount = 0;
//			List<Boolean> validationStatusList = new ArrayList<>();
			
//			List<PutAwayLine> putAwayLineList = 
//				putAwayLineService.getPutAwayLine(warehouseId, preInboundNo, refDocNumber, dbInboundLine.getLineNo(), 
//						dbInboundLine.getItemCode());
//			List<Long> paStatusList = putAwayLineList.stream().map(PutAwayLine::getStatusId).collect(Collectors.toList());
//			matchedCount = paStatusList.stream().filter(a -> a == 20L || a == 22L).count();
//			boolean isConditionMet = (matchedCount == paStatusList.size());
//			log.info("PutAwayLine status condition check : " + isConditionMet);
//			
//			if (!isConditionMet) {
//				throw new BadRequestException("Error on Inbound Confirmation: PutAwayLines are NOT processed completely.");
//			}
			
			/*
			 * Pass WH_ID/PRE_IB_NO/REF_DOC_NO values in PUTAWAYHEADER table and 
			 * Validate STATUS_ID of all the values = 20 or 22
			 */
//			List<PutAwayHeader> putAwayHeaderList = putAwayHeaderService.getPutAwayHeader(warehouseId, preInboundNo, refDocNumber);
//			List<Long> paheaderStatusList = putAwayHeaderList.stream().map(PutAwayHeader::getStatusId).collect(Collectors.toList());
//			matchedCount = paheaderStatusList.stream().filter(a -> a == 20L || a == 22L).count();
//			boolean isConditionMet = (matchedCount == paheaderStatusList.size());
//			log.info("PutAwayHeader status condition check : " + isConditionMet);
//			
//			if (!isConditionMet) {
//				throw new BadRequestException("Error on Inbound Confirmation: PutAwayHeaders are NOT processed completely.");
//			}
//			
//			validationStatusList.add(isConditionMet);
//			log.info("PutAwayHeader status----> : " + paheaderStatusList);
			
			/*
			 * -----------StagingLine Validation---------------------------
			 * Validate StagingLine whether statusId is 17 OR 14
			 */
//			List<StagingLineEntity> stagingLineList = stagingLineService.getStagingLine(warehouseId, refDocNumber, preInboundNo, dbInboundLine.getLineNo(), dbInboundLine.getItemCode());
//			List<Long> stagingLineStatusList = stagingLineList.stream().map(StagingLineEntity::getStatusId).collect(Collectors.toList());
//			matchedCount = stagingLineStatusList.stream().filter(a -> a == 14L || a == 17L).count();
//			boolean isConditionMet = (matchedCount <= stagingLineStatusList.size());
//			log.info("StagingLine status condition check : " + isConditionMet);
//			
//			if (!isConditionMet) {
//				throw new BadRequestException("Error on Inbound Confirmation: StagingLines are NOT processed completely.");
//			}
//			
//			validationStatusList.add(isConditionMet);
//			log.info("StagingLine status----> : " + stagingLineStatusList);
			
//			long conditionCount = validationStatusList.stream().filter(b -> b == true).count();
//			log.info("conditionCount : " + conditionCount);
//			log.info("conditionCount ----> : " + (conditionCount == validationStatusList.size()));
//			
//			if (conditionCount == validationStatusList.size() && dbInboundLine.getStatusId() == 20) {
//				sendConfirmationToAX = true;
//			} else {
//				throw new BadRequestException("Order is NOT completely processed : " + conditionCount + "," + dbInboundLine.getStatusId());
//			}
//		}
		
		/*
		 * -----------Send Confirmation details to MS Dynamics through API----------------------- 
		 * Based on IB_ORD_TYP_ID, call Corresponding End points as per API document and 
		 * send the confirmation details to MS Dynamics via API integration as below
		 * Once the response is 200 then, we need to update inboundline and header table with StatusId = 24.
		 * */
		AXApiResponse axapiResponse = null;
		if (sendConfirmationToAX) {
			try {
				InboundHeader confirmedInboundHeader = getInboundHeaderByEntity(warehouseId, refDocNumber, preInboundNo);
				List<InboundLine> confirmedInboundLines = 
						inboundLineService.getInboundLinebyRefDocNoISNULL(confirmedInboundHeader.getWarehouseId(), 
								confirmedInboundHeader.getRefDocNumber(), confirmedInboundHeader.getPreInboundNo());
				
				log.info("Order type id: " + confirmedInboundHeader.getInboundOrderTypeId());
				// If IB_ORD_TYP_ID = 1, call ASN API
				if (confirmedInboundHeader.getInboundOrderTypeId() == 1L) { 
					axapiResponse = postASN(confirmedInboundHeader, confirmedInboundLines);
					log.info("AXApiResponse: " + axapiResponse);
				} 
				
				// If IB_ORD_TYP_ID = 2, call StoreReturns API
				if (confirmedInboundHeader.getInboundOrderTypeId() == 2L) { 
					axapiResponse = postStoreReturn(confirmedInboundHeader, confirmedInboundLines);
					log.info("AXApiResponse: " + axapiResponse);
				} 
				
				// If IB_ORD_TYP_ID = 3, call InterWarehouse Receipt Confirmation API
				if (confirmedInboundHeader.getInboundOrderTypeId() == 3L) { 
					axapiResponse = postInterWarehouse(confirmedInboundHeader, confirmedInboundLines);
					log.info("AXApiResponse: " + axapiResponse);
				} 
				
				// If IB_ORD_TYP_ID = 4, call Sale Order Returns API
				if (confirmedInboundHeader.getInboundOrderTypeId() == 4L) { 
					axapiResponse = postSOReturn(confirmedInboundHeader, confirmedInboundLines);
					log.info("AXApiResponse: " + axapiResponse);
				}
			} catch (Exception e) {
				log.error("AXApiResponse error: " + e.toString());
				e.printStackTrace();
			}
		}
		
		// If AX throws any error then, return the Error response
		if (axapiResponse != null && axapiResponse.getStatusCode() != null && !axapiResponse.getStatusCode().equalsIgnoreCase("200")) {
			String errorFromAXAPI = axapiResponse.getMessage();
			AXApiResponse axapiErrorResponse = new AXApiResponse();
			axapiErrorResponse.setStatusCode("400");
			axapiErrorResponse.setMessage("Error from AX: " + errorFromAXAPI);
			return axapiErrorResponse;
		}
		
		if (axapiResponse != null && axapiResponse.getStatusCode() != null && 
				axapiResponse.getStatusCode().equalsIgnoreCase("200")) {
			inboundLineRepository.updateInboundLineStatus (warehouseId, refDocNumber, 24L, loginUserID, new Date());
			log.info("InboundLine updated");
			
			inboundHeaderRepository.updateInboundHeaderStatus (warehouseId, refDocNumber, 24L, loginUserID, new Date());
			log.info("InboundHeader updated");
			
			preInboundHeaderRepository.updatePreInboundHeaderEntityStatus (warehouseId, refDocNumber, 24L);
			log.info("PreInboundHeader updated");
			
			preInboundLineRepository.updatePreInboundLineStatus (warehouseId, refDocNumber, 24L);
			log.info("PreInboundLine updated");
			
			grHeaderRepository.updateGrHeaderStatus (warehouseId, refDocNumber, 24L);
			log.info("grHeader updated");
			
			stagingHeaderRepository.updateStagingHeaderStatus(warehouseId, refDocNumber, 24L);
			log.info("stagingHeader updated");
			
		}
		
		// Update relevant tables once AXResponse is success
//		Long statusId = 24L;
//		for (InboundLine dbInboundLine : dbInboundLines) {
//			// Checking the AX-API response
//			if (axapiResponse != null && axapiResponse.getStatusCode() != null && 
//					axapiResponse.getStatusCode().equalsIgnoreCase("200")) {
				// Checking the status of Line record whether Status = 20
//				try {
//					BeanUtils.copyProperties(dbInboundLine, dbInboundLine, CommonUtils.getNullPropertyNames(dbInboundLine));
//					dbInboundLine.setStatusId(statusId);
//					dbInboundLine.setConfirmedBy(loginUserID);
//					dbInboundLine.setConfirmedOn(new Date());
//					dbInboundLine.setUpdatedBy(loginUserID);
//					dbInboundLine.setUpdatedOn(new Date());
//					dbInboundLine = inboundLineRepository.save(dbInboundLine);
//					log.info("dbInboundLine updated : " + dbInboundLine);
//				} catch (Exception e1) {
//					log.error("InboundLine update error: " + e1.toString());
//					e1.printStackTrace();
//				}
		
//				try {
//					// Inbound Header Update
//					InboundHeader dbInboundHeader = getInboundHeaderByEntity(warehouseId, refDocNumber, preInboundNo);
//					dbInboundHeader.setStatusId(statusId);
//					dbInboundHeader.setUpdatedBy(loginUserID);
//					dbInboundHeader.setUpdatedOn(new Date());
//					dbInboundHeader.setConfirmedBy(loginUserID);
//					dbInboundHeader.setConfirmedOn(new Date());
//					dbInboundHeader = inboundHeaderRepository.save(dbInboundHeader);
//					log.info("InboundHeader updated : " + dbInboundHeader);
//				} catch (Exception e1) {
//					log.info("InboundHeader update error: " + dbInboundLine);
//					e1.printStackTrace();
//				}
				
				// PREINBOUND table updates
//				try {
//					PreInboundHeader preInboundHeader = preInboundHeaderService.updatePreInboundHeader(preInboundNo, warehouseId, 
//							refDocNumber, statusId, loginUserID);
//					log.info("PreInboundHeader updated : " + preInboundHeader);
//					
//					PreInboundLineEntity preInboundLine = preInboundLineService.updatePreInboundLine(preInboundNo, warehouseId, 
//							refDocNumber, dbInboundLine.getLineNo(), dbInboundLine.getItemCode(), statusId, loginUserID);
//					log.info("PreInboundLine updated : " + preInboundLine);
//				} catch (Exception e1) {
//					log.error("PreInboundHeader & line update error: " + e1.toString());
//					e1.printStackTrace();
//				}	
				
//				try {
//					// GRHEADER table updates
//					grHeaderService.updateGrHeader(warehouseId, preInboundNo, refDocNumber, dbInboundLine.getLineNo(), 
//							dbInboundLine.getItemCode(), statusId, loginUserID);
//					log.info("grHeaderService updated : ");
//				} catch (Exception e) {
//					log.error("grHeaderService update error: " + e.getLocalizedMessage());
//					e.printStackTrace();
//				}	
			
//				try {
//					// STAGINGHEADER table updates
//					stagingHeaderService.updateStagingHeader(warehouseId, preInboundNo, refDocNumber, dbInboundLine.getLineNo(), 
//							dbInboundLine.getItemCode(), statusId, loginUserID);
//					log.info("stagingHeaderService updated : ");
//				} catch (Exception e) {
//					log.error("stagingHeaderService update error: " + e.getLocalizedMessage());
//					e.printStackTrace();
//				}
//			}
//		}
		return axapiResponse;
	}
	
	/**
	 * 
	 * @param asnNumber
	 */
	public void updateASN (String asnNumber) {
		List<InboundHeader> inboundHeaders = getInboundHeaders();
		inboundHeaders.forEach(p -> p.setReferenceField1(asnNumber));
		inboundHeaderRepository.saveAll(inboundHeaders);
	}
	
	/**
	 * deleteInboundHeader
	 * @param loginUserID 
	 * @param refDocNumber
	 */
	public void deleteInboundHeader (String warehouseId, String refDocNumber, String preInboundNo, String loginUserID) {
		InboundHeader containerReceipt = getInboundHeaderByEntity(warehouseId, refDocNumber, preInboundNo);
		if ( containerReceipt != null) {
			containerReceipt.setDeletionIndicator(1L);
			containerReceipt.setUpdatedBy(loginUserID);
			inboundHeaderRepository.save(containerReceipt);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + refDocNumber);
		}
	}
	
	//----------------------------------------------------------------------------------------------------
	/**
	 * POST ASN
	 * @param confirmedInboundHeader
	 * @param confirmedInboundLines
	 * @return 
	 */
	private AXApiResponse postASN (InboundHeader confirmedInboundHeader, List<InboundLine> confirmedInboundLines) {
		ASNHeader asnHeader = new ASNHeader();
		asnHeader.setAsnNumber(confirmedInboundHeader.getRefDocNumber());	// REF_DOC_NO
		
		List<ASNLine> asnLines = new ArrayList<>();
		for (InboundLine inboundLine : confirmedInboundLines) {
			asnHeader.setSupplierInvoice(inboundLine.getInvoiceNo());
			
			/*
			 * AcceptQty not equal to null and greater than 0
			 * OR
			 * DamageQty not equal to null and greater than 0
			 */
			if ((inboundLine.getAcceptedQty() != null && inboundLine.getAcceptedQty() > 0)
					|| (inboundLine.getDamageQty() != null && inboundLine.getDamageQty() > 0)) {
				inboundLine.setConfirmedOn(new Date());
				
				ASNLine asnLine = new ASNLine();
			
				// SKU	<-	ITM_CODE
				asnLine.setSku(inboundLine.getItemCode());
				
				// SKU description	<- ITEM_TEXT
				asnLine.setSkuDescription(inboundLine.getDescription());
				
				// Line reference	<-	IB_LINE_NO
				asnLine.setLineReference(inboundLine.getLineNo());
				
				// Expected Qty	<- ORD_QTY
				asnLine.setExpectedQty(inboundLine.getOrderedQuantity());
				
				// UOM	<-	ORD_UOM
				asnLine.setUom(inboundLine.getOrderedUnitOfMeasure());
				
				// Received Qty	<-	ACCEPT_QTY
				if (inboundLine.getAcceptedQty() == null ) {
					asnLine.setReceivedQty(0D);
				} else {
					asnLine.setReceivedQty(inboundLine.getAcceptedQty());
				}
				
				// Damage Qty <-	DAMAGE_QTY
				if (inboundLine.getDamageQty() == null ) {
					asnLine.setDamagedQty(0D);
				} else {
					asnLine.setDamagedQty(inboundLine.getDamageQty());
				}
				
				// Pack Qty	<-	ITM_CASE_QTY
				asnLine.setPackQty(inboundLine.getItemCaseQty());
				
				// Actual Receipt Date	<-	IB_CNF_ON
				String date = DateUtils.date2String_MMDDYYYY(inboundLine.getConfirmedOn());
				asnLine.setActualReceiptDate(date);
				
				// Warehouse ID	<-	WH_ID
				asnLine.setWareHouseId(inboundLine.getWarehouseId());
				asnLines.add(asnLine);
			}
		}
		
		if (asnLines.isEmpty()) {
			throw new BadRequestException ("ConfirmedInboundLines had neither AcceptQty nor DamageQty. Please check the data.");
		}
		
		ASN asn = new ASN();
		asn.setAsnHeader(asnHeader);
		asn.setAsnLines(asnLines);
		log.info("Sending ASN : " + asn);
		
		/*
		 * Posting to AX_API
		 */
		AXAuthToken authToken = authTokenService.generateAXOAuthToken();
		AXApiResponse apiResponse = warehouseService.postASNConfirmation(asn, authToken.getAccess_token());
		log.info("apiResponse : " + apiResponse);
		
		// Capture the AXResponse in Table
		IntegrationApiResponse response = new IntegrationApiResponse();
		response.setOrderNumber(asnHeader.getAsnNumber());
		response.setOrderType("INBOUND");
		response.setOrderTypeId(confirmedInboundHeader.getInboundOrderTypeId());
		response.setResponseCode(apiResponse.getStatusCode());
		response.setResponseText(apiResponse.getMessage());
		response.setApiUrl(propertiesConfig.getAxapiServiceAsnUrl());
		response.setTransDate(new Date());
		
		integrationApiResponseRepository.save(response);
		return apiResponse;
	}
	
	/**
	 * StoreReturn API
	 * @param confirmedInboundHeader
	 * @param confirmedInboundLines
	 * @return
	 */
	private AXApiResponse postStoreReturn(InboundHeader confirmedInboundHeader,
			List<InboundLine> confirmedInboundLines) {
		StoreReturnHeader storeReturnHeader = new StoreReturnHeader();
		storeReturnHeader.setTransferOrderNumber(confirmedInboundHeader.getRefDocNumber());	// REF_DOC_NO
		List<StoreReturnLine> storeReturnLines = new ArrayList<>();
		for (InboundLine inboundLine : confirmedInboundLines) {
			/*
			 * AcceptQty not equal to null and greater than 0
			 * OR
			 * DamageQty not equal to null and greater than 0
			 */
			if ((inboundLine.getAcceptedQty() != null && inboundLine.getAcceptedQty() > 0)
					|| (inboundLine.getDamageQty() != null && inboundLine.getDamageQty() > 0)) {
				inboundLine.setConfirmedOn(new Date());
				StoreReturnLine storeReturnLine = new StoreReturnLine();
				
				// SKU	<-	ITM_CODE
				storeReturnLine.setSku(inboundLine.getItemCode());
				
				// SKU description	<- ITEM_TEXT
				storeReturnLine.setSkuDescription(inboundLine.getDescription());
				
				// Line reference	<-	IB_LINE_NO
				storeReturnLine.setLineReference(inboundLine.getLineNo());
				
				// Expected Qty	<- ORD_QTY
				storeReturnLine.setExpectedQty(inboundLine.getOrderedQuantity());
				
				// UOM	<-	ORD_UOM
				storeReturnLine.setUom(inboundLine.getOrderedUnitOfMeasure());
				
				// Received Qty	<-	ACCEPT_QTY
				if (inboundLine.getAcceptedQty() == null ) {
					storeReturnLine.setReceivedQty(0D);
				} else {
					storeReturnLine.setReceivedQty(inboundLine.getAcceptedQty());
				}
				
				// Damage Qty <-	DAMAGE_QTY
				if (inboundLine.getDamageQty() == null ) {
					storeReturnLine.setDamagedQty(0D);
				} else {
					storeReturnLine.setDamagedQty(inboundLine.getDamageQty());
				}
				
				// Pack Qty	<-	ITM_CASE_QTY
				storeReturnLine.setPackQty(inboundLine.getItemCaseQty());
				
				// Actual Receipt Date	<-	IB_CNF_ON
				String date = DateUtils.date2String_MMDDYYYY(inboundLine.getConfirmedOn());
				storeReturnLine.setActualReceiptDate(date);
				
				// Warehouse ID	<-	WH_ID
				storeReturnLine.setWareHouseId(inboundLine.getWarehouseId());
				
				// Store ID <- PARTNER_CODE
				storeReturnLine.setStoreId(inboundLine.getVendorCode());
				
				storeReturnLines.add(storeReturnLine);
			}
		}
		
		if (storeReturnLines.isEmpty()) {
			throw new BadRequestException ("ConfirmedInboundLines had neither AcceptQty nor DamageQty. Please check the data.");
		}
		
		StoreReturn storeReturn = new StoreReturn();
		storeReturn.setToHeader(storeReturnHeader);
		storeReturn.setToLines(storeReturnLines);
		log.info("Sending StoreReturn : " + storeReturn);
		
		/*
		 * Posting to AX_API
		 */
		AXAuthToken authToken = authTokenService.generateAXOAuthToken();
		AXApiResponse apiResponse = warehouseService.postStoreReturnConfirmation(storeReturn, authToken.getAccess_token());
		log.info("apiResponse : " + apiResponse);
		
		// Capture the AXResponse in Table
		IntegrationApiResponse response = new IntegrationApiResponse();
		response.setOrderNumber(storeReturnHeader.getTransferOrderNumber());
		response.setOrderType("INBOUND");
		response.setOrderTypeId(confirmedInboundHeader.getInboundOrderTypeId());
		response.setResponseCode(apiResponse.getStatusCode());
		response.setResponseText(apiResponse.getMessage());
		response.setApiUrl(propertiesConfig.getAxapiServiceStoreReturnUrl());
		response.setTransDate(new Date());
		
		integrationApiResponseRepository.save(response);
		return apiResponse;
	}
	
	/**
	 * SO Return
	 * @param confirmedInboundHeader
	 * @param confirmedInboundLines
	 * @return
	 */
	private AXApiResponse postSOReturn(InboundHeader confirmedInboundHeader, List<InboundLine> confirmedInboundLines) {
		SOReturnHeader soReturnHeader = new SOReturnHeader();
		soReturnHeader.setReturnOrderReference(confirmedInboundHeader.getRefDocNumber());	// REF_DOC_NO
		
		List<SOReturnLine> soReturnLines = new ArrayList<>();
		for (InboundLine inboundLine : confirmedInboundLines) {
			/*
			 * AcceptQty not equal to null and greater than 0
			 * OR
			 * DamageQty not equal to null and greater than 0
			 */
			if ((inboundLine.getAcceptedQty() != null && inboundLine.getAcceptedQty() > 0)
					|| (inboundLine.getDamageQty() != null && inboundLine.getDamageQty() > 0)) {
				inboundLine.setConfirmedOn(new Date());
				SOReturnLine soReturnLine = new SOReturnLine();
			
				// Salesroderreference <-	REF_FIELD_4
				soReturnLine.setSalesOrderReference(inboundLine.getReferenceField4());
				
				// SKU	<-	ITM_CODE
				soReturnLine.setSku(inboundLine.getItemCode());
				
				// SKU description	<- ITEM_TEXT
				soReturnLine.setSkuDescription(inboundLine.getDescription());
				
				// Line reference	<-	IB_LINE_NO
				soReturnLine.setLineReference(inboundLine.getLineNo());
				
				// Expected Qty	<- ORD_QTY
				soReturnLine.setExpectedQty(inboundLine.getOrderedQuantity());
				
				// UOM	<-	ORD_UOM
				soReturnLine.setUom(inboundLine.getOrderedUnitOfMeasure());
				
				// Received Qty	<-	ACCEPT_QTY
				if (inboundLine.getAcceptedQty() == null ) {
					soReturnLine.setReturnQty(0D);
				} else {
					soReturnLine.setReturnQty(inboundLine.getAcceptedQty());
				}
				
				// Actual Receipt Date	<-	IB_CNF_ON
				String date = DateUtils.date2String_MMDDYYYY(inboundLine.getConfirmedOn());
				soReturnLine.setActualReceiptDate(date);
				
				// Warehouse ID	<-	WH_ID
				soReturnLine.setWareHouseId(inboundLine.getWarehouseId());
				
				soReturnLines.add(soReturnLine);
			}
		}
		
		if (soReturnLines.isEmpty()) {
			throw new BadRequestException ("ConfirmedInboundLines had neither AcceptQty nor DamageQty. Please check the data.");
		}
		
		SOReturn soReturn = new SOReturn();
		soReturn.setReturnOrderHeader(soReturnHeader);
		soReturn.setReturnOrderLines(soReturnLines);
		log.info("Sending SOReturn : " + soReturn);
		
		/*
		 * Posting to AX_API
		 */
		AXAuthToken authToken = authTokenService.generateAXOAuthToken();
		AXApiResponse apiResponse = warehouseService.postSOReturnConfirmation(soReturn, authToken.getAccess_token());
		log.info("apiResponse : " + apiResponse);
		
		// Capture the AXResponse in Table
		IntegrationApiResponse response = new IntegrationApiResponse();
		response.setOrderNumber(soReturnHeader.getReturnOrderReference());
		response.setOrderType("INBOUND");
		response.setOrderTypeId(confirmedInboundHeader.getInboundOrderTypeId());
		response.setResponseCode(apiResponse.getStatusCode());
		response.setResponseText(apiResponse.getMessage());
		response.setApiUrl(propertiesConfig.getAxapiServiceSOReturnUrl());
		response.setTransDate(new Date());
		
		integrationApiResponseRepository.save(response);
		return apiResponse;
	}
	
	/**
	 * InterWarehouse API
	 * @param confirmedInboundHeader
	 * @param confirmedInboundLines
	 * @return
	 */
	private AXApiResponse postInterWarehouse (InboundHeader confirmedInboundHeader,
			List<InboundLine> confirmedInboundLines) {
		InterWarehouseTransferHeader toHeader = new InterWarehouseTransferHeader();
		toHeader.setTransferOrderNumber(confirmedInboundHeader.getRefDocNumber());	// REF_DOC_NO
		
		List<InterWarehouseTransferLine> toLines = new ArrayList<>();
		for (InboundLine inboundLine : confirmedInboundLines) {
			/*
			 * AcceptQty not equal to null and greater than 0
			 * OR
			 * DamageQty not equal to null and greater than 0
			 */
			if ((inboundLine.getAcceptedQty() != null && inboundLine.getAcceptedQty() > 0)
					|| (inboundLine.getDamageQty() != null && inboundLine.getDamageQty() > 0)) {
				inboundLine.setConfirmedOn(new Date());
				InterWarehouseTransferLine iwhTransferLine = new InterWarehouseTransferLine();
				
				// SKU	<-	ITM_CODE
				iwhTransferLine.setSku(inboundLine.getItemCode());
				
				// SKU description	<- ITEM_TEXT
				iwhTransferLine.setSkuDescription(inboundLine.getDescription());
				
				// Line reference	<-	IB_LINE_NO
				iwhTransferLine.setLineReference(inboundLine.getLineNo());
				
				// Expected Qty	<- ORD_QTY
				iwhTransferLine.setExpectedQty(inboundLine.getOrderedQuantity());
				
				// UOM	<-	ORD_UOM
				iwhTransferLine.setUom(inboundLine.getOrderedUnitOfMeasure());
				
				// Received Qty	<-	ACCEPT_QTY
				if (inboundLine.getAcceptedQty() == null ) {
					iwhTransferLine.setReceivedQty(0D);
				} else {
					iwhTransferLine.setReceivedQty(inboundLine.getAcceptedQty());
				}
				
				// Damage Qty <-	DAMAGE_QTY
				if (inboundLine.getDamageQty() == null ) {
					iwhTransferLine.setDamageQty(0D);
				} else {
					iwhTransferLine.setDamageQty(inboundLine.getDamageQty());
				}
				
				// Pack Qty	<-	ITM_CASE_QTY
				iwhTransferLine.setPackQty(inboundLine.getItemCaseQty());
				
				// Actual Receipt Date	<-	IB_CNF_ON
				String date = DateUtils.date2String_MMDDYYYY(inboundLine.getConfirmedOn());
				iwhTransferLine.setActualReceiptDate(date);
				
				// Warehouse ID	<-	WH_ID
				iwhTransferLine.setToWhsId(inboundLine.getWarehouseId());
				
				// FromWhsID <-	PARTNER_CODE
				iwhTransferLine.setFromWhsId(inboundLine.getVendorCode());
				
				toLines.add(iwhTransferLine);
			}
		}
		
		if (toLines.isEmpty()) {
			throw new BadRequestException ("confirmedInboundLines had neither AcceptQty nor DamageQty. Please check the data.");
		}
		
		InterWarehouseTransfer interWarehouseTransfer = new InterWarehouseTransfer();
		interWarehouseTransfer.setToHeader(toHeader);
		interWarehouseTransfer.setToLines(toLines);
		log.info("Sending InterWarehouseTransfer : " + interWarehouseTransfer);
		
		/*
		 * Posting to AX_API
		 */
		AXAuthToken authToken = authTokenService.generateAXOAuthToken();
		AXApiResponse apiResponse = warehouseService.postInterWarehouseTransferConfirmation(interWarehouseTransfer, authToken.getAccess_token());
		log.info("apiResponse : " + apiResponse);
		
		// Capture the AXResponse in Table
		IntegrationApiResponse response = new IntegrationApiResponse();
		response.setOrderNumber(toHeader.getTransferOrderNumber());
		response.setOrderType("INBOUND");
		response.setOrderTypeId(confirmedInboundHeader.getInboundOrderTypeId());
		response.setResponseCode(apiResponse.getStatusCode());
		response.setResponseText(apiResponse.getMessage());
		response.setApiUrl(propertiesConfig.getAxapiServiceInterwareHouseUrl());
		response.setTransDate(new Date());
		
		integrationApiResponseRepository.save(response);
		return apiResponse;
	}
}
