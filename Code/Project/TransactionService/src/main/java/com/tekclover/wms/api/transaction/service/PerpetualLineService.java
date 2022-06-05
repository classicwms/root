package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.AddPerpetualHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.AddPerpetualLine;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.AssignHHTUserCC;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualLine;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.UpdatePerpetualLine;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.PerpetualLineRepository;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

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
		if (perpetualLine != null) {
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
		if (perpetualLine.isEmpty()) {
			throw new BadRequestException("The given cycleCountNo: " + cycleCountNo);
		} 
		return perpetualLine;
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
	 * updateAssingHHTUser
	 * @param assignHHTUser
	 * @param loginUserID
	 * @return
	 */
	public List<PerpetualLine> updateAssingHHTUser (List<AssignHHTUserCC> assignHHTUsers, String loginUserID) {
		List<PerpetualLine> responseList = new ArrayList<>();
		for (AssignHHTUserCC assignHHTUser : assignHHTUsers) {
			PerpetualLine dbPerpetualLine = getPerpetualLine(assignHHTUser.getWarehouseId(), assignHHTUser.getCycleCountNo(), 
					assignHHTUser.getStorageBin(), assignHHTUser.getItemCode(), assignHHTUser.getPackBarcodes());
			dbPerpetualLine.setCycleCounterId(assignHHTUser.getCycleCounterId());
			dbPerpetualLine.setCycleCounterName(assignHHTUser.getCycleCounterName());
			dbPerpetualLine.setStatusId("72");
			dbPerpetualLine.setCountedBy(loginUserID);
			dbPerpetualLine.setCountedOn(new Date());
			PerpetualLine updatedPerpetualLine = perpetualLineRepository.save(dbPerpetualLine);
			responseList.add(updatedPerpetualLine);
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
	public List<PerpetualLine> updatePerpetualLine (List<UpdatePerpetualLine> updatePerpetualLines, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		List<PerpetualLine> responsePerpetualLines = new ArrayList<>();
		for (UpdatePerpetualLine updatePerpetualLine : updatePerpetualLines) {
			PerpetualLine dbPerpetualLine = getPerpetualLine(updatePerpetualLine.getWarehouseId(), updatePerpetualLine.getCycleCountNo(), 
					updatePerpetualLine.getStorageBin(), updatePerpetualLine.getItemCode(), updatePerpetualLine.getPackBarcodes());
			if (dbPerpetualLine != null) { /* Update */
				BeanUtils.copyProperties(updatePerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(updatePerpetualLine));
				
				// INV_QTY
				double INV_QTY = updatePerpetualLine.getInventoryQuantity();
				dbPerpetualLine.setInventoryQuantity(INV_QTY);
				
				// CTD_QTY
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
					dbPerpetualLine.setStatusId("78");
				} else if (VAR_QTY > 0 || VAR_QTY < 0) {
					dbPerpetualLine.setStatusId("74");
				}
				
				dbPerpetualLine.setCountedBy(loginUserID);
				dbPerpetualLine.setCountedOn(new Date());
				PerpetualLine updatedPerpetualLine = perpetualLineRepository.save(dbPerpetualLine);
				log.info("updatedPerpetualLine : " + updatedPerpetualLine);
				responsePerpetualLines.add(updatedPerpetualLine);
			} else {
				// Create new Record
				AddPerpetualLine newPerpetualLine = new AddPerpetualLine ();
				BeanUtils.copyProperties(updatePerpetualLine, newPerpetualLine, CommonUtils.getNullPropertyNames(updatePerpetualLine));
				newPerpetualLine.setCycleCountNo(updatePerpetualLine.getCycleCountNo());
				PerpetualLine createdPerpetualLine = createPerpetualLine (newPerpetualLine, loginUserID);
				log.info("createdPerpetualLine : " + createdPerpetualLine);
				responsePerpetualLines.add(createdPerpetualLine);
			}
		}
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
	public List<PerpetualLine> updatePerpetualLine(String cycleCountNo, List<UpdatePerpetualLine> updatePerpetualLines,
			String loginUserID) throws IllegalAccessException, InvocationTargetException {
		List<PerpetualLine> responsePerpetualLines = new ArrayList<>();
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
			 * update in STATUS_ID field as "76"
			 */
			if (updatePerpetualLine.getStatusId().equalsIgnoreCase(WRITEOFF)) {
				dbPerpetualLine.setStatusId("76");
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
			 * and update in STATUS_ID field as "77"
			 */
			if (updatePerpetualLine.getStatusId().equalsIgnoreCase(SKIP)) {
				dbPerpetualLine.setStatusId("77");
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
				createInventory (updatedPerpetualLine);
				createInventoryMovement (updatedPerpetualLine) ;
			}
			
			/*
			 * 3. Action = RECOUNT (default Action Value)
			 * If ACTION = RECOUNT, update ACTION field in PERPETUALLINE as SKIP by passing unique fields 
			 * and update in STATUS_ID field as "75 "
			 */
			if (updatePerpetualLine.getStatusId().equalsIgnoreCase(RECOUNT)) {
				dbPerpetualLine.setStatusId("75");
				dbPerpetualLine.setCycleCountAction(RECOUNT);
				PerpetualLine updatedPerpetualLine = perpetualLineRepository.save(dbPerpetualLine);
				log.info("updatedPerpetualLine : " + updatedPerpetualLine);
				responsePerpetualLines.add(updatedPerpetualLine);
				
				/*
				 * Also create New CC_NO record as below
				 */
				AddPerpetualHeader newPerpetualHeader = new AddPerpetualHeader();
				PerpetualHeader perpetualHeader = perpetualHeaderService.getPerpetualHeader(updatedPerpetualLine.getCycleCountNo());
				BeanUtils.copyProperties(perpetualHeader, newPerpetualHeader, CommonUtils.getNullPropertyNames(perpetualHeader));
				newPerpetualHeader.setReferenceField1(updatedPerpetualLine.getCycleCountNo());
				PerpetualHeader createdPerpetualHeader = 
						perpetualHeaderService.createPerpetualHeader(newPerpetualHeader, loginUserID);
				log.info("createdPerpetualHeader : " + createdPerpetualHeader);
			}
		}
		
		return responsePerpetualLines;
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
		inventory.setInventoryQuantity(updatePerpetualLine.getCountedQty());
		Inventory updatedInventory = inventoryRepository.save(inventory);
		log.info("updatedInventory : " + updatedInventory);
		return updatedInventory;
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
		inventory.setBinClassId(5L);	
		
		// ST_BIN ---Pass WH_ID/BIN_CL_ID=5 in STORAGEBIN table and fetch ST_BIN value and update
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		StorageBin storageBin = 
				mastersService.getStorageBin(updatePerpetualLine.getWarehouseId(), 5L, authTokenForMastersService.getAccess_token());
		inventory.setStorageBin(storageBin.getStorageBin());
		
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
		
		inventoryMovement.setCompanyCodeId(updatedPerpetualLine.getCompanyCodeId());
		inventoryMovement.setPlantId(updatedPerpetualLine.getPlantId());
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
		
		// IM_CTD_BY
		inventoryMovement.setCreatedBy(updatedPerpetualLine.getCreatedBy());
		
		// IM_CTD_ON
		inventoryMovement.setCreatedOn(updatedPerpetualLine.getCreatedOn());
		inventoryMovement = inventoryMovementRepository.save(inventoryMovement);
		log.info("cerated InventoryMovement : " + inventoryMovement);
		return inventoryMovement;
	}
}
