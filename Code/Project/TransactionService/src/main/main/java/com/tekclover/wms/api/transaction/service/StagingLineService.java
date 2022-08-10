package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.transaction.model.inbound.UpdateInboundLine;
import com.tekclover.wms.api.transaction.model.inbound.gr.AddGrHeader;
import com.tekclover.wms.api.transaction.model.inbound.gr.GrHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.PreInboundLineEntity;
import com.tekclover.wms.api.transaction.model.inbound.staging.AddStagingLine;
import com.tekclover.wms.api.transaction.model.inbound.staging.AssignHHTUser;
import com.tekclover.wms.api.transaction.model.inbound.staging.CaseConfirmation;
import com.tekclover.wms.api.transaction.model.inbound.staging.SearchStagingLine;
import com.tekclover.wms.api.transaction.model.inbound.staging.StagingHeader;
import com.tekclover.wms.api.transaction.model.inbound.staging.StagingLine;
import com.tekclover.wms.api.transaction.model.inbound.staging.StagingLineEntity;
import com.tekclover.wms.api.transaction.model.inbound.staging.UpdateStagingHeader;
import com.tekclover.wms.api.transaction.model.inbound.staging.UpdateStagingLine;
import com.tekclover.wms.api.transaction.repository.PreInboundLineRepository;
import com.tekclover.wms.api.transaction.repository.StagingLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.StagingLineSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StagingLineService extends BaseService {
	
	@Autowired
	private StagingLineRepository stagingLineEntityRepository;
	
	@Autowired
	private StagingHeaderService stagingHeaderService;
	
	@Autowired
	private InboundLineService inboundLineService;
	
	@Autowired
	private PreInboundLineService preInboundLineService;
	
	@Autowired
	private PreInboundLineRepository preInboundLineRepository;
	
	@Autowired
	private GrHeaderService grHeaderService;
	
	@Autowired
	private IDMasterService idmasterService;
	
	@Autowired
	private AuthTokenService authTokenService;
	
	/**
	 * getStagingLineEntitys
	 * @return
	 */
	public List<StagingLineEntity> getStagingLines () {
		List<StagingLineEntity> stagingLineEntityList =  stagingLineEntityRepository.findAll();
		stagingLineEntityList = stagingLineEntityList.stream().filter(n -> n.getDeletionIndicator() != null && 
				n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return stagingLineEntityList;
	}
	
	/**
	 * getStagingLineEntity
	 * @param palletCode
	 * @return
	 */
	public StagingLineEntity getStagingLine (String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, 
			String palletCode, String caseCode, Long lineNo, String itemCode) {
		Optional<StagingLineEntity> StagingLineEntity = stagingLineEntityRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndPalletCodeAndCaseCodeAndLineNoAndItemCodeAndDeletionIndicator(
									getLanguageId(),
									getCompanyCode(),
									getPlantId(),
									warehouseId, 
									preInboundNo, 
									refDocNumber, 
									stagingNo, 
									palletCode, 
									caseCode, 
									lineNo, 
									itemCode,
									0L);
		if (StagingLineEntity.isEmpty()) {
			throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
					",refDocNumber: " + refDocNumber + 
					",preInboundNo: " + preInboundNo + 	
					",stagingNo: " + stagingNo +
					",palletCode: " + palletCode + 
					",caseCode: " + caseCode + 
					",lineNo: " + lineNo + 
					",itemCode: " + itemCode + 
					" doesn't exist.");
		} 
		
		return StagingLineEntity.get();
	}
	
	/**
	 * getStagingLineEntity
	 * @param warehouseId
	 * @param refDocNumber
	 * @param preInboundNo
	 * @param lineNo
	 * @param itemCode
	 * @return
	 */
	public List<StagingLineEntity> getStagingLine (String warehouseId, String refDocNumber, String preInboundNo, Long lineNo,
			String itemCode) {
				List<StagingLineEntity> StagingLineEntity = 
						stagingLineEntityRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndDeletionIndicator(
							getLanguageId(),
							getCompanyCode(),
							getPlantId(),
							warehouseId, 
							refDocNumber,
							preInboundNo, 
							lineNo, 
							itemCode,
							0L);
			if (StagingLineEntity.isEmpty()) {
				throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
				",refDocNumber: " + refDocNumber + "," +
				",preInboundNo: " + preInboundNo + "," +
				",lineNo: " + lineNo + 
				",itemCode: " + itemCode + 
				" doesn't exist.");
			} 
		
		return StagingLineEntity;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param refDocNumber
	 * @param preInboundNo
	 * @param lineNo
	 * @param itemCode
	 * @param caseCode
	 * @return
	 */
	public List<StagingLineEntity> getStagingLine (String warehouseId, String refDocNumber, String preInboundNo, Long lineNo,
			String itemCode, String caseCode) {
				List<StagingLineEntity> StagingLineEntity = 
						stagingLineEntityRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndCaseCodeAndDeletionIndicator(
							getLanguageId(),
							getCompanyCode(),
							getPlantId(),
							warehouseId, 
							refDocNumber,
							preInboundNo, 
							lineNo, 
							itemCode,
							caseCode,
							0L);
			if (StagingLineEntity.isEmpty()) {
			throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
			",refDocNumber: " + refDocNumber + "," +
			",preInboundNo: " + preInboundNo + "," +
			",lineNo: " + lineNo + 
			",itemCode: " + itemCode + 
			",caseCode: " + caseCode + 
			" doesn't exist.");
			} 
		
		return StagingLineEntity;
	}
	
	/**
	 * 
	 * @param searchStagingLine
	 * @return
	 * @throws ParseException
	 */
	public List<StagingLineEntity> findStagingLine(SearchStagingLine searchStagingLine) 
			throws ParseException {
		StagingLineSpecification spec = new StagingLineSpecification(searchStagingLine);
		List<StagingLineEntity> searchResults = stagingLineEntityRepository.findAll(spec);
		log.info("searchResults: " + searchResults);
		
		List<StagingLineEntity> stagingLineEntityList = 
				searchResults.stream().filter(n -> n.getDeletionIndicator() != null && 
				n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return stagingLineEntityList;
	}
	
	/**
	 * createStagingLineEntity
	 * @param newStagingLineEntity
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<StagingLine> createStagingLine (List<AddStagingLine> newStagingLines, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		List<StagingLineEntity> stagingLineEntityList = new ArrayList<>();
		String preInboundNo = null;
		
		for (AddStagingLine newStagingLine : newStagingLines) {
			log.info("newStagingLineEntity : " + newStagingLine);
			if (newStagingLine.getCaseCode() == null) {
				throw new BadRequestException("CaseCode is not presented.");
			}
			
			// Warehouse
			AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
			Warehouse warehouse = idmasterService.getWarehouse(newStagingLine.getWarehouseId(), authTokenForIDMasterService.getAccess_token());
			// Insert based on the number of casecodes
			
			for (String caseCode : newStagingLine.getCaseCode()) {
				StagingLineEntity dbStagingLineEntity = new StagingLineEntity();
				BeanUtils.copyProperties(newStagingLine, dbStagingLineEntity, CommonUtils.getNullPropertyNames(newStagingLine));
				dbStagingLineEntity.setCaseCode(caseCode);
				dbStagingLineEntity.setPalletCode(caseCode); 	//Copy CASE_CODE
	
				// STATUS_ID - Hard Coded Value "13"
				dbStagingLineEntity.setStatusId (13L);
				
				// C_ID
				dbStagingLineEntity.setCompanyCode(warehouse.getCompanyCode());
				
				// PLANT_ID
				dbStagingLineEntity.setPlantId(warehouse.getPlantId());
				
				// LANG_ID
				dbStagingLineEntity.setLanguageId("EN");
				
				dbStagingLineEntity.setDeletionIndicator(0L);
				dbStagingLineEntity.setCreatedBy(loginUserID);
				dbStagingLineEntity.setUpdatedBy(loginUserID);
				dbStagingLineEntity.setCreatedOn(new Date());
				dbStagingLineEntity.setUpdatedOn(new Date());
				stagingLineEntityList.add(dbStagingLineEntity);
				
				// PreInboundNo
				preInboundNo = dbStagingLineEntity.getPreInboundNo();
			}
		}
			
		// Batch Insert
		if (!stagingLineEntityList.isEmpty()) {
			List<StagingLineEntity> createdStagingLineEntityList = 
					stagingLineEntityRepository.saveAll(stagingLineEntityList);
			log.info("created StagingLine records." + createdStagingLineEntityList);
			
			List<StagingLine> responseStagingLineList = new ArrayList<>();
			for (StagingLineEntity stagingLineEntity : createdStagingLineEntityList) {
				responseStagingLineList.add(copyLineEntityToBean (stagingLineEntity));
			}
			
			// Update PreInboundLines
			List<PreInboundLineEntity> preInboundLineList = preInboundLineService.getPreInboundLine(preInboundNo);
			for (PreInboundLineEntity preInboundLineEntity : preInboundLineList) {
				// STATUS_ID - Hard Coded Value "13"
				preInboundLineEntity.setStatusId (13L);
			}
			List<PreInboundLineEntity> updatedList = preInboundLineRepository.saveAll(preInboundLineList);
			log.info("updated PreInboundLineEntity : " + updatedList);
			return responseStagingLineList;
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param stagingNo
	 * @param palletCode
	 * @param caseCode
	 * @param lineNo
	 * @param itemCode
	 * @param loginUserID
	 * @param updateStagingLineEntity
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StagingLineEntity updateStagingLine (String warehouseId, 
			String preInboundNo, String refDocNumber, String stagingNo, String palletCode, String caseCode, Long lineNo, 
			String itemCode, String loginUserID, UpdateStagingLine updateStagingLine) 
			throws IllegalAccessException, InvocationTargetException {
		StagingLineEntity dbStagingLineEntity = getStagingLine(warehouseId, preInboundNo, refDocNumber, stagingNo, palletCode, 
				caseCode, lineNo, itemCode);
		BeanUtils.copyProperties(updateStagingLine, dbStagingLineEntity, CommonUtils.getNullPropertyNames(updateStagingLine));
		dbStagingLineEntity.setUpdatedBy(loginUserID);
		dbStagingLineEntity.setUpdatedOn(new Date());
		return stagingLineEntityRepository.save(dbStagingLineEntity);
	}
	
	/**
	 * 
	 * @param assignHHTUsers
	 * @param assignedUserId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<StagingLineEntity> assignHHTUser (List<AssignHHTUser> assignHHTUsers, String assignedUserId,
			String loginUserID) throws IllegalAccessException, InvocationTargetException {
		List<StagingLineEntity> updatedStagingLineEntityList = new ArrayList<>();
		for (AssignHHTUser assignHHTUser : assignHHTUsers) {
			StagingLineEntity dbStagingLineEntity = getStagingLine(assignHHTUser.getWarehouseId(), assignHHTUser.getPreInboundNo(), 
					assignHHTUser.getRefDocNumber(), assignHHTUser.getStagingNo(), assignHHTUser.getPalletCode(), 
					assignHHTUser.getCaseCode(), assignHHTUser.getLineNo(), assignHHTUser.getItemCode());
			dbStagingLineEntity.setAssignedUserId(assignedUserId);
			dbStagingLineEntity.setUpdatedBy(loginUserID);
			dbStagingLineEntity.setUpdatedOn(new Date());
			StagingLineEntity updatedStagingLineEntity = stagingLineEntityRepository.save(dbStagingLineEntity);
			updatedStagingLineEntityList.add(updatedStagingLineEntity);
		}
		return updatedStagingLineEntityList;
	}
	
	/**
	 * 
	 * @param asnNumber
	 */
	public void updateASN (String asnNumber) {
		List<StagingLineEntity> StagingLineEntitys = getStagingLines();
		StagingLineEntitys.forEach(stagLines -> stagLines.setReferenceField1(asnNumber));
		stagingLineEntityRepository.saveAll(StagingLineEntitys);
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param stagingNo
	 * @param palletCode
	 * @param caseCode
	 * @param lineNo
	 * @param itemCode
	 * @param loginUserID
	 * @param updateStagingLineEntity
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<StagingLineEntity> caseConfirmation ( List<CaseConfirmation> caseConfirmations, String caseCode, 
			String loginUserID) throws IllegalAccessException, InvocationTargetException {
		log.info ("caseConfirmation--called----> : " + caseConfirmations);
		
		List<StagingLineEntity> updatedStagingLineEntityList = new ArrayList<>();
		for (CaseConfirmation caseConfirmation : caseConfirmations) {
			StagingLineEntity dbStagingLineEntity = getStagingLine(caseConfirmation.getWarehouseId(), 
					caseConfirmation.getPreInboundNo(), caseConfirmation.getRefDocNumber(), caseConfirmation.getStagingNo(), 
					caseConfirmation.getPalletCode(), caseConfirmation.getCaseCode(), caseConfirmation.getLineNo(), caseConfirmation.getItemCode());
			
			// update STATUS_ID value as 14
			dbStagingLineEntity.setStatusId(14L);
			dbStagingLineEntity.setCaseCode(caseCode);
			dbStagingLineEntity.setUpdatedBy(loginUserID);
			dbStagingLineEntity.setUpdatedOn(new Date());
			StagingLineEntity updatedStagingLineEntity = stagingLineEntityRepository.save(dbStagingLineEntity);
			log.info ("updatedStagingLineEntity------> : " + updatedStagingLineEntity);
			updatedStagingLineEntityList.add(updatedStagingLineEntity);
			
			if (updatedStagingLineEntity != null) {
				// STATUS updates
				/*
				 * Pass WH_ID/PRE_IB_NO/REF_DOC_NO/STG_NO/IB_LINE_NO/ITM_CODE/CASE_CODE values in StagingLineEntity table and 
				 * validate STATUS_ID , if all the STATUS_ID values of the CASE_CODE are 14				
				 */
				List<StagingLineEntity> stagingLineEntity = 
						stagingLineEntityRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndLineNoAndItemCodeAndCaseCodeAndDeletionIndicator(
						getLanguageId(), getCompanyCode(), getPlantId(), caseConfirmation.getWarehouseId(), caseConfirmation.getPreInboundNo(), 
						caseConfirmation.getRefDocNumber(), caseConfirmation.getStagingNo(), caseConfirmation.getLineNo(),
						caseConfirmation.getItemCode(), caseCode, 0L);
				
				boolean isStatus14 = false;
				List<Long> statusList = stagingLineEntity.stream().map(StagingLineEntity::getStatusId).collect(Collectors.toList());
				long statusIdCount = statusList.stream().filter(a -> a == 14L).count();
				log.info("status count : " + (statusIdCount == statusList.size()));
				
				isStatus14 = (statusIdCount == statusList.size());
			
				//-----logic for checking all records as 14 then only it should go to update header---issue--#5-------------
				if (!stagingLineEntity.isEmpty() && isStatus14) {
					/*
					 * 1. Then pass WH_ID/PRE_IB_NO/REF_DOC_NO/STG_NO in STAGINGHEADER table and 
					 * update the STATUS_ID as 14,ST_CNF_BY and ST_CNF_ON fields
					 */
					UpdateStagingHeader updateStagingHeader = new UpdateStagingHeader();
					updateStagingHeader.setStatusId(14L);
					StagingHeader stagingHeader = 
							stagingHeaderService.updateStagingHeader(caseConfirmation.getWarehouseId(), caseConfirmation.getPreInboundNo(), 
									caseConfirmation.getRefDocNumber(), caseConfirmation.getStagingNo(), loginUserID, 
									updateStagingHeader);
					log.info("stagingHeader : " + stagingHeader);
					
					/*
					 * 2. Then Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE in INBOUNDLINE table and 
					 * updated STATUS_ID as 14
					 */
					UpdateInboundLine updateInboundLine = new UpdateInboundLine();
					updateInboundLine.setStatusId(14L);
					InboundLine inboundLine = inboundLineService.updateInboundLine(caseConfirmation.getWarehouseId(), 
							caseConfirmation.getRefDocNumber(), caseConfirmation.getPreInboundNo(), caseConfirmation.getLineNo(), 
							caseConfirmation.getItemCode(), loginUserID, updateInboundLine);
					log.info("inboundLine : " + inboundLine);
				}
			}
		}
		
		// Record Insertion in GRHEADER table
		if (!updatedStagingLineEntityList.isEmpty()) {
			StagingLineEntity updatedStagingLineEntity = updatedStagingLineEntityList.get(0);
			log.info ("updatedStagingLineEntity-----IN---GRHEADER---CREATION---> : " + updatedStagingLineEntity);
			
			AddGrHeader addGrHeader = new AddGrHeader();
			BeanUtils.copyProperties(updatedStagingLineEntity, addGrHeader, CommonUtils.getNullPropertyNames(updatedStagingLineEntity));
			
			// GR_NO
			AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
			long NUM_RAN_CODE = 5;
			String nextGRHeaderNumber = getNextRangeNumber (NUM_RAN_CODE, updatedStagingLineEntity.getWarehouseId(), 
					authTokenForIDMasterService.getAccess_token());
			addGrHeader.setGoodsReceiptNo(nextGRHeaderNumber);
			
			// STATUS_ID
			addGrHeader.setStatusId(16L);			
			GrHeader createdGrHeader = grHeaderService.createGrHeader(addGrHeader, loginUserID);
			log.info("createdGrHeader : " + createdGrHeader);
		}
		
		return updatedStagingLineEntityList;
	}
	
	/**
	 * deleteStagingLineEntity
	 * @param loginUserID 
	 * @param palletCode
	 */
	public void deleteStagingLine (String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, 
			String palletCode, String caseCode, Long lineNo, String itemCode, String loginUserID) {
		StagingLineEntity StagingLineEntity = getStagingLine(warehouseId, preInboundNo, refDocNumber, stagingNo, palletCode, 
				caseCode, lineNo, itemCode);
		if ( StagingLineEntity != null) {
			StagingLineEntity.setDeletionIndicator(1L);
			StagingLineEntity.setUpdatedBy(loginUserID);
			stagingLineEntityRepository.save(StagingLineEntity);
		} else {
			throw new BadRequestException("Error in deleting Id:  warehouseId:" + warehouseId + 
					",refDocNumber: " + refDocNumber + "," +
					",preInboundNo: " + preInboundNo + "," +
					",stagingNo: " + stagingNo +
					",palletCode: " + palletCode + 
					",caseCode: " + caseCode + 
					",lineNo: " + lineNo + 
					",itemCode: " + itemCode + 
					" doesn't exist.");
		}
	}
	
	/**
	 * 
	 * @param preInboundNo
	 * @param stagingNo
	 * @param itemCode
	 * @param lineNo
	 * @param loginUserID
	 * @param loginUserID2 
	 */
	public void deleteCases (String preInboundNo, Long lineNo, String itemCode, String caseCode, String loginUserID) {
		List<Long> statusIds = new ArrayList<>();
		statusIds.add(13L);
		statusIds.add(14L);
		List<StagingLineEntity> StagingLineEntitys = 
				stagingLineEntityRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndPreInboundNoAndLineNoAndItemCodeAndStatusIdInAndDeletionIndicator (
				getLanguageId(),
				getCompanyCode(),
				getPlantId(),
				preInboundNo,
				lineNo,
				itemCode,
				statusIds,
				0L
				);
		if (StagingLineEntitys == null) {
			throw new BadRequestException("Error in deleting Id: " + 
					",preInboundNo: " + preInboundNo + "," +
					",caseCode: " + caseCode + 
					",lineNo: " + lineNo + 
					",itemCode: " + itemCode + 
					" doesn't exist.");
		}
		
		for (StagingLineEntity StagingLineEntity : StagingLineEntitys) {
			StagingLineEntity.setDeletionIndicator(1L);
			StagingLineEntity.setUpdatedBy(loginUserID);
			StagingLineEntity deletedStagingLineEntity = stagingLineEntityRepository.save(StagingLineEntity);
			log.info("deletedStagingLineEntity : " + deletedStagingLineEntity);	
		}
	}
	
	/**
	 * 
	 * @param stagingLineEntity
	 * @return
	 */
	private StagingLine copyLineEntityToBean(StagingLineEntity stagingLineEntity) {
		StagingLine stagingLine = new StagingLine();
		BeanUtils.copyProperties(stagingLineEntity, stagingLine, CommonUtils.getNullPropertyNames(stagingLineEntity));
		return stagingLine;
	}
}
