package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import com.tekclover.wms.api.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.transaction.model.inbound.gr.AddGrLine;
import com.tekclover.wms.api.transaction.model.inbound.gr.GrHeader;
import com.tekclover.wms.api.transaction.model.inbound.gr.GrLine;
import com.tekclover.wms.api.transaction.model.inbound.gr.PackBarcode;
import com.tekclover.wms.api.transaction.model.inbound.gr.SearchGrLine;
import com.tekclover.wms.api.transaction.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.transaction.model.inbound.gr.UpdateGrLine;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayHeader;
import com.tekclover.wms.api.transaction.model.inbound.staging.StagingLineEntity;
import com.tekclover.wms.api.transaction.repository.GrHeaderRepository;
import com.tekclover.wms.api.transaction.repository.GrLineRepository;
import com.tekclover.wms.api.transaction.repository.InboundLineRepository;
import com.tekclover.wms.api.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.PutAwayHeaderRepository;
import com.tekclover.wms.api.transaction.repository.StagingLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.GrLineSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GrLineService extends BaseService {
	
	@Autowired
	private GrLineRepository grLineRepository;
	
	@Autowired
	private GrHeaderRepository grHeaderRepository;
	
	@Autowired
	private PutAwayHeaderRepository putAwayHeaderRepository;
	
	@Autowired
	private InventoryRepository inventoryRepository;
	
	@Autowired
	private InventoryMovementRepository inventoryMovementRepository;
	
	@Autowired
	private InboundLineRepository inboundLineRepository;
	
	@Autowired
	private StagingLineRepository stagingLineRepository;
	
	@Autowired
	private GrHeaderService grHeaderService;
	
	@Autowired
	private InboundLineService inboundLineService;
	
	@Autowired
	private StagingLineService stagingLineService;
	
	@Autowired
	private AuthTokenService authTokenService;
	
	@Autowired
	private MastersService mastersService;
	
	private static final String WAREHOUSEID_111 = "111";
	
	/**
	 * getGrLines
	 * @return
	 */
	public List<GrLine> getGrLines () {
		List<GrLine> grLineList =  grLineRepository.findAll();
		grLineList = grLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return grLineList;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param goodsReceiptNo
	 * @param palletCode
	 * @param caseCode
	 * @param packBarcodes
	 * @param lineNo
	 * @param itemCode
	 * @return
	 */
	public GrLine getGrLine (String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo, 
			String palletCode, String caseCode, String packBarcodes, Long lineNo, String itemCode) {
		Optional<GrLine> grLine = grLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndPackBarcodesAndLineNoAndItemCodeAndDeletionIndicator(
										getLanguageId(),
										getCompanyCode(),
										getPlantId(),
										warehouseId, 
										preInboundNo, 
										refDocNumber, 
										goodsReceiptNo, 
										palletCode, 
										caseCode, 
										packBarcodes,
										lineNo,
										itemCode,
										0L);
		if (grLine.isEmpty()) {
			throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
				",refDocNumber: " + refDocNumber + "," +
				",preInboundNo: " + preInboundNo + "," +
				",packBarcodes: " + packBarcodes +
				",palletCode: " + palletCode + 
				",caseCode: " + caseCode + 
				",goodsReceiptNo: " + goodsReceiptNo + 
				",lineNo: " + lineNo + 
				",itemCode: " + itemCode + 
				" doesn't exist.");
		} 
		
		return grLine.get();
	}
	
	/**
	 * PRE_IB_NO/REF_DOC_NO/PACK_BARCODE/IB_LINE_NO/ITM_CODE
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param packBarcodes
	 * @param lineNo
	 * @param itemCode
	 * @return
	 */
	public List<GrLine> getGrLine (String preInboundNo, String refDocNumber, String packBarcodes, Long lineNo, String itemCode) {
		List<GrLine> grLine = 
				grLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndPreInboundNoAndRefDocNumberAndPackBarcodesAndLineNoAndItemCodeAndDeletionIndicator(
				getLanguageId(),
				getCompanyCode(),
				getPlantId(),
				preInboundNo, 
				refDocNumber, 
				packBarcodes,
				lineNo,
				itemCode,
				0L);
		if (grLine.isEmpty()) {
			throw new BadRequestException("The given values: " +
			",refDocNumber: " + refDocNumber + "," +
			",preInboundNo: " + preInboundNo + "," +
			",packBarcodes: " + packBarcodes +
			",lineNo: " + lineNo + 
			",itemCode: " + itemCode + 
			" doesn't exist.");
		} 
		
		return grLine;
	}
	
	/**
	 * 
	 * @param refDocNumber
	 * @param packBarcodes
	 * @param warehouseId
	 * @param preInboundNo
	 * @param caseCode
	 * @return
	 */
	public List<GrLine> getGrLine (String refDocNumber, String packBarcodes, String warehouseId, String preInboundNo, String caseCode) {
		List<GrLine> grLine = 
				grLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndDeletionIndicator(
				getLanguageId(),
				getCompanyCode(),
				getPlantId(),
				refDocNumber,
				packBarcodes,
				warehouseId, 
				preInboundNo,
				caseCode,
				0L);
		if (grLine.isEmpty()) {
			throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
					",refDocNumber: " + refDocNumber + "," +
					",packBarcodes: " + packBarcodes + "," +
					",preInboundNo: " + preInboundNo + "," +
					",caseCode: " + caseCode + 
					" doesn't exist.");
		} 
		
		return grLine;
	}
	
	/**
	 * 
	 * @param refDocNumber
	 * @param packBarcodes
	 * @return
	 */
	public List<GrLine> getGrLine (String refDocNumber, String packBarcodes) {
		List<GrLine> grLine = 
				grLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
				getLanguageId(),
				getCompanyCode(),
				getPlantId(),
				refDocNumber,
				packBarcodes,
				0L);
		if (grLine.isEmpty()) {
			throw new BadRequestException("The given values: " + 
					",refDocNumber: " + refDocNumber + "," +
					",packBarcodes: " + packBarcodes + "," +
					" doesn't exist in GRLine.");
		} 
		
		return grLine;
	}
	
	/**
	 * 
	 * @param searchGrLine
	 * @return
	 * @throws ParseException
	 */
	public List<GrLine> findGrLine(SearchGrLine searchGrLine) throws ParseException {
		GrLineSpecification spec = new GrLineSpecification(searchGrLine);
		List<GrLine> results = grLineRepository.findAll(spec);
//		log.info("results: " + results);
		return results;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param lineNo
	 * @param itemCode
	 * @return
	 */
	public List<GrLine> getGrLineForUpdate (String warehouseId, String preInboundNo, String refDocNumber, Long lineNo, String itemCode) {
		List<GrLine> grLine = 
				grLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndDeletionIndicator(
				getLanguageId(),
				getCompanyCode(),
				getPlantId(),
				warehouseId,
				preInboundNo, 
				refDocNumber, 
				lineNo,
				itemCode,
				0L);
		if (grLine.isEmpty()) {
			throw new BadRequestException("The given values: " +
					",warehouseId: " + warehouseId + 
					",refDocNumber: " + refDocNumber + 
					",preInboundNo: " + preInboundNo + 
					",lineNo: " + lineNo + 
					",itemCode: " + itemCode + 
					" doesn't exist.");
		} 
		
		return grLine;
	}
	
	/**
	 * 
	 * @param acceptQty
	 * @param damageQty
	 * @param loginUserID
	 * @return
	 */
	public List<PackBarcode> generatePackBarcode(Long acceptQty, Long damageQty, String warehouseId, String loginUserID) {
		AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
//		UserManagement userManagement = 
//				idmasterService.getUserManagement(loginUserID, authTokenForIDMasterService.getAccess_token());
		
		long NUM_RAN_ID = 6;
//		String warehouseId = userManagement.getWarehouseId();
		
		List<PackBarcode> packBarcodes = new ArrayList<>();
		// Accept Qty
		if (acceptQty != 0) {
			String nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, warehouseId, authTokenForIDMasterService.getAccess_token());
			PackBarcode acceptQtyPackBarcode = new PackBarcode();
			acceptQtyPackBarcode.setQuantityType("A");
			acceptQtyPackBarcode.setBarcode(nextRangeNumber);
			packBarcodes.add(acceptQtyPackBarcode);
		}
		
		// Damage Qty
		if (damageQty != 0) {
			String nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, warehouseId, authTokenForIDMasterService.getAccess_token());
			PackBarcode damageQtyPackBarcode = new PackBarcode();
			damageQtyPackBarcode.setQuantityType("D");
			damageQtyPackBarcode.setBarcode(nextRangeNumber);
			packBarcodes.add(damageQtyPackBarcode);
		}
		return packBarcodes;
	}
	
	/**
	 * createGrLine
	 * @param newGrLine
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<GrLine> createGrLine (@Valid List<AddGrLine> newGrLines, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		List<GrLine> createdGRLines = new ArrayList<>();
		try {
			// Inserting multiple records
			for (AddGrLine newGrLine : newGrLines) {
				
				/*------------Inserting based on the PackBarcodes -----------*/
				for (PackBarcode packBarcode : newGrLine.getPackBarcodes()) {
					GrLine dbGrLine = new GrLine();
					log.info("newGrLine : " + newGrLine);
					BeanUtils.copyProperties(newGrLine, dbGrLine, CommonUtils.getNullPropertyNames(newGrLine));
					dbGrLine.setCompanyCode(newGrLine.getCompanyCode());
					
					// GR_QTY
					if (packBarcode.getQuantityType().equalsIgnoreCase("A")) {
						Double grQty = newGrLine.getAcceptedQty();
						dbGrLine.setGoodReceiptQty(grQty);
						dbGrLine.setAcceptedQty(grQty);
						dbGrLine.setDamageQty(0D);
						log.info("A-------->: " + dbGrLine);
					} else if (packBarcode.getQuantityType().equalsIgnoreCase("D")) {
						Double grQty = newGrLine.getDamageQty();
						dbGrLine.setGoodReceiptQty(grQty);
						dbGrLine.setDamageQty(newGrLine.getDamageQty());
						dbGrLine.setAcceptedQty(0D);
						log.info("D-------->: " + dbGrLine);
					}
					
					dbGrLine.setQuantityType (packBarcode.getQuantityType());
					dbGrLine.setPackBarcodes(packBarcode.getBarcode());
					dbGrLine.setStatusId(17L);
					dbGrLine.setDeletionIndicator(0L);
					dbGrLine.setCreatedBy(loginUserID);
					dbGrLine.setUpdatedBy(loginUserID);
					dbGrLine.setCreatedOn(new Date());
					dbGrLine.setUpdatedOn(new Date());
					GrLine createdGRLine = grLineRepository.save(dbGrLine);
					log.info("createdGRLine : " + createdGRLine);
					createdGRLines.add(createdGRLine);
					
					if (createdGRLine != null) {
						// Record Insertion in PUTAWAYHEADER table 
						createPutAwayHeader (createdGRLine, loginUserID);
					}
				}
				log.info("Records were inserted successfully...");
			}
			
			// STATUS updates
			/*
			 * Pass WH_ID/PRE_IB_NO/REF_DOC_NO/GR_NO/IB_LINE_NO/ITM_CODE in GRLINE table and 
			 * validate STATUS_ID of the all the filtered line items = 17 , if yes
			 */
			for (GrLine grLine : createdGRLines) {
				/*
				 * 1. Update GRHEADER table with STATUS_ID=17 by Passing WH_ID/GR_NO/CASE_CODE/REF_DOC_NO and 
				 * GR_CNF_BY with USR_ID and GR_CNF_ON with Server time
				 */
				List<GrHeader> grHeaders = grHeaderService.getGrHeader(grLine.getWarehouseId(), grLine.getGoodsReceiptNo(), 
						grLine.getCaseCode(), grLine.getRefDocNumber());
				for (GrHeader grHeader : grHeaders) {
					if (grHeader.getCompanyCode() == null) {
						grHeader.setCompanyCode(getCompanyCode());
					} 
					grHeader.setStatusId(17L);
					grHeader.setCreatedBy(loginUserID);	
					grHeader.setCreatedOn(new Date());
					grHeader = grHeaderRepository.save(grHeader);
					log.info("grHeader updated: " + grHeader);
				}
				
				/*
				 * '2. 'Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE/CASECODE in STAGINIGLINE table and 
				 * update STATUS_ID as 17 
				 */
				List<StagingLineEntity> stagingLineEntityList = 
						stagingLineService.getStagingLine(grLine.getWarehouseId(), grLine.getRefDocNumber(), grLine.getPreInboundNo(), 
						grLine.getLineNo(), grLine.getItemCode(), grLine.getCaseCode());
				for (StagingLineEntity stagingLineEntity : stagingLineEntityList) {
					stagingLineEntity.setStatusId(17L);
					stagingLineEntity = stagingLineRepository.save(stagingLineEntity);
					log.info("stagingLineEntity updated: " + stagingLineEntity);
				}
				
				/*
				 * 3. Then Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE in INBOUNDLINE table and 
				 * updated STATUS_ID as 17
				 */
				InboundLine inboundLine = inboundLineService.getInboundLine(grLine.getWarehouseId(),
						grLine.getRefDocNumber(), grLine.getPreInboundNo(), grLine.getLineNo(), grLine.getItemCode());
				inboundLine.setStatusId(17L);
				inboundLine = inboundLineRepository.save(inboundLine);
				log.info("inboundLine updated : " + inboundLine);
			}
				
			return createdGRLines;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 
	 * @param createdGRLine
	 * @param loginUserID
	 */
	private void createPutAwayHeader (GrLine createdGRLine, String loginUserID) {
		//  ASS_HE_NO
		if (createdGRLine != null && createdGRLine.getAssignedUserId() != null) {
			// Insert record into PutAwayHeader
			//private Double putAwayQuantity, private String putAwayUom;
			PutAwayHeader putAwayHeader = new PutAwayHeader();
			BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
			putAwayHeader.setCompanyCodeId(createdGRLine.getCompanyCode());
			
			// PA_NO
			AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
//			UserManagement userManagement = idmasterService.getUserManagement(loginUserID, authTokenForIDMasterService.getAccess_token());
			
			long NUM_RAN_CODE = 7;
			String nextPANumber = getNextRangeNumber (NUM_RAN_CODE, createdGRLine.getWarehouseId(), authTokenForIDMasterService.getAccess_token());
			putAwayHeader.setPutAwayNumber(nextPANumber);
			
			putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
			putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
			
			//-----------------PROP_ST_BIN---------------------------------------------
			/*
			 * 1. Fetch ITM_CODE from GRLINE table and Pass WH_ID/ITM_CODE/BIN_CLASS_ID=1 in INVENTORY table and Fetch ST_BIN values. 
			 * Pass ST_BIN values into STORAGEBIN table  where ST_SEC_ID = ZB,ZG,ZD,ZC,ZT and PUTAWAY_BLOCK and PICK_BLOCK columns are Null( FALSE) and 
			 * fetch the filtered values and sort the latest and insert.  
			 * 
			 * If WH_ID=111, fetch ST_BIN values of ST_SEC_ID= ZT and sort the latest and insert
			 */
			List<String> storageSectionIds = Arrays.asList("ZB","ZG","ZD","ZC","ZT");
			
			// Discussed to remove this condition
//			if (createdGRLine.getWarehouseId().equalsIgnoreCase(WAREHOUSEID_111)) {
//				storageSectionIds = Arrays.asList("ZT");
//			} 
			List<Inventory> stBinInventoryList = 
					inventoryRepository.findByWarehouseIdAndItemCodeAndBinClassIdAndDeletionIndicator(createdGRLine.getWarehouseId(), 
							createdGRLine.getItemCode(), 1L, 0L);
			log.info("stBinInventoryList -----------> : " + stBinInventoryList);
			
			AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
			if (!stBinInventoryList.isEmpty()) {
				List<String> stBins = stBinInventoryList.stream().map(Inventory::getStorageBin).collect(Collectors.toList());
				log.info("stBins -----------> : " + stBins);
				
				StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
				storageBinPutAway.setStorageBin(stBins);
				storageBinPutAway.setStorageSectionIds(storageSectionIds);
				StorageBin[] storageBin = mastersService.getStorageBin(storageBinPutAway, authTokenForMastersService.getAccess_token());
				putAwayHeader.setProposedStorageBin(storageBin[0].getStorageBin());
			} else {
				// If ST_BIN value is null 
				// Validate if ACCEPT_QTY is not null and DAMAGE_QTY is NULL, 
				// then pass WH_ID in STORAGEBIN table and fetch ST_BIN values for STATUS_ID=EMPTY.
				log.info ("QuantityType : " + createdGRLine.getQuantityType());
				if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
					if (createdGRLine.getWarehouseId().equalsIgnoreCase(WAREHOUSEID_111)) {
						storageSectionIds = Arrays.asList("ZT");
					} 
					StorageBin[] storageBinEMPTY = 
							mastersService.getStorageBinByStatus(createdGRLine.getWarehouseId(), 0L, authTokenForMastersService.getAccess_token());
					List<StorageBin> storageBinEMPTYList = Arrays.asList(storageBinEMPTY);				
					List<String> stBins = storageBinEMPTYList.stream().map(StorageBin::getStorageBin).collect(Collectors.toList());
					/*
					 * Pass ST_BIN values into STORAGEBIN table  where where ST_SEC_ID = ZB,ZG,ZD,ZC,ZT and 
					 * PUTAWAY_BLOCK and PICK_BLOCK columns are Null( FALSE) and fetch the filteerd values and 
					 * Sort the latest and Insert.
					 */
					storageSectionIds = Arrays.asList("ZB","ZG","ZC","ZT"); // Removing ZD
					StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
					storageBinPutAway.setStorageBin(stBins);
					storageBinPutAway.setStorageSectionIds(storageSectionIds);
					
					StorageBin[] storageBin = mastersService.getStorageBin(storageBinPutAway, authTokenForMastersService.getAccess_token());
					if (storageBin != null && storageBin.length > 0) {
						putAwayHeader.setProposedStorageBin(storageBin[0].getStorageBin());
					} else {
						Long binClassID = 2L;
						StorageBin stBin = mastersService.getStorageBin(createdGRLine.getWarehouseId(), binClassID, authTokenForMastersService.getAccess_token());
						putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
					}
				}
				
				/*
				 * Validate if ACCEPT_QTY is null and DAMAGE_QTY is not NULL , then pass WH_ID in STORAGEBIN table and 
				 * fetch ST_BIN values for STATUS_ID=EMPTY. 
				 */
				if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
					storageSectionIds = Arrays.asList("ZD");
					StorageBin[] storageBinEMPTY = 
							mastersService.getStorageBinByStatus(createdGRLine.getWarehouseId(), 0L, authTokenForMastersService.getAccess_token());
					List<StorageBin> storageBinEMPTYList = Arrays.asList(storageBinEMPTY);				
					List<String> stBins = storageBinEMPTYList.stream().map(StorageBin::getStorageBin).collect(Collectors.toList());
					
					// Pass ST_BIN values into STORAGEBIN table  where where ST_SEC_ID = ZD and PUTAWAY_BLOCK and 
					// PICK_BLOCK columns are Null( FALSE) 
					StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
					storageBinPutAway.setStorageBin(stBins);
					storageBinPutAway.setStorageSectionIds(storageSectionIds);
					StorageBin[] storageBin = mastersService.getStorageBin(storageBinPutAway, authTokenForMastersService.getAccess_token());
					if (storageBin != null && storageBin.length > 0) {
						putAwayHeader.setProposedStorageBin(storageBin[0].getStorageBin());
					} else {
						Long binClassID = 2L;
						StorageBin stBin = mastersService.getStorageBin(createdGRLine.getWarehouseId(), binClassID, authTokenForMastersService.getAccess_token());
						putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
					}
				}
			}
			
			/////////////////////////////////////////////////////////////////////////////////////////////////////
			
			//PROP_HE_NO	<- PAWAY_HE_NO
			putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
			putAwayHeader.setStatusId(19L);
			putAwayHeader.setDeletionIndicator(0L);
			putAwayHeader.setCreatedBy(loginUserID);	
			putAwayHeader.setCreatedOn(new Date());
			putAwayHeader.setUpdatedBy(loginUserID);
			putAwayHeader.setUpdatedOn(new Date());
			putAwayHeader = putAwayHeaderRepository.save(putAwayHeader);
			log.info("putAwayHeader : " + putAwayHeader);	
			
			/*----------------Inventory tables Create---------------------------------------------*/
			Inventory createdinventory = createInventory (createdGRLine);
			
			/*----------------INVENTORYMOVEMENT table Update---------------------------------------------*/
			createInventoryMovement (createdGRLine, createdinventory);
		}
	}
	
	/**
	 * 
	 * @param createdGRLine
	 * @param createdinventory 
	 */
	private void createInventoryMovement(GrLine createdGRLine, Inventory createdinventory) {
		InventoryMovement inventoryMovement = new InventoryMovement();
		BeanUtils.copyProperties(createdGRLine, inventoryMovement, CommonUtils.getNullPropertyNames(createdGRLine));
		inventoryMovement.setCompanyCodeId(createdGRLine.getCompanyCode());
		
		// MVT_TYP_ID
		inventoryMovement.setMovementType(1L);
		
		// SUB_MVT_TYP_ID
		inventoryMovement.setSubmovementType(1L);
		
		// STR_MTD
		inventoryMovement.setStorageMethod("1");
		
		// STR_NO
		inventoryMovement.setBatchSerialNumber("1");
		
		// MVT_DOC_NO
		inventoryMovement.setMovementDocumentNo(createdGRLine.getGoodsReceiptNo());
		
		// ST_BIN
		inventoryMovement.setStorageBin(createdinventory.getStorageBin());
		
		// MVT_QTY
		inventoryMovement.setMovementQty(createdGRLine.getGoodReceiptQty());
		
		// MVT_QTY_VAL
		inventoryMovement.setMovementQtyValue("P");
		
		// MVT_UOM
		inventoryMovement.setInventoryUom(createdGRLine.getOrderUom());
		
		inventoryMovement.setVariantCode(1L);
		inventoryMovement.setVariantSubCode("1");
		
		// IM_CTD_BY
		inventoryMovement.setCreatedBy(createdGRLine.getCreatedBy());
		
		// IM_CTD_ON
		inventoryMovement.setCreatedOn(createdGRLine.getCreatedOn());
		inventoryMovement = inventoryMovementRepository.save(inventoryMovement);
		log.info("inventoryMovement : " + inventoryMovement);
	}

	/**
	 * 
	 * @param createdGRLine
	 * @return
	 */
	private Inventory createInventory (GrLine createdGRLine) {
		Inventory inventory = new Inventory();
		BeanUtils.copyProperties(createdGRLine, inventory, CommonUtils.getNullPropertyNames(createdGRLine));
		inventory.setCompanyCodeId(createdGRLine.getCompanyCode());
		
		// VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
		inventory.setVariantCode(1L);	
		inventory.setVariantSubCode("1");
		inventory.setStorageMethod("1");
		inventory.setBatchSerialNumber("1");
		inventory.setBinClassId(3L);	
		
		// ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		StorageBin storageBin = mastersService.getStorageBin(createdGRLine.getWarehouseId(), 3L, authTokenForMastersService.getAccess_token());
		inventory.setStorageBin(storageBin.getStorageBin());
		
		// STCK_TYP_ID
		inventory.setStockTypeId(1L);
		
		// SP_ST_IND_ID
		inventory.setSpecialStockIndicatorId(1L);	
		
		// INV_QTY
		inventory.setInventoryQuantity(createdGRLine.getGoodReceiptQty());
		
		// INV_UOM
		inventory.setInventoryUom(createdGRLine.getOrderUom());
		inventory.setCreatedBy(createdGRLine.getCreatedBy());
		inventory.setCreatedOn(createdGRLine.getCreatedOn());
		Inventory createdinventory = inventoryRepository.save(inventory);
		log.info("created inventory : " + createdinventory);
		return createdinventory;
	}
	
	/**
	 * updateGrLine
	 * @param loginUserId 
	 * @param itemCode
	 * @param updateGrLine
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public GrLine updateGrLine (String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo, String palletCode, String caseCode, String packBarcodes, Long lineNo, String itemCode, 
			String loginUserID, UpdateGrLine updateGrLine) 
			throws IllegalAccessException, InvocationTargetException {
		GrLine dbGrLine = getGrLine(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode, 
				packBarcodes, lineNo, itemCode);
		BeanUtils.copyProperties(updateGrLine, dbGrLine, CommonUtils.getNullPropertyNames(updateGrLine));
		dbGrLine.setUpdatedBy(loginUserID);
		dbGrLine.setUpdatedOn(new Date());
		GrLine updatedGrLine = grLineRepository.save(dbGrLine);
		return updatedGrLine;
	}
	
	/**
	 * 
	 * @param asnNumber
	 */
	public void updateASN (String asnNumber) {
		List<GrLine> grLines = getGrLines();
		grLines.forEach(g -> g.setReferenceField1(asnNumber));
		grLineRepository.saveAll(grLines);
	}
	
	/**
	 * deleteGrLine
	 * @param loginUserID 
	 * @param itemCode
	 */
	public void deleteGrLine (String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo, 
			String palletCode, String caseCode, String packBarcodes, Long lineNo, String itemCode, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		GrLine grLine = getGrLine(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode, 
				packBarcodes, lineNo, itemCode);
		if ( grLine != null) {
			grLine.setDeletionIndicator(1L);
			grLine.setUpdatedBy(loginUserID);
			grLineRepository.save(grLine);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: warehouseId:" + warehouseId + 
					",refDocNumber: " + refDocNumber + "," +
					",preInboundNo: " + preInboundNo + "," +
					",packBarcodes: " + packBarcodes +
					",palletCode: " + palletCode + 
					",caseCode: " + caseCode + 
					",goodsReceiptNo: " + goodsReceiptNo + 
					",lineNo: " + lineNo + 
					",itemCode: " + itemCode + 
					" doesn't exist.");
		}
	}
}
