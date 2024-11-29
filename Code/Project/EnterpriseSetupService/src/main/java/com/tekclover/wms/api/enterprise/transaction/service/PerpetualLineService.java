package com.tekclover.wms.api.enterprise.transaction.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.enterprise.transaction.model.cyclecount.perpetual.*;
import com.tekclover.wms.api.enterprise.transaction.model.dto.IImbasicData1;
import com.tekclover.wms.api.enterprise.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.enterprise.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.enterprise.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.enterprise.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.enterprise.transaction.repository.ImBasicData1Repository;
import com.tekclover.wms.api.enterprise.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.enterprise.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.enterprise.transaction.repository.PerpetualLineRepository;
import com.tekclover.wms.api.enterprise.transaction.repository.specification.PerpetualLineSpecification;
import com.tekclover.wms.api.enterprise.transaction.util.CommonUtils;
import com.tekclover.wms.api.enterprise.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class PerpetualLineService extends BaseService {
	
	private static final String WRITEOFF = "WRITEOFF";
	private static final String SKIP = "SKIP";
	private static final String RECOUNT = "RECOUNT";

	@Autowired
	PerpetualLineRepository perpetualLineRepository;
	
	@Autowired
	InventoryRepository inventoryRepository;
	
	@Autowired
	InventoryMovementRepository inventoryMovementRepository;
	
	@Autowired
	AuthTokenService authTokenService;
	
	@Autowired
	InventoryService inventoryService;
	
	@Autowired
	MastersService mastersService;
	
	@Autowired
	PerpetualHeaderService perpetualHeaderService;
	
	@Autowired
	PerpetualLineService perpetualLineService;

	@Autowired
	ImBasicData1Repository imbasicdata1Repository;
	
	/**
	 * getPerpetualLines
	 * @return
	 */
	public List<PerpetualLine> getPerpetualLines () {
		List<PerpetualLine> perpetualLineList =  perpetualLineRepository.findAll();
		perpetualLineList = perpetualLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return perpetualLineList;
	}
	
	/**
	 * getPerpetualLine
	 * @param cycleCountNo
	 * @return
	 */
	public PerpetualLine getPerpetualLine (String warehouseId, String cycleCountNo, 
		String storageBin, String itemCode, String packBarcodes) {
		PerpetualLine perpetualLine = 
				perpetualLineRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountNoAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator(
						getCompanyCode(), getPlantId(), warehouseId, cycleCountNo, storageBin, itemCode, 
						packBarcodes, 0L);
		if (perpetualLine == null) {
			throw new BadRequestException("The given PerpetualLine ID - "
					+ " warehouseId: " + warehouseId + ","
					+ "cycleCountNo: " + cycleCountNo + "," 
					+ "storageBin: " + storageBin + ","
					+ "itemCode: " + itemCode + ","
					+ "packBarcodes: " + packBarcodes + ","					
					+ " doesn't exist.");
		} 
		return perpetualLine;
	}
	
	/**
	 * 
	 * @param cycleCountNo
	 * @return
	 */
	public List<PerpetualLine> getPerpetualLine (String cycleCountNo) {
		List<PerpetualLine> perpetualLine = perpetualLineRepository.findByCycleCountNoAndDeletionIndicator(cycleCountNo, 0L);
		return perpetualLine;
	}
	
	/**
	 * 
	 * @param cycleCountNo
	 * @param cycleCounterId
	 * @return
	 */
	public List<PerpetualLine> getPerpetualLine (String cycleCountNo, List<String> cycleCounterId) {
		List<PerpetualLine> perpetualLine = 
				perpetualLineRepository.findByCycleCountNoAndCycleCounterIdInAndDeletionIndicator(cycleCountNo, cycleCounterId, 0L);
		return perpetualLine;
	}
	
	/**
	 * 
	 * @param searchPerpetualLine
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	public List<PerpetualLine> findPerpetualLine (SearchPerpetualLine searchPerpetualLine)
			throws ParseException, java.text.ParseException {		
		if (searchPerpetualLine.getStartCreatedOn() != null && searchPerpetualLine.getStartCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPerpetualLine.getStartCreatedOn(),
															 searchPerpetualLine.getEndCreatedOn());
			searchPerpetualLine.setStartCreatedOn(dates[0]);
			searchPerpetualLine.setEndCreatedOn(dates[1]);
		}
		
		PerpetualLineSpecification spec = new PerpetualLineSpecification(searchPerpetualLine);
		List<PerpetualLine> perpetualLineResults = perpetualLineRepository.findAll(spec);
		return perpetualLineResults;
	}

	/**
	 * Stream
	 * @param searchPerpetualLine
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	public Stream<PerpetualLine> findPerpetualLineStream (SearchPerpetualLine searchPerpetualLine)
			throws ParseException, java.text.ParseException {
		if (searchPerpetualLine.getStartCreatedOn() != null && searchPerpetualLine.getStartCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPerpetualLine.getStartCreatedOn(),
					searchPerpetualLine.getEndCreatedOn());
			searchPerpetualLine.setStartCreatedOn(dates[0]);
			searchPerpetualLine.setEndCreatedOn(dates[1]);
		}

		PerpetualLineSpecification spec = new PerpetualLineSpecification(searchPerpetualLine);
		Stream<PerpetualLine> perpetualLineResults = perpetualLineRepository.stream(spec, PerpetualLine.class);
		return perpetualLineResults;
	}
	
	/**
	 * createPerpetualLine
	 * @param newPerpetualLine
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PerpetualLine createPerpetualLine (AddPerpetualLine newPerpetualLine, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		PerpetualLine dbPerpetualLine = new PerpetualLine();
		log.info("newPerpetualLine : " + newPerpetualLine);
		BeanUtils.copyProperties(newPerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(newPerpetualLine));
		dbPerpetualLine.setDeletionIndicator(0L);
		dbPerpetualLine.setCreatedBy(loginUserID);
		dbPerpetualLine.setCreatedOn(new Date());
		dbPerpetualLine.setCountedBy(loginUserID);
		dbPerpetualLine.setCountedOn(new Date());
		return perpetualLineRepository.save(dbPerpetualLine);
	}
	
	/**
	 * 
	 * @param newPerpetualLines
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<PerpetualLine> createPerpetualLine (List<AddPerpetualLine> newPerpetualLines, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		List<PerpetualLine> newPerpetualLineList = new ArrayList<>();
		for (AddPerpetualLine newPerpetualLine : newPerpetualLines) {
			PerpetualLine dbPerpetualLine = new PerpetualLine();
			BeanUtils.copyProperties(newPerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(newPerpetualLine));
			dbPerpetualLine.setDeletionIndicator(0L);
			dbPerpetualLine.setCreatedBy(loginUserID);
			dbPerpetualLine.setCreatedOn(new Date());
			dbPerpetualLine.setCountedBy(loginUserID);
			dbPerpetualLine.setCountedOn(new Date());
			newPerpetualLineList.add(dbPerpetualLine);
		}
		
		return perpetualLineRepository.saveAll(newPerpetualLineList);
	}
	
	/**
	 * updateAssingHHTUser
	 * @param assignHHTUser
	 * @param loginUserID
	 * @return
	 */
	public List<PerpetualLine> updateAssingHHTUser (List<AssignHHTUserCC> assignHHTUsers, String loginUserID) {
		List<PerpetualLine> responseList = new ArrayList<>();
		for (AssignHHTUserCC assignHHTUser : assignHHTUsers) {
			perpetualLineRepository.updateHHTUser(assignHHTUser.getCycleCounterId(), 
					assignHHTUser.getCycleCounterName(), 72L, loginUserID, new Date(), assignHHTUser.getWarehouseId(), assignHHTUser.getCycleCountNo(), 
					assignHHTUser.getStorageBin(), assignHHTUser.getItemCode(), assignHHTUser.getPackBarcodes());
		}
		return responseList;
	}
	
	/**
	 * updatePerpetualLine
	 * @param loginUserId 
	 * @param cycleCountNo
	 * @param updatePerpetualLine
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<PerpetualLine> updatePerpetualLineForMobileCount (List<UpdatePerpetualLine> updatePerpetualLines,
			String loginUserID) throws IllegalAccessException, InvocationTargetException {
		List<PerpetualLine> responsePerpetualLines = new ArrayList<>();
		
		List<AddPerpetualLine> createBatchPerpetualLines = new ArrayList<>();
		List<PerpetualLine> updateBatchPerpetualLines = new ArrayList<>();
		for (UpdatePerpetualLine updatePerpetualLine : updatePerpetualLines) {
			PerpetualLine dbPerpetualLine = getPerpetualLine(updatePerpetualLine.getWarehouseId(), updatePerpetualLine.getCycleCountNo(), 
					updatePerpetualLine.getStorageBin(), updatePerpetualLine.getItemCode(), updatePerpetualLine.getPackBarcodes());
			if (dbPerpetualLine != null) { /* Update */
				BeanUtils.copyProperties(updatePerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(updatePerpetualLine));
				
				// INV_QTY
				double INV_QTY = updatePerpetualLine.getInventoryQuantity();
				dbPerpetualLine.setInventoryQuantity(INV_QTY);
				
				// CTD_QTY
				if(updatePerpetualLine.getCountedQty() != null ) {
					double CTD_QTY = updatePerpetualLine.getCountedQty();
					dbPerpetualLine.setCountedQty(CTD_QTY);

					// VAR_QTY = INV_QTY - CTD_QTY
					double VAR_QTY = INV_QTY - CTD_QTY;
					dbPerpetualLine.setVarianceQty(VAR_QTY);

					/*
					 * HardCoded Value "78" if VAR_QTY = 0 and
					 * Hardcodeed value"74" - if VAR_QTY is greater than or less than Zero
					 */
					if (VAR_QTY == 0) {
						dbPerpetualLine.setStatusId(78L);
					} else if (VAR_QTY > 0 || VAR_QTY < 0) {
						dbPerpetualLine.setStatusId(74L);
					}
				}
				
				dbPerpetualLine.setCountedBy(loginUserID);
				dbPerpetualLine.setCountedOn(new Date());
				updateBatchPerpetualLines.add(dbPerpetualLine);
			} else {
				// Create new Record
				AddPerpetualLine newPerpetualLine = new AddPerpetualLine ();
				BeanUtils.copyProperties(updatePerpetualLine, newPerpetualLine, CommonUtils.getNullPropertyNames(updatePerpetualLine));
				newPerpetualLine.setCycleCountNo(updatePerpetualLine.getCycleCountNo());
				newPerpetualLine.setDeletionIndicator(0L);
				newPerpetualLine.setCreatedBy(loginUserID);
				newPerpetualLine.setCreatedOn(new Date());
				newPerpetualLine.setCountedBy(loginUserID);
				newPerpetualLine.setCountedOn(new Date());
				createBatchPerpetualLines.add(newPerpetualLine);
			}
		}
		
		responsePerpetualLines.addAll(createPerpetualLine (createBatchPerpetualLines, loginUserID));
		responsePerpetualLines.addAll(perpetualLineRepository.saveAll (updateBatchPerpetualLines));
		return responsePerpetualLines;
	}

	/**
	 * 
	 * @param cycleCountNo
	 * @param updatePerpetualLines
	 * @param loginUserID
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public PerpetualUpdateResponse updatePerpetualLine(String cycleCountNo, List<UpdatePerpetualLine> updatePerpetualLines,
			String loginUserID) throws IllegalAccessException, InvocationTargetException {
		List<PerpetualLine> responsePerpetualLines = new ArrayList<>();
		List<AddPerpetualLine> newPerpetualLines = new ArrayList<>();
		try {
			for (UpdatePerpetualLine updatePerpetualLine : updatePerpetualLines) {
				PerpetualLine dbPerpetualLine = getPerpetualLine(updatePerpetualLine.getWarehouseId(), 
						updatePerpetualLine.getCycleCountNo(), 
						updatePerpetualLine.getStorageBin(), 
						updatePerpetualLine.getItemCode(), 
						updatePerpetualLine.getPackBarcodes());
				BeanUtils.copyProperties(updatePerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(updatePerpetualLine));
				dbPerpetualLine.setRemarks(updatePerpetualLine.getRemarks());
				dbPerpetualLine.setCycleCountAction(updatePerpetualLine.getCycleCountAction());	
				
				/*
				 * 1. Action = WRITEOFF 
				 * If ACTION = WRITEOFF , update ACTION field in PERPETUALLINE as WRITEOFF by passing unique fields and 
				 * update in STATUS_ID field as "78"
				 */
				if (updatePerpetualLine.getCycleCountAction().equalsIgnoreCase(WRITEOFF)) {
					dbPerpetualLine.setStatusId(78L);
					dbPerpetualLine.setCycleCountAction(WRITEOFF);
					PerpetualLine updatedPerpetualLine = perpetualLineRepository.save(dbPerpetualLine);
					log.info("updatedPerpetualLine : " + updatedPerpetualLine);
					responsePerpetualLines.add(updatedPerpetualLine);
					
					/*
					 * Inventory table update
					 * ---------------------------
					 * Fetch CNT_QTY of the selected ITM_CODE and Pass WH_ID/ITM_CODE/ST_BIN/PACK_BARCODE values in INVENTORY table 
					 * and replace INV_QTY as CNT_QTY
					 */
					updateInventory (updatedPerpetualLine);
					createInventoryMovement (updatedPerpetualLine) ;
				}
				
				
				/*
				 * 2. Action = SKIP
				 * if ACTION = SKIP in UI,  update ACTION field in PERPETUALLINE as SKIP by passing unique fields 
				 * and update in STATUS_ID field as "78"
				 */
				if (updatePerpetualLine.getCycleCountAction().equalsIgnoreCase(SKIP)) {
					dbPerpetualLine.setStatusId(78L);
					dbPerpetualLine.setCycleCountAction(SKIP);
					PerpetualLine updatedPerpetualLine = perpetualLineRepository.save(dbPerpetualLine);
					log.info("updatedPerpetualLine : " + updatedPerpetualLine);
					responsePerpetualLines.add(updatedPerpetualLine);
					
					/*
					 * Inventory table update
					 * ---------------------------
					 * Insert a new record by passing WH_ID/ITM_CODE/PACK_BARCODE/ST_BIN (fetch ST_BIN 
					 * from STORAGEBIN table where BIN_CL_ID=5) values in INVENTORY table and append INV_QTY as 
					 * VAR_QTY
					 */
//					createInventory (updatedPerpetualLine);
//					createInventoryMovement (updatedPerpetualLine);
				}
				
				/*
				 * 3. Action = RECOUNT (default Action Value)
				 * If ACTION = RECOUNT, update ACTION field in PERPETUALLINE as SKIP by passing unique fields 
				 * and update in STATUS_ID field as "78"
				 */
				log.info("---------->updatePerpetualLine data : " + updatePerpetualLine);
				log.info("---------->RECOUNT : " + RECOUNT);
				if (updatePerpetualLine.getCycleCountAction().equalsIgnoreCase(RECOUNT)) {
					dbPerpetualLine.setStatusId(78L);
					dbPerpetualLine.setCycleCountAction(RECOUNT);
					PerpetualLine updatedPerpetualLine = perpetualLineRepository.save(dbPerpetualLine);
					log.info("updatedPerpetualLine : " + updatedPerpetualLine);
					responsePerpetualLines.add(updatedPerpetualLine);
					
					/*
					 * Preparation of new PerpetualLines
					 */
					AddPerpetualLine newPerpetualLine = new AddPerpetualLine();
					BeanUtils.copyProperties(updatedPerpetualLine, newPerpetualLine, CommonUtils.getNullPropertyNames(updatedPerpetualLine));
					newPerpetualLine.setStatusId(70L);
					newPerpetualLines.add(newPerpetualLine);
				}
			}
			
			PerpetualHeader newlyCreatedPerpetualHeader = new PerpetualHeader();
			if (!newPerpetualLines.isEmpty()) {
				log.info("newPerpetualLines : " + newPerpetualLines);
				// Create new PerpetualHeader and Lines
				PerpetualHeaderEntity createdPerpetualHeader = createNewHeaderNLines(cycleCountNo, newPerpetualLines, loginUserID);
				BeanUtils.copyProperties(createdPerpetualHeader, newlyCreatedPerpetualHeader, CommonUtils.getNullPropertyNames(createdPerpetualHeader));
			}
			
			// Update new PerpetualHeader
			PerpetualHeader dbPerpetualHeader = perpetualHeaderService.getPerpetualHeader(cycleCountNo);
			UpdatePerpetualHeader updatePerpetualHeader = new UpdatePerpetualHeader();
			BeanUtils.copyProperties(dbPerpetualHeader, updatePerpetualHeader, CommonUtils.getNullPropertyNames(dbPerpetualHeader));
			PerpetualHeader updatedPerpetualHeader = perpetualHeaderService.updatePerpetualHeader(dbPerpetualHeader.getWarehouseId(), dbPerpetualHeader.getCycleCountTypeId(), 
					dbPerpetualHeader.getCycleCountNo(), dbPerpetualHeader.getMovementTypeId(), dbPerpetualHeader.getSubMovementTypeId(), loginUserID);
			log.info("updatedPerpetualHeader : " + updatedPerpetualHeader);
			
			PerpetualUpdateResponse response = new PerpetualUpdateResponse();
			response.setPerpetualHeader(newlyCreatedPerpetualHeader);
			response.setPerpetualLines(responsePerpetualLines);
			log.info("PerpetualUpdateResponse------> : " + response);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
	}
	
	/**
	 * 
	 * @param cycleCountNo 
	 * @param newPerpetualLines
	 * @param loginUserID
	 * @return 
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private PerpetualHeaderEntity createNewHeaderNLines(String cycleCountNo, List<AddPerpetualLine> newPerpetualLines, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		log.info("newPerpetualLines : " + newPerpetualLines);
		if (newPerpetualLines != null) {
			PerpetualHeader dbPerpetualHeader = perpetualHeaderService.getPerpetualHeader(cycleCountNo);
			AddPerpetualHeader newPerpetualHeader = new AddPerpetualHeader();
			BeanUtils.copyProperties(dbPerpetualHeader, newPerpetualHeader, CommonUtils.getNullPropertyNames(dbPerpetualHeader));
			newPerpetualHeader.setAddPerpetualLine(newPerpetualLines);
			PerpetualHeaderEntity createdPerpetualHeader = perpetualHeaderService.createPerpetualHeader(newPerpetualHeader, loginUserID);
			log.info("createdPerpetualHeader : " + createdPerpetualHeader);
			return createdPerpetualHeader;
		}
		return null;
	}

	/**
	 * 
	 * @param updatedPerpetualLine
	 * @return 
	 */
	private Inventory updateInventory (PerpetualLine updatePerpetualLine) {
		Inventory inventory = inventoryService.getInventory(updatePerpetualLine.getWarehouseId(), 
				updatePerpetualLine.getPackBarcodes(), updatePerpetualLine.getItemCode(), 
				updatePerpetualLine.getStorageBin());
		if (inventory != null) {
			inventory.setInventoryQuantity(updatePerpetualLine.getCountedQty());
			Inventory updatedInventory = inventoryRepository.save(inventory);
			log.info("updatedInventory : " + updatedInventory);
			return updatedInventory;
		} else {
			return createInventory (updatePerpetualLine);
		}
	}
	
	/**
	 * 
	 * @param updatePerpetualLine
	 * @return
	 */
	private Inventory createInventory (PerpetualLine updatePerpetualLine) {
		Inventory inventory = new Inventory();
		BeanUtils.copyProperties(updatePerpetualLine, inventory, CommonUtils.getNullPropertyNames(updatePerpetualLine));
		inventory.setCompanyCodeId(updatePerpetualLine.getCompanyCodeId());
		
		// VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
		inventory.setVariantCode(1L);	
		inventory.setVariantSubCode("1");
		inventory.setStorageMethod("1");
		inventory.setBatchSerialNumber("1");
		inventory.setBinClassId(6L);	
		
		// ST_BIN ---Pass WH_ID/BIN_CL_ID=5 in STORAGEBIN table and fetch ST_BIN value and update
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		StorageBin storageBin =
				mastersService.getStorageBin(updatePerpetualLine.getWarehouseId(), 5L, authTokenForMastersService.getAccess_token());
		inventory.setStorageBin(storageBin.getStorageBin());

		List<IImbasicData1> imbasicdata1 = imbasicdata1Repository.findByItemCode(inventory.getItemCode());
		if(imbasicdata1 != null && !imbasicdata1.isEmpty()){
			inventory.setReferenceField8(imbasicdata1.get(0).getDescription());
			inventory.setReferenceField9(imbasicdata1.get(0).getManufacturePart());
		}
		if(storageBin != null){
			inventory.setReferenceField10(storageBin.getStorageSectionId());
			inventory.setReferenceField5(storageBin.getAisleNumber());
			inventory.setReferenceField6(storageBin.getShelfId());
			inventory.setReferenceField7(storageBin.getRowId());
		}
		
		// STCK_TYP_ID
		inventory.setStockTypeId(1L);
		
		// SP_ST_IND_ID
		inventory.setSpecialStockIndicatorId(1L);	
		
		// INV_QTY
		inventory.setInventoryQuantity(updatePerpetualLine.getVarianceQty());
		
		// INV_UOM
		inventory.setInventoryUom(updatePerpetualLine.getInventoryUom());
		
		inventory.setCreatedBy(updatePerpetualLine.getCreatedBy());
		inventory.setCreatedOn(updatePerpetualLine.getCreatedOn());
		Inventory createdinventory = inventoryRepository.save(inventory);
		log.info("created inventory : " + createdinventory);
		return createdinventory;
	}
	
	/**
	 * 
	 * @param updatedPerpetualLine
	 * @param createdinventory
	 * @return 
	 */
	private InventoryMovement createInventoryMovement (PerpetualLine updatedPerpetualLine) {
		InventoryMovement inventoryMovement = new InventoryMovement();
		BeanUtils.copyProperties(updatedPerpetualLine, inventoryMovement, CommonUtils.getNullPropertyNames(updatedPerpetualLine));
		
		inventoryMovement.setLanguageId(getLanguageId());
		inventoryMovement.setCompanyCodeId(getCompanyCode());
		inventoryMovement.setPlantId(getPlantId());
		inventoryMovement.setWarehouseId(updatedPerpetualLine.getWarehouseId());
		
		// MVT_TYP_ID
		inventoryMovement.setMovementType(4L);
		
		// SUB_MVT_TYP_ID
		inventoryMovement.setSubmovementType(1L);
		
		// ITEM_TEXT
		// Pass ITM_CODE in IMBASICDATA table and fetch ITEM_TEXT values
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCode(inventoryMovement.getItemCode(),
																			 inventoryMovement.getWarehouseId(), authTokenForMastersService.getAccess_token());
		inventoryMovement.setDescription(imBasicData1.getDescription());
		
		// MFR_PART of ITM_CODE from BASICDATA1
		inventoryMovement.setManufacturerPartNo(imBasicData1.getManufacturerPartNo());
		
		/*
		 * MVT_QTY_VAL
		 * -----------------
		 * Hard Coded value "P", if VAR_QTY is negative and Hard coded value "N", if VAR_QTY is positive
		 */
		if (updatedPerpetualLine.getVarianceQty() < 0 ) {
			inventoryMovement.setMovementQtyValue("P");
		} else if (updatedPerpetualLine.getVarianceQty() > 0 ) {
			inventoryMovement.setMovementQtyValue("N");
		} 
		
		inventoryMovement.setMovementQty(updatedPerpetualLine.getVarianceQty());		
		inventoryMovement.setBatchSerialNumber("1");
		inventoryMovement.setMovementDocumentNo(updatedPerpetualLine.getCycleCountNo());
		
		// IM_CTD_BY
		inventoryMovement.setCreatedBy(updatedPerpetualLine.getCreatedBy());
		
		// IM_CTD_ON
		inventoryMovement.setCreatedOn(new Date());
		inventoryMovement = inventoryMovementRepository.save(inventoryMovement);
		log.info("created InventoryMovement : " + inventoryMovement);
		return inventoryMovement;
	}
}