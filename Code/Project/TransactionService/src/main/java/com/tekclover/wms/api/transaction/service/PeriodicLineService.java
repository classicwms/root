package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.transaction.model.dto.IImbasicData1;
import com.tekclover.wms.api.transaction.repository.ImBasicData1Repository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.AddPeriodicHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.AddPeriodicLine;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicHeaderEntity;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicLine;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.UpdatePeriodicLine;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.AssignHHTUserCC;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.PeriodicLineRepository;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PeriodicLineService extends BaseService {

	private static final String WRITEOFF = "WRITEOFF";
	private static final String SKIP = "SKIP";
	private static final String RECOUNT = "RECOUNT";

	@Autowired
	AuthTokenService authTokenService;
	
	@Autowired
	InventoryService inventoryService;
	
	@Autowired
	MastersService mastersService;
	
	@Autowired
	PeriodicHeaderService periodicHeaderService;
	
	@Autowired
	InventoryRepository inventoryRepository;
	
	@Autowired
	InventoryMovementRepository inventoryMovementRepository;
	
	@Autowired
	private PeriodicLineRepository periodicLineRepository;

	@Autowired
	private ImBasicData1Repository imbasicdata1Repository;

	/**
	 * getPeriodicLines
	 * 
	 * @return
	 */
	public List<PeriodicLine> getPeriodicLines() {
		List<PeriodicLine> periodicLineList = periodicLineRepository.findAll();
		periodicLineList = periodicLineList.stream().filter(n -> n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return periodicLineList;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param cycleCountNo
	 * @param storageBin
	 * @param itemCode
	 * @param packBarcodes
	 * @return
	 */
	public PeriodicLine getPeriodicLine(String warehouseId, String cycleCountNo, String storageBin, String itemCode,
			String packBarcodes) {
		PeriodicLine periodicLine = 
				periodicLineRepository.findByWarehouseIdAndCycleCountNoAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator(
				warehouseId, cycleCountNo, storageBin, itemCode, packBarcodes, 0L);
		if (periodicLine != null && periodicLine.getDeletionIndicator() == 0) {
			return periodicLine;
		}
		throw new BadRequestException("The given PeriodicLine ID : " + storageBin + " doesn't exist.");
	}
	
	/**
	 * 
	 * @param cycleCountNo
	 * @return
	 */
	public List<PeriodicLine> getPeriodicLine(String cycleCountNo) {
		List<PeriodicLine> perpetualLine = periodicLineRepository.findByCycleCountNoAndDeletionIndicator(cycleCountNo, 0L);
		return perpetualLine;
	}
	
	/**
	 * 
	 * @param newPeriodicLine
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PeriodicLine createPeriodicLine (AddPeriodicLine newPeriodicLine, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PeriodicLine dbPeriodicLine = new PeriodicLine();
		log.info("newPeriodicLine : " + newPeriodicLine);
		BeanUtils.copyProperties(newPeriodicLine, dbPeriodicLine, CommonUtils.getNullPropertyNames(newPeriodicLine));
		dbPeriodicLine.setDeletionIndicator(0L);
		dbPeriodicLine.setCreatedBy(loginUserID);
		dbPeriodicLine.setCreatedOn(new Date());
		dbPeriodicLine.setCountedBy(loginUserID);
		dbPeriodicLine.setCountedOn(new Date());
		return periodicLineRepository.save(dbPeriodicLine);
	}

	/**
	 * 
	 * @param assignHHTUsers
	 * @param loginUserID
	 * @return
	 */
	public List<PeriodicLine> updateAssingHHTUser (List<AssignHHTUserCC> assignHHTUsers, String loginUserID) {
		List<PeriodicLine> responseList = new ArrayList<>();
		for (AssignHHTUserCC assignHHTUser : assignHHTUsers) {
			PeriodicLine periodicLine = getPeriodicLine(assignHHTUser.getWarehouseId(), assignHHTUser.getCycleCountNo(), 
					assignHHTUser.getStorageBin(), assignHHTUser.getItemCode(), assignHHTUser.getPackBarcodes());
			periodicLine.setCycleCounterId(assignHHTUser.getCycleCounterId());
			periodicLine.setCycleCounterName(assignHHTUser.getCycleCounterName());
			periodicLine.setStatusId(72L);
			periodicLine.setCountedBy(loginUserID);
			periodicLine.setCountedOn(new Date());
			PeriodicLine updatedPeriodicLine = periodicLineRepository.save(periodicLine);
			responseList.add(updatedPeriodicLine);
		}
		return responseList;
	}
	
	/**
	 * 
	 * @param updatePeriodicLines
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<PeriodicLine> updatePeriodicLineForMobileCount (List<UpdatePeriodicLine> updatePeriodicLines, 
			String loginUserID) throws IllegalAccessException, InvocationTargetException {
		List<PeriodicLine> responsePeriodicLines = new ArrayList<>();
		for (UpdatePeriodicLine updatePeriodicLine : updatePeriodicLines) {
			PeriodicLine dbPeriodicLine = getPeriodicLine(updatePeriodicLine.getWarehouseId(), updatePeriodicLine.getCycleCountNo(), 
					updatePeriodicLine.getStorageBin(), updatePeriodicLine.getItemCode(), updatePeriodicLine.getPackBarcodes());
			if (dbPeriodicLine != null) { /* Update */
				BeanUtils.copyProperties(updatePeriodicLine, dbPeriodicLine, CommonUtils.getNullPropertyNames(updatePeriodicLine));
				
				// INV_QTY
				double INV_QTY = updatePeriodicLine.getInventoryQuantity();
				dbPeriodicLine.setInventoryQuantity(INV_QTY);
				
				// CTD_QTY
				double CTD_QTY = updatePeriodicLine.getCountedQty();
				dbPeriodicLine.setCountedQty(CTD_QTY);
				
				// VAR_QTY = INV_QTY - CTD_QTY
				double VAR_QTY = INV_QTY - CTD_QTY;
				dbPeriodicLine.setVarianceQty(VAR_QTY);
				
				/*
				 * HardCoded Value "78" if VAR_QTY = 0 and 
				 * Hardcodeed value"74" - if VAR_QTY is greater than or less than Zero
				 */
				if (VAR_QTY == 0) {
					dbPeriodicLine.setStatusId(78L);
				} else if (VAR_QTY > 0 || VAR_QTY < 0) {
					dbPeriodicLine.setStatusId(74L);
				} 
				
				dbPeriodicLine.setCountedBy(loginUserID);
				dbPeriodicLine.setCountedOn(new Date());
				PeriodicLine updatedPeriodicLine = periodicLineRepository.save(dbPeriodicLine);
				log.info("updatedPeriodicLine : " + updatedPeriodicLine);
				responsePeriodicLines.add(updatedPeriodicLine);
			} else {
				// Create new Record
				AddPeriodicLine newPeriodicLine = new AddPeriodicLine ();
				BeanUtils.copyProperties(updatePeriodicLine, newPeriodicLine, CommonUtils.getNullPropertyNames(updatePeriodicLine));
				newPeriodicLine.setCycleCountNo(updatePeriodicLine.getCycleCountNo());
				PeriodicLine createdPeriodicLine = createPeriodicLine (newPeriodicLine, loginUserID);
				log.info("createdPeriodicLine : " + createdPeriodicLine);
				responsePeriodicLines.add(createdPeriodicLine);
			}
		}
		
		return responsePeriodicLines;
	}
	
	/**
	 * 
	 * @param cycleCountNo
	 * @param updatePeriodicLines
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<PeriodicLine> updatePeriodicLine(String cycleCountNo, List<UpdatePeriodicLine> updatePeriodicLines,
			String loginUserID) throws IllegalAccessException, InvocationTargetException {
		List<PeriodicLine> responsePeriodicLines = new ArrayList<>();
		try {
			for (UpdatePeriodicLine updatePeriodicLine : updatePeriodicLines) {
				PeriodicLine dbPeriodicLine = getPeriodicLine(updatePeriodicLine.getWarehouseId(), 
						updatePeriodicLine.getCycleCountNo(), 
						updatePeriodicLine.getStorageBin(), 
						updatePeriodicLine.getItemCode(), 
						updatePeriodicLine.getPackBarcodes());
				BeanUtils.copyProperties(updatePeriodicLine, dbPeriodicLine, CommonUtils.getNullPropertyNames(updatePeriodicLine));
				dbPeriodicLine.setRemarks(updatePeriodicLine.getRemarks());
				dbPeriodicLine.setCycleCountAction(updatePeriodicLine.getCycleCountAction());	
				
				/*
				 * 1. Action = WRITEOFF 
				 * If ACTION = WRITEOFF , update ACTION field in PERPETUALLINE as WRITEOFF by passing unique fields and 
				 * update in STATUS_ID field as "76"
				 */
				if (updatePeriodicLine.getCycleCountAction().equalsIgnoreCase(WRITEOFF)) {
					dbPeriodicLine.setStatusId(76L);
					dbPeriodicLine.setCycleCountAction(WRITEOFF);
					PeriodicLine updatedPeriodicLine = periodicLineRepository.save(dbPeriodicLine);
					log.info("updatedPeriodicLine : " + updatedPeriodicLine);
					responsePeriodicLines.add(updatedPeriodicLine);
					
					/*
					 * Inventory table update
					 * ---------------------------
					 * Fetch CNT_QTY of the selected ITM_CODE and Pass WH_ID/ITM_CODE/ST_BIN/PACK_BARCODE values in INVENTORY table 
					 * and replace INV_QTY as CNT_QTY
					 */
					updateInventory (updatedPeriodicLine);
					createInventoryMovement (updatedPeriodicLine) ;
				}
				
				
				/*
				 * 2. Action = SKIP
				 * if ACTION = SKIP in UI,  update ACTION field in PERPETUALLINE as SKIP by passing unique fields 
				 * and update in STATUS_ID field as "77"
				 */
				if (updatePeriodicLine.getCycleCountAction().equalsIgnoreCase(SKIP)) {
					dbPeriodicLine.setStatusId(77L);
					dbPeriodicLine.setCycleCountAction(SKIP);
					PeriodicLine updatedPeriodicLine = periodicLineRepository.save(dbPeriodicLine);
					log.info("updatedPeriodicLine : " + updatedPeriodicLine);
					responsePeriodicLines.add(updatedPeriodicLine);
					
					/*
					 * Inventory table update
					 * ---------------------------
					 * Insert a new record by passing WH_ID/ITM_CODE/PACK_BARCODE/ST_BIN (fetch ST_BIN 
					 * from STORAGEBIN table where BIN_CL_ID=5) values in INVENTORY table and append INV_QTY as 
					 * VAR_QTY
					 */
					createInventory (updatedPeriodicLine);
					createInventoryMovement (updatedPeriodicLine) ;
				}
				
				/*
				 * 3. Action = RECOUNT (default Action Value)
				 * If ACTION = RECOUNT, update ACTION field in PERPETUALLINE as SKIP by passing unique fields 
				 * and update in STATUS_ID field as "75 "
				 */
				if (updatePeriodicLine.getCycleCountAction().equalsIgnoreCase(RECOUNT)) {
					dbPeriodicLine.setStatusId(75L);
					dbPeriodicLine.setCycleCountAction(RECOUNT);
					PeriodicLine updatedPeriodicLine = periodicLineRepository.save(dbPeriodicLine);
					log.info("updatedPeriodicLine : " + updatedPeriodicLine);
					responsePeriodicLines.add(updatedPeriodicLine);
					
					/*
					 * Also create New CC_NO record as below
					 */
					AddPeriodicHeader newPeriodicHeader = new AddPeriodicHeader();
					PeriodicHeader perpetualHeader = periodicHeaderService.getPeriodicHeader(updatedPeriodicLine.getCycleCountNo());
					BeanUtils.copyProperties(perpetualHeader, newPeriodicHeader, CommonUtils.getNullPropertyNames(perpetualHeader));
					newPeriodicHeader.setReferenceField1(updatedPeriodicLine.getCycleCountNo());
					
					// Adding Lines
					List<AddPeriodicLine> addPeriodicLineList = new ArrayList<>();
					AddPeriodicLine newPeriodicLine = new AddPeriodicLine();
					BeanUtils.copyProperties(updatedPeriodicLine, newPeriodicLine, CommonUtils.getNullPropertyNames(updatedPeriodicLine));
					addPeriodicLineList.add(newPeriodicLine);
					newPeriodicHeader.setAddPeriodicLine(addPeriodicLineList);
					
					PeriodicHeaderEntity createdPeriodicHeader = 
							periodicHeaderService.createPeriodicHeader(newPeriodicHeader, loginUserID);
					log.info("createdPeriodicHeader : " + createdPeriodicHeader);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return responsePeriodicLines;
	}
	
	/**
	 * 
	 * @param updatedPeriodicLine
	 * @return 
	 */
	private Inventory updateInventory (PeriodicLine updatePeriodicLine) {
		Inventory inventory = inventoryService.getInventory(updatePeriodicLine.getWarehouseId(), 
				updatePeriodicLine.getPackBarcodes(), updatePeriodicLine.getItemCode(), 
				updatePeriodicLine.getStorageBin());
		inventory.setInventoryQuantity(updatePeriodicLine.getCountedQty());
		Inventory updatedInventory = inventoryRepository.save(inventory);
		log.info("updatedInventory : " + updatedInventory);
		return updatedInventory;
	}
	
	/**
	 * 
	 * @param updatePeriodicLine
	 * @return
	 */
	private Inventory createInventory (PeriodicLine updatePeriodicLine) {
		Inventory inventory = new Inventory();
		BeanUtils.copyProperties(updatePeriodicLine, inventory, CommonUtils.getNullPropertyNames(updatePeriodicLine));
		inventory.setCompanyCodeId(updatePeriodicLine.getCompanyCode());
		
		// VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
		inventory.setVariantCode(1L);	
		inventory.setVariantSubCode("1");
		inventory.setStorageMethod("1");
		inventory.setBatchSerialNumber("1");
		inventory.setBinClassId(5L);	
		
		// ST_BIN ---Pass WH_ID/BIN_CL_ID=5 in STORAGEBIN table and fetch ST_BIN value and update
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		StorageBin storageBin = 
				mastersService.getStorageBin(updatePeriodicLine.getWarehouseId(), 5L, authTokenForMastersService.getAccess_token());
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
		inventory.setInventoryQuantity(updatePeriodicLine.getVarianceQty());
		
		// INV_UOM
		inventory.setInventoryUom(updatePeriodicLine.getInventoryUom());
		
		inventory.setCreatedBy(updatePeriodicLine.getCreatedBy());
		inventory.setCreatedOn(updatePeriodicLine.getCreatedOn());
		Inventory createdinventory = inventoryRepository.save(inventory);
		log.info("created inventory : " + createdinventory);
		return createdinventory;
	}
	
	/**
	 * 
	 * @param updatedPeriodicLine
	 * @param createdinventory
	 * @return 
	 */
	private InventoryMovement createInventoryMovement (PeriodicLine updatedPeriodicLine) {
		InventoryMovement inventoryMovement = new InventoryMovement();
		BeanUtils.copyProperties(updatedPeriodicLine, inventoryMovement, CommonUtils.getNullPropertyNames(updatedPeriodicLine));
		
		inventoryMovement.setLanguageId(getLanguageId());
		inventoryMovement.setCompanyCodeId(getCompanyCode());
		inventoryMovement.setPlantId(getPlantId());
		inventoryMovement.setWarehouseId(updatedPeriodicLine.getWarehouseId());
		
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
		if (updatedPeriodicLine.getVarianceQty() < 0 ) {
			inventoryMovement.setMovementQtyValue("P");
		} else if (updatedPeriodicLine.getVarianceQty() > 0 ) {
			inventoryMovement.setMovementQtyValue("N");
		} 
		
		inventoryMovement.setBatchSerialNumber("1");
		inventoryMovement.setMovementDocumentNo(updatedPeriodicLine.getCycleCountNo());
		
		// IM_CTD_BY
		inventoryMovement.setCreatedBy(updatedPeriodicLine.getCreatedBy());
		
		// IM_CTD_ON
		inventoryMovement.setCreatedOn(updatedPeriodicLine.getCreatedOn());
		inventoryMovement = inventoryMovementRepository.save(inventoryMovement);
		log.info("created InventoryMovement : " + inventoryMovement);
		return inventoryMovement;
	}

	/**
	 * deletePeriodicLine
	 * 
	 * @param loginUserID
	 * @param storageBin
	 */
	public void deletePeriodicLine(String warehouseId, String cycleCountNo, String storageBin, String itemCode, String packBarcodes, String loginUserID) {
		PeriodicLine periodicLine = getPeriodicLine(warehouseId, cycleCountNo, storageBin, itemCode, packBarcodes);
		if (periodicLine != null) {
			periodicLine.setDeletionIndicator(1L);
			periodicLine.setConfirmedBy(loginUserID);
			periodicLine.setConfirmedOn(new Date());
			periodicLineRepository.save(periodicLine);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storageBin);
		}
	}
}
