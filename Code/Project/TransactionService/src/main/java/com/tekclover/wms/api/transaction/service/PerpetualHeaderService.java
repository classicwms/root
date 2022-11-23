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

import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.transaction.repository.PerpetualHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PerpetualLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.PerpetualHeaderSpecification;
import com.tekclover.wms.api.transaction.repository.specification.PerpetualLineSpecification;
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
	public PerpetualHeader getPerpetualHeaderWithLine (String warehouseId, Long cycleCountTypeId, String cycleCountNo,
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
	public List<PerpetualHeader> findPerpetualHeader(SearchPerpetualHeader searchPerpetualHeader)
			throws ParseException, java.text.ParseException {		
		if (searchPerpetualHeader.getStartCreatedOn() != null && searchPerpetualHeader.getStartCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPerpetualHeader.getStartCreatedOn(), 
					searchPerpetualHeader.getEndCreatedOn());
			searchPerpetualHeader.setStartCreatedOn(dates[0]);
			searchPerpetualHeader.setEndCreatedOn(dates[1]);
		}
		
		PerpetualHeaderSpecification spec = new PerpetualHeaderSpecification(searchPerpetualHeader);
		List<PerpetualHeader> perpetualHeaderResults = perpetualHeaderRepository.findAll(spec);
//		log.info("perpetualHeaderResults: " + perpetualHeaderResults);
//		return convertToEntity (perpetualHeaderResults,
//				searchPerpetualHeader.getCycleCounterId(), searchPerpetualHeader.getLineStatusId());
		return perpetualHeaderResults;
	}
	
	/**
	 * 
	 * @param runPerpetualHeader
	 * @return
	 * @throws java.text.ParseException 
	 */
	public List<PerpetualLineEntity> runPerpetualHeader(@Valid RunPerpetualHeader runPerpetualHeader) throws java.text.ParseException {
		if (runPerpetualHeader.getDateFrom() != null && runPerpetualHeader.getDateFrom() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(runPerpetualHeader.getDateFrom(), 	runPerpetualHeader.getDateTo());
			runPerpetualHeader.setDateFrom(dates[0]);
			runPerpetualHeader.setDateTo(dates[1]);
		}
		
		List<InventoryMovement> inventoryMovements = inventoryMovementRepository.findByMovementTypeInAndSubmovementTypeInAndCreatedOnBetween (
				runPerpetualHeader.getMovementTypeId(), runPerpetualHeader.getSubMovementTypeId(),
				runPerpetualHeader.getDateFrom(), runPerpetualHeader.getDateTo());
//		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		List<PerpetualLineEntity> perpetualLineList = new ArrayList<>();
		for (InventoryMovement inventoryMovement : inventoryMovements) {
			PerpetualLineEntity perpetualLine = new PerpetualLineEntity();

			// LANG_ID
			perpetualLine.setLanguageId(inventoryMovement.getLanguageId());

			// C_ID
			perpetualLine.setCompanyCodeId(inventoryMovement.getCompanyCodeId());

			// PLANT_ID
			perpetualLine.setPlantId(inventoryMovement.getPlantId());

			// WH_ID
			perpetualLine.setWarehouseId(inventoryMovement.getWarehouseId());

			// ITM_CODE
			perpetualLine.setItemCode(inventoryMovement.getItemCode());

			// HAREESH 11-09-2022 comment item master get and item name from inventory itself
			// Pass ITM_CODE in IMBASICDATA table and fetch ITEM_TEXT values
//			ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCode(inventoryMovement.getItemCode(),
//					inventoryMovement.getWarehouseId(), authTokenForMastersService.getAccess_token());

			// ST_BIN
			perpetualLine.setStorageBin(inventoryMovement.getStorageBin());

			// HAREESH 11-09-2022 comment storage related data master get and get from inventory itself
			// ST_SEC_ID/ST_SEC
			// Pass the ST_BIN in STORAGEBIN table and fetch ST_SEC_ID/ST_SEC values
//			StorageBin storageBin = mastersService.getStorageBin(inventoryMovement.getStorageBin(), authTokenForMastersService.getAccess_token());

			// MFR_PART
			// Pass ITM_CODE in IMBASICDATA table and fetch MFR_PART values

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
			log.info("inventory : " + inventory);

			if (inventory != null) {
				perpetualLine.setInventoryQuantity((inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0 ) + (inventory.getAllocatedQuantity() != null ? inventory.getAllocatedQuantity() : 0 ));
				perpetualLine.setInventoryUom(inventory.getInventoryUom());

				perpetualLine.setItemDesc(inventory.getReferenceField8());
				perpetualLine.setManufacturerPartNo(inventory.getReferenceField9());
				perpetualLine.setStorageSectionId(inventory.getReferenceField10());
			}
			perpetualLine.setCreatedOn(null);
			perpetualLine.setCountedOn(null);
			perpetualLineList.add(perpetualLine);
		}

		List<PerpetualLineEntity> uniqueArray = new ArrayList<>();
		for (PerpetualLineEntity perpetualLine : perpetualLineList) {
			if(!uniqueArray.contains(perpetualLine)) {
				uniqueArray.add(perpetualLine);
			}
		}
		
		return uniqueArray;
	}

	//Performance enhanced
	public List<PerpetualLineEntityImpl> runPerpetualHeaderNew(@Valid RunPerpetualHeader runPerpetualHeader) throws java.text.ParseException {
		if (runPerpetualHeader.getDateFrom() != null && runPerpetualHeader.getDateFrom() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(runPerpetualHeader.getDateFrom(), 	runPerpetualHeader.getDateTo());
			runPerpetualHeader.setDateFrom(dates[0]);
			runPerpetualHeader.setDateTo(dates[1]);
		}

		return inventoryMovementRepository.getRecordsForRunPerpetualCount (
				runPerpetualHeader.getMovementTypeId(), runPerpetualHeader.getSubMovementTypeId(),
				runPerpetualHeader.getDateFrom(), runPerpetualHeader.getDateTo());

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
		dbPerpetualHeader.setLanguageId(getLanguageId());
		dbPerpetualHeader.setCompanyCodeId(getCompanyCode());
		dbPerpetualHeader.setPlantId(getPlantId());
		
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
		
		// STATUS_ID - HardCoded Value "70"
		dbPerpetualHeader.setStatusId(70L);
		dbPerpetualHeader.setDeletionIndicator(0L);
		dbPerpetualHeader.setCreatedBy(loginUserID);
		dbPerpetualHeader.setCountedBy(loginUserID);
		dbPerpetualHeader.setCreatedOn(new Date());
		dbPerpetualHeader.setCountedOn(new Date());
		PerpetualHeader createdPerpetualHeader = perpetualHeaderRepository.save(dbPerpetualHeader);
		log.info("createdPerpetualHeader : " + createdPerpetualHeader);
		
		// Lines Creation
		List<PerpetualLine> perpetualLines = new ArrayList<>();
		for (AddPerpetualLine newPerpetualLine : newPerpetualHeader.getAddPerpetualLine()) {
			PerpetualLine dbPerpetualLine = new PerpetualLine();
			BeanUtils.copyProperties(newPerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(newPerpetualLine));
			
			dbPerpetualLine.setLanguageId(getLanguageId());
			dbPerpetualLine.setCompanyCodeId(getCompanyCode());
			dbPerpetualLine.setPlantId(getPlantId());
			
			// WH_ID
			dbPerpetualLine.setWarehouseId(createdPerpetualHeader.getWarehouseId());
			
			// CC_NO
			dbPerpetualLine.setCycleCountNo(createdPerpetualHeader.getCycleCountNo());
			
			dbPerpetualLine.setStatusId(70L);
			dbPerpetualLine.setDeletionIndicator(0L);
			dbPerpetualLine.setCreatedBy(loginUserID);
			dbPerpetualLine.setCreatedOn(new Date());
			PerpetualLine createdPerpetualLine = perpetualLineRepository.save(dbPerpetualLine);
			log.info("createdPerpetualLine : " + createdPerpetualLine);
			perpetualLines.add(createdPerpetualLine);
			
			//----------Discussed to Remove this update on 06-07-2022------------------------------------------------
			/*
			 * On Successful Cycle count creation, fetch ST_BIN from PERPETUALLINE for the selected WH_ID/CC_NO and
			 * Pass WH_ID/ ST_BIN in STORAGEBIN table and update PUTAWAY_BLOCK,PICK_BLOCK values with "TRUE"
			 */
//			StorageBin modifiedStorageBin = new StorageBin();
//			modifiedStorageBin.setPutawayBlock(1);
//			modifiedStorageBin.setPickingBlock(1);
//			StorageBin updatedStorageBin = mastersService.updateStorageBin(createdPerpetualLine.getStorageBin(), 
//					modifiedStorageBin, loginUserID, authTokenForMastersService.getAccess_token());
//			log.info("updatedStorageBin : " + updatedStorageBin);
			//----------Discussed to Remove this update on 06-07-2022------------------------------------------------
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
		try {
			// Update Line Details
			List<PerpetualLine> lines = perpetualLineService.updatePerpetualLineForMobileCount (updatePerpetualHeader.getUpdatePerpetualLine(), loginUserID);
			log.info("Lines Updated : " + lines);
			
			PerpetualHeader dbPerpetualHeader = getPerpetualHeaderRecord(warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId, 
					subMovementTypeId);
			BeanUtils.copyProperties(updatePerpetualHeader, dbPerpetualHeader, CommonUtils.getNullPropertyNames(updatePerpetualHeader));
			
			/*
			 * Pass CC_NO in PERPETUALLINE table and validate STATUS_ID of the selected records. 
			 * 1. If STATUS_ID=78 for all the selected records, update STATUS_ID of PERPETUALHEADER table as "78" by passing CC_NO
			 * 2. If STATUS_ID=74 for all the selected records, Update STATUS_ID of PERPETUALHEADER table as "74" by passing CC_NO
			 * Else Update STATUS_ID as "73"
			 */
			List<PerpetualLine> perpetualLines = perpetualLineService.getPerpetualLine (cycleCountNo);
			long count_78 = perpetualLines.stream().filter(a->a.getStatusId() == 78L).count();
			long count_74 = perpetualLines.stream().filter(a->a.getStatusId() == 74L).count();
			
			if (perpetualLines.size() == count_78) {
				dbPerpetualHeader.setStatusId(78L);
			} else if (perpetualLines.size() == count_74) {
				dbPerpetualHeader.setStatusId(74L);
			} else {
				dbPerpetualHeader.setStatusId(73L);
			}
			
			dbPerpetualHeader.setCountedBy(loginUserID);
			dbPerpetualHeader.setCountedOn(new Date());
			return perpetualHeaderRepository.save(dbPerpetualHeader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; 
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
		if ( dbPerpetualHeader != null && dbPerpetualHeader.getStatusId() == 70L) {
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
	 * @param lineStatusId 
	 * @param list 
	 * @return
	 */
	private List<PerpetualHeaderEntity> convertToEntity (List<PerpetualHeader> perpetualHeaderList, 
			List<String> cycleCounterId, List<Long> lineStatusId) {
		try {
			List<PerpetualHeaderEntity> listPerpetualHeaderEntity = new ArrayList<>();
			for (PerpetualHeader perpetualHeader : perpetualHeaderList) {
				SearchPerpetualLine searchPerpetualLine = new SearchPerpetualLine(); 
				searchPerpetualLine.setCycleCountNo(perpetualHeader.getCycleCountNo());
				
				if (cycleCounterId != null) {
					searchPerpetualLine.setCycleCounterId(cycleCounterId);
				}
				
				if (lineStatusId != null) {
					searchPerpetualLine.setLineStatusId(lineStatusId);
				}
				
				PerpetualLineSpecification spec = new PerpetualLineSpecification (searchPerpetualLine);
				List<PerpetualLine> perpetualLineList = perpetualLineRepository.findAll(spec);
//				log.info("perpetualLineList: " + perpetualLineList);
				
				List<PerpetualLineEntity> listPerpetualLineEntity = new ArrayList<>();
				for (PerpetualLine perpetualLine : perpetualLineList) {
					if (perpetualHeader.getCycleCountNo().equalsIgnoreCase(perpetualLine.getCycleCountNo())) {
						PerpetualLineEntity perpetualLineEntity = new PerpetualLineEntity();
						BeanUtils.copyProperties(perpetualLine, perpetualLineEntity, CommonUtils.getNullPropertyNames(perpetualLine));
						listPerpetualLineEntity.add(perpetualLineEntity);
					}
				}
				
				PerpetualHeaderEntity perpetualHeaderEntity = new PerpetualHeaderEntity();
				BeanUtils.copyProperties(perpetualHeader, perpetualHeaderEntity, CommonUtils.getNullPropertyNames(perpetualHeader));
				perpetualHeaderEntity.setPerpetualLine(listPerpetualLineEntity);
				listPerpetualHeaderEntity.add(perpetualHeaderEntity);
			}
			return listPerpetualHeaderEntity;
		} catch (BeansException e) {
			e.printStackTrace();
		}
		return null;
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
	private PerpetualHeader convertToEntity (PerpetualHeader perpetualHeader) {
		List<PerpetualLine> perpetualLineList = perpetualLineService.getPerpetualLine(perpetualHeader.getCycleCountNo());
		log.info("perpetualLineList found: " + perpetualLineList);
		perpetualHeader.setPerpetualLine(perpetualLineList);
		return perpetualHeader;
	}
}
