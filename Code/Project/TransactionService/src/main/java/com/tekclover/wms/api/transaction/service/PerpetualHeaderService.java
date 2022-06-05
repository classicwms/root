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
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.AddPerpetualHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.AddPerpetualLine;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualHeaderEntity;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualLine;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualLineEntity;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.RunPerpetualHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.SearchPerpetualHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.UpdatePerpetualHeader;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.transaction.repository.PerpetualHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PerpetualLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.PerpetualHeaderSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PerpetualHeaderService extends BaseService {
	
	@Autowired
	PerpetualHeaderRepository perpetualHeaderRepository;
	
	@Autowired
	PerpetualLineRepository perpetualLineRepository;
	
	@Autowired
	InventoryMovementRepository inventoryMovementRepository;
	
	@Autowired
	PerpetualLineService perpetualLineService;
	
	@Autowired
	AuthTokenService authTokenService;
	
	@Autowired
	MastersService mastersService;
	
	@Autowired
	InventoryService inventoryService;
	
	/**
	 * getPerpetualHeaders
	 * @return
	 */
	public List<PerpetualHeaderEntity> getPerpetualHeaders () {
		List<PerpetualHeader> perpetualHeaderList =  perpetualHeaderRepository.findAll();
		perpetualHeaderList = perpetualHeaderList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0L)
				.collect(Collectors.toList());
		return convertToEntity (perpetualHeaderList);
	}
	
	/**
	 * 
	 * @param cycleCountNo
	 * @return
	 */
	public PerpetualHeader getPerpetualHeader (String cycleCountNo) {
		PerpetualHeader perpetualHeader = perpetualHeaderRepository.findByCycleCountNo(cycleCountNo);
		return perpetualHeader;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param cycleCountTypeId
	 * @param cycleCountNo
	 * @param movementTypeId
	 * @param subMovementTypeId
	 * @return
	 */
	public List<PerpetualHeaderEntity> getPerpetualHeader (String warehouseId, Long cycleCountTypeId, String cycleCountNo, 
			Long movementTypeId, Long subMovementTypeId) {
		Optional<PerpetualHeader> optPerpetualHeader = 
				perpetualHeaderRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNoAndMovementTypeIdAndSubMovementTypeIdAndDeletionIndicator(
				getCompanyCode(), getPlantId(), warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId, subMovementTypeId, 0L);
		
		if (optPerpetualHeader.isEmpty()) {
			throw new BadRequestException("The given PerpetualHeader ID : " + cycleCountNo 
					+ "cycleCountTypeId: " + cycleCountTypeId + ","
					+ "movementTypeId: " + movementTypeId + ","
					+ "subMovementTypeId: " + subMovementTypeId
					+ " doesn't exist.");
		}
		return convertToEntity (Arrays.asList(optPerpetualHeader.get()));
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param cycleCountTypeId
	 * @param cycleCountNo
	 * @param movementTypeId
	 * @param subMovementTypeId
	 * @return
	 */
	public PerpetualHeader getPerpetualHeaderRecord (String warehouseId, Long cycleCountTypeId, String cycleCountNo, 
			Long movementTypeId, Long subMovementTypeId) {
		Optional<PerpetualHeader> optPerpetualHeader = 
				perpetualHeaderRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNoAndMovementTypeIdAndSubMovementTypeIdAndDeletionIndicator(
				getCompanyCode(), getPlantId(), warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId, subMovementTypeId, 0L);
		
		if (optPerpetualHeader.isEmpty()) {
			throw new BadRequestException("The given PerpetualHeader ID : " + cycleCountNo 
					+ "cycleCountTypeId: " + cycleCountTypeId + ","
					+ "movementTypeId: " + movementTypeId + ","
					+ "subMovementTypeId: " + subMovementTypeId
					+ " doesn't exist.");
		}
		return optPerpetualHeader.get();
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param cycleCountTypeId
	 * @param cycleCountNo
	 * @param movementTypeId
	 * @param subMovementTypeId
	 * @return
	 */
	public PerpetualHeaderEntity getPerpetualHeaderEntity (String warehouseId, Long cycleCountTypeId, String cycleCountNo, 
			Long movementTypeId, Long subMovementTypeId) {
		Optional<PerpetualHeader> optPerpetualHeader = 
				perpetualHeaderRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNoAndMovementTypeIdAndSubMovementTypeIdAndDeletionIndicator(
				getCompanyCode(), getPlantId(), warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId, subMovementTypeId, 0L);
		
		if (optPerpetualHeader.isEmpty()) {
			throw new BadRequestException("The given PerpetualHeader ID : " + cycleCountNo 
					+ "cycleCountTypeId: " + cycleCountTypeId + ","
					+ "movementTypeId: " + movementTypeId + ","
					+ "subMovementTypeId: " + subMovementTypeId
					+ " doesn't exist.");
		}
		
		PerpetualHeader perpetualHeader = optPerpetualHeader.get();
		return convertToEntity (perpetualHeader);
	}
	
	/**
	 * 
	 * @param searchPerpetualHeader
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException 
	 */
	public List<PerpetualHeaderEntity> findPerpetualHeader(SearchPerpetualHeader searchPerpetualHeader) 
			throws ParseException, java.text.ParseException {		
		if (searchPerpetualHeader.getStartCreatedOn() != null && searchPerpetualHeader.getStartCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPerpetualHeader.getStartCreatedOn(), searchPerpetualHeader.getEndCreatedOn());
			searchPerpetualHeader.setStartCreatedOn(dates[0]);
			searchPerpetualHeader.setEndCreatedOn(dates[1]);
		}
		
		PerpetualHeaderSpecification spec = new PerpetualHeaderSpecification(searchPerpetualHeader);
		List<PerpetualHeader> perpetualHeaderResults = perpetualHeaderRepository.findAll(spec);
		log.info("perpetualHeaderResults: " + perpetualHeaderResults);
		return convertToEntity (perpetualHeaderResults);
	}
	
	/**
	 * 
	 * @param runPerpetualHeader
	 * @return
	 */
	public List<PerpetualLineEntity> runPerpetualHeader(@Valid RunPerpetualHeader runPerpetualHeader) {
		List<InventoryMovement> inventoryMovements = 
				inventoryMovementRepository.findByMovementTypeInAndSubmovementTypeInAndCreatedOnBetween (
				runPerpetualHeader.getMovementTypeId(), runPerpetualHeader.getSubMovementTypeId(),
				runPerpetualHeader.getDateFrom(), runPerpetualHeader.getDateTo());
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		
		List<PerpetualLineEntity> perpetualLineList = new ArrayList<>();
		for (InventoryMovement inventoryMovement : inventoryMovements) {
			PerpetualLineEntity perpetualLine = new PerpetualLineEntity();
			
			// ITM_CODE
			perpetualLine.setItemCode(inventoryMovement.getItemCode());
			
			// Pass ITM_CODE in IMBASICDATA table and fetch ITEM_TEXT values
			ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCode(inventoryMovement.getItemCode(), 
					inventoryMovement.getWarehouseId(), authTokenForMastersService.getAccess_token());
			perpetualLine.setItemDesc(imBasicData1.getDescription());
			
			// ST_BIN
			perpetualLine.setStorageBin(inventoryMovement.getStorageBin());
			
			// ST_SEC_ID/ST_SEC
			// Pass the ST_BIN in STORAGEBIN table and fetch ST_SEC_ID/ST_SEC values
			StorageBin storageBin = mastersService.getStorageBin(inventoryMovement.getStorageBin(), authTokenForMastersService.getAccess_token());
			perpetualLine.setStorageSectionId(storageBin.getStorageSectionId());
			
			// MFR_PART
			// Pass ITM_CODE in IMBASICDATA table and fetch MFR_PART values
			perpetualLine.setManufacturerPartNo(imBasicData1.getManufacturerPartNo());
			
			// STCK_TYP_ID
			perpetualLine.setStockTypeId(inventoryMovement.getStockTypeId());
			
			// SP_ST_IND_ID
			perpetualLine.setSpecialStockIndicator(inventoryMovement.getSpecialStockIndicator());
			
			// PACK_BARCODE
			perpetualLine.setPackBarcodes(inventoryMovement.getPackBarcodes());
			
			/*
			 * INV_QTY
			 * -------------
			 * Pass the filled WH_ID/ITM_CODE/PACK_BARCODE/ST_BIN
			 * values in INVENTORY table and fetch INV_QTY/INV_UOM values and 
			 * fill against each ITM_CODE values and this is non-editable"
			 */
			Inventory inventory = inventoryService.getInventory(inventoryMovement.getWarehouseId(), 
					inventoryMovement.getPackBarcodes(), inventoryMovement.getItemCode(), 
					inventoryMovement.getStorageBin());
			perpetualLine.setInventoryQuantity(inventory.getInventoryQuantity());
			perpetualLine.setInventoryUom(inventory.getInventoryUom());
			perpetualLineList.add(perpetualLine);
		}
		
		return perpetualLineList;
	}
	
	/**
	 * createPerpetualHeader
	 * @param newPerpetualHeader
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PerpetualHeader createPerpetualHeader (AddPerpetualHeader newPerpetualHeader, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PerpetualHeader dbPerpetualHeader = new PerpetualHeader();
		log.info("newPerpetualHeader : " + newPerpetualHeader);
		BeanUtils.copyProperties(newPerpetualHeader, dbPerpetualHeader, CommonUtils.getNullPropertyNames(newPerpetualHeader));
		
		/*
		 * Cycle Count No
		 * --------------------
		 * Pass WH_ID - User logged in WH_ID and NUM_RAN_ID = 14 values in NUMBERRANGE table and fetch NUM_RAN_CURRENT value and 
		 * add +1 and then update in PERPETUALHEADER table during Save
		 */
		AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
		long NUM_RAN_ID = 14;
		String nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, newPerpetualHeader.getWarehouseId(), 
				authTokenForIDMasterService.getAccess_token());
		dbPerpetualHeader.setCycleCountNo(nextRangeNumber);
		
		// CC_TYP_ID
		dbPerpetualHeader.setCycleCountTypeId(1L);
		
		// MVT_TYP_ID
//		dbPerpetualHeader.setMovementTypeId(newPerpetualHeader.getMovementTypeId());
		
		// SUB_MVT_TYP_ID
//		dbPerpetualHeader.setSubMovementTypeId(newPerpetualHeader.getSubMovementTypeId());
		
		// STATUS_ID - HardCoded Value "70"
		dbPerpetualHeader.setStatusId("70");
		dbPerpetualHeader.setDeletionIndicator(0L);
		dbPerpetualHeader.setCreatedBy(loginUserID);
		dbPerpetualHeader.setCountedBy(loginUserID);
		dbPerpetualHeader.setCreatedOn(new Date());
		dbPerpetualHeader.setCountedOn(new Date());
		PerpetualHeader createdPerpetualHeader = perpetualHeaderRepository.save(dbPerpetualHeader);
		log.info("createdPerpetualHeader : " + createdPerpetualHeader);
		
		// Lines Creation
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		List<PerpetualLine> perpetualLines = new ArrayList<>();
		for (AddPerpetualLine newPerpetualLine : newPerpetualHeader.getAddPerpetualLine()) {
			PerpetualLine dbPerpetualLine = new PerpetualLine();
			BeanUtils.copyProperties(newPerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(newPerpetualLine));
			
			// WH_ID
			dbPerpetualLine.setWarehouseId(createdPerpetualHeader.getWarehouseId());
			
			// CC_NO
			dbPerpetualLine.setCycleCountNo(createdPerpetualHeader.getCycleCountNo());
			
			dbPerpetualLine.setStatusId("70");
			dbPerpetualLine.setDeletionIndicator(0L);
			dbPerpetualLine.setCreatedBy(loginUserID);
			dbPerpetualLine.setCreatedOn(new Date());
			PerpetualLine createdPerpetualLine = perpetualLineRepository.save(dbPerpetualLine);
			log.info("createdPerpetualLine : " + createdPerpetualLine);
			perpetualLines.add(createdPerpetualLine);
			
			/*
			 * On Successful Cycle count creation , fetch ST_BIN from PERPETUALLINE for the selected WH_ID/CC_NO and
			 * Pass WH_ID/ ST_BIN in STORAGEBIN table and update PUTAWAY_BLOCK,PICK_BLOCK values with "TRUE"
			 */
			StorageBin modifiedStorageBin = new StorageBin();
			modifiedStorageBin.setPutawayBlock(1);
			modifiedStorageBin.setPickingBlock(1);
			StorageBin updatedStorageBin = mastersService.updateStorageBin(createdPerpetualLine.getStorageBin(), 
					modifiedStorageBin, loginUserID, authTokenForMastersService.getAccess_token());
			log.info("updatedStorageBin : " + updatedStorageBin);
		}
		return createdPerpetualHeader;
	}
	
	/**
	 * updatePerpetualHeader
	 * @param loginUserId 
	 * @param cycleCountNo
	 * @param updatePerpetualHeader
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PerpetualHeader updatePerpetualHeader (String warehouseId, Long cycleCountTypeId, String cycleCountNo, 
			Long movementTypeId, Long subMovementTypeId, String loginUserID, UpdatePerpetualHeader updatePerpetualHeader) 
			throws IllegalAccessException, InvocationTargetException {
		PerpetualHeader dbPerpetualHeader = getPerpetualHeaderRecord(warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId, 
				subMovementTypeId);
		BeanUtils.copyProperties(updatePerpetualHeader, dbPerpetualHeader, CommonUtils.getNullPropertyNames(updatePerpetualHeader));
		dbPerpetualHeader.setCountedBy(loginUserID);
		dbPerpetualHeader.setCountedOn(new Date());
		return perpetualHeaderRepository.save(dbPerpetualHeader);
	}
	
	/**
	 * deletePerpetualHeader
	 * @param loginUserID 
	 * @param cycleCountNo
	 */
	public void deletePerpetualHeader (String warehouseId, Long cycleCountTypeId, String cycleCountNo, Long movementTypeId, 
			Long subMovementTypeId, String loginUserID) {
		PerpetualHeader dbPerpetualHeader = getPerpetualHeaderRecord(warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId, 
				subMovementTypeId);
		if ( dbPerpetualHeader != null && dbPerpetualHeader.getStatusId().equalsIgnoreCase("70")) {
			dbPerpetualHeader.setDeletionIndicator(1L);
			dbPerpetualHeader.setCountedBy(loginUserID);
			dbPerpetualHeader.setCountedOn(new Date());
			perpetualHeaderRepository.save(dbPerpetualHeader);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + cycleCountNo);
		}
	}
	
	/**
	 * 
	 * @param perpetualHeaderList
	 * @return
	 */
	private List<PerpetualHeaderEntity> convertToEntity (List<PerpetualHeader> perpetualHeaderList) {
		List<PerpetualHeaderEntity> listPerpetualHeaderEntity = new ArrayList<>();
		for (PerpetualHeader perpetualHeader : perpetualHeaderList) {
			List<PerpetualLine> perpetualLineList = perpetualLineService.getPerpetualLine(perpetualHeader.getCycleCountNo());
			log.info("perpetualLineList found: " + perpetualLineList);
			
			List<PerpetualLineEntity> listPerpetualLineEntity = new ArrayList<>();
			for (PerpetualLine perpetualLine : perpetualLineList) {
				PerpetualLineEntity perpetualLineEntity = new PerpetualLineEntity();
				BeanUtils.copyProperties(perpetualLine, perpetualLineEntity, CommonUtils.getNullPropertyNames(perpetualLine));
				listPerpetualLineEntity.add(perpetualLineEntity);
			}
			
			PerpetualHeaderEntity perpetualHeaderEntity = new PerpetualHeaderEntity();
			BeanUtils.copyProperties(perpetualHeader, perpetualHeaderEntity, CommonUtils.getNullPropertyNames(perpetualHeader));
			perpetualHeaderEntity.setPerpetualLine(listPerpetualLineEntity);
			listPerpetualHeaderEntity.add(perpetualHeaderEntity);
		}
		return listPerpetualHeaderEntity;
	}
	
	/**
	 * 
	 * @param perpetualHeader
	 * @return
	 */
	private PerpetualHeaderEntity convertToEntity (PerpetualHeader perpetualHeader) {
		List<PerpetualLine> perpetualLineList = perpetualLineService.getPerpetualLine(perpetualHeader.getCycleCountNo());
		log.info("perpetualLineList found: " + perpetualLineList);
		
		List<PerpetualLineEntity> listPerpetualLineEntity = new ArrayList<>();
		for (PerpetualLine perpetualLine : perpetualLineList) {
			PerpetualLineEntity perpetualLineEntity = new PerpetualLineEntity();
			BeanUtils.copyProperties(perpetualLine, perpetualLineEntity, CommonUtils.getNullPropertyNames(perpetualLine));
			listPerpetualLineEntity.add(perpetualLineEntity);
		}
		
		PerpetualHeaderEntity perpetualHeaderEntity = new PerpetualHeaderEntity();
		BeanUtils.copyProperties(perpetualHeader, perpetualHeaderEntity, CommonUtils.getNullPropertyNames(perpetualHeader));
		perpetualHeaderEntity.setPerpetualLine(listPerpetualLineEntity);
		return perpetualHeaderEntity;
	}
}
