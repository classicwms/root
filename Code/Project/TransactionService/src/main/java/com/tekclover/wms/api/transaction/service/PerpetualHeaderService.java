package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.AddPerpetualHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.AddPerpetualLine;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualHeaderEntity;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualLine;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualLineEntity;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualLineEntityImpl;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.RunPerpetualHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.SearchPerpetualHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.UpdatePerpetualHeader;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.transaction.repository.PerpetualHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PerpetualLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.PerpetualHeaderSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

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

	@Autowired
	JdbcTemplate jdbcTemplate;
	
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

	
	/**
	 * Performance enhanced
	 * ------------------------------------
	 * @param runPerpetualHeader
	 * @return
	 * @throws java.text.ParseException
	 */
	public Set<PerpetualLineEntityImpl> runPerpetualHeaderNew(@Valid RunPerpetualHeader runPerpetualHeader) throws java.text.ParseException {
		if (runPerpetualHeader.getDateFrom() != null && runPerpetualHeader.getDateFrom() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(runPerpetualHeader.getDateFrom(), 	runPerpetualHeader.getDateTo());
			runPerpetualHeader.setDateFrom(dates[0]);
			runPerpetualHeader.setDateTo(dates[1]);
		}

		List<PerpetualLineEntityImpl> runResponseList = inventoryMovementRepository.getRecordsForRunPerpetualCount (
				runPerpetualHeader.getMovementTypeId(), runPerpetualHeader.getSubMovementTypeId(),
				runPerpetualHeader.getDateFrom(), runPerpetualHeader.getDateTo());
		
		Set<PerpetualLineEntityImpl> responseList = new HashSet<>();
		for (PerpetualLineEntityImpl line : runResponseList) {
			List<PerpetualLine> dbPerpetualLines = perpetualLineRepository.findByWarehouseIdAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator (
					line.getWarehouseId(), line.getStorageBin(), line.getItemCode(), line.getPackBarcodes(), 0L);
			log.info ("dbPerpetualLines---queried---> : " + dbPerpetualLines);
			
			long count_78 = dbPerpetualLines.stream().filter(a->a.getStatusId() == 78L).count();
			if (dbPerpetualLines.size() == count_78) {
				log.info ("---#1--78----condi-----> : " + line);
				responseList.add(line);
			}
			
			if (dbPerpetualLines != null && dbPerpetualLines.isEmpty()) {
				log.info ("---#2--78----condi-----> : " + line);
				responseList.add(line);
			}
		}
		log.info ("runResponseList---trimmed---> : " + responseList);
		return responseList;
	}
	/**
	 * Performance enhanced - Streaming
	 * ------------------------------------
	 * @param runPerpetualHeader
	 * @return
	 * @throws java.text.ParseException
	 */
	@Transactional
	public Set<PerpetualLineEntityImpl> runPerpetualHeaderStream(@Valid RunPerpetualHeader runPerpetualHeader) throws java.text.ParseException {
		if (runPerpetualHeader.getDateFrom() != null && runPerpetualHeader.getDateFrom() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(runPerpetualHeader.getDateFrom(), 	runPerpetualHeader.getDateTo());
			runPerpetualHeader.setDateFrom(dates[0]);
			runPerpetualHeader.setDateTo(dates[1]);
		}

		Stream<PerpetualLineEntityImpl> runResponseList = inventoryMovementRepository.getRecordsForRunPerpetualCountStream (
				runPerpetualHeader.getMovementTypeId(), runPerpetualHeader.getWarehouseId(), runPerpetualHeader.getSubMovementTypeId(),
				runPerpetualHeader.getDateFrom(), runPerpetualHeader.getDateTo());

		Set<PerpetualLineEntityImpl> responseList = new HashSet<>();

		runResponseList.forEach(n -> {

				List<PerpetualLine> dbPerpetualLines = perpetualLineRepository
						.findByWarehouseIdAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator (
								n.getWarehouseId(),
								n.getStorageBin(),
								n.getItemCode(),
								n.getPackBarcodes(), 0L);
				log.info ("dbPerpetualLines---queried---> : " + dbPerpetualLines);

				long count_78 = dbPerpetualLines.stream().filter(a->a.getStatusId() == 78L).count();
				if (dbPerpetualLines.size() == count_78) {
					log.info ("---#1--78----condi-----> : " + n);
					responseList.add(n);
				}

				if (dbPerpetualLines != null && dbPerpetualLines.isEmpty()) {
					log.info ("---#2--78----condi-----> : " + n);
					responseList.add(n);
				}
				}
		);

		log.info ("runResponseList---trimmed---> : " + responseList);

		return responseList;
	}
	
	/**
	 * createPerpetualHeader
	 * @param newPerpetualHeader
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PerpetualHeaderEntity createPerpetualHeader (AddPerpetualHeader newPerpetualHeader, String loginUserID) 
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
			log.info("Manu Part No : " + newPerpetualLine.getManufacturerPartNo());
			log.info("itemDex : " + newPerpetualLine.getItemDesc());
			log.info("storage secIds : " + newPerpetualLine.getStorageSectionId());
			
			PerpetualLine dbPerpetualLine = new PerpetualLine();
			BeanUtils.copyProperties(newPerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(newPerpetualLine));
			dbPerpetualLine.setLanguageId(getLanguageId());
			dbPerpetualLine.setCompanyCodeId(getCompanyCode());
			dbPerpetualLine.setPlantId(getPlantId());
			dbPerpetualLine.setWarehouseId(createdPerpetualHeader.getWarehouseId());
			dbPerpetualLine.setCycleCountNo(createdPerpetualHeader.getCycleCountNo());
			dbPerpetualLine.setStatusId(70L);
			dbPerpetualLine.setDeletionIndicator(0L);
			dbPerpetualLine.setCreatedBy(loginUserID);
			dbPerpetualLine.setCreatedOn(new Date());
			perpetualLines.add(dbPerpetualLine);
		}
//		List<PerpetualLine> createdPerpetualLines = perpetualLineRepository.saveAll(perpetualLines);
//		log.info("createdPerpetualLine : " + createdPerpetualLines);
		log.info("createdPerpetualLine : " + perpetualLines);
		batchInsert(perpetualLines);
		// Return Response
//		List<PerpetualLineEntity> listPerpetualLineEntity = new ArrayList<>();
//		for (PerpetualLine perpetualLine : createdPerpetualLines) {
//			PerpetualLineEntity perpetualLineEntity = new PerpetualLineEntity();
//			BeanUtils.copyProperties(perpetualLine, perpetualLineEntity, CommonUtils.getNullPropertyNames(perpetualLine));
//			listPerpetualLineEntity.add(perpetualLineEntity);
//		}
		
		PerpetualHeaderEntity perpetualHeaderEntity = new PerpetualHeaderEntity();
		BeanUtils.copyProperties(createdPerpetualHeader, perpetualHeaderEntity, CommonUtils.getNullPropertyNames(createdPerpetualHeader));
//		perpetualHeaderEntity.setPerpetualLine(listPerpetualLineEntity);
		
		return perpetualHeaderEntity;
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
	 * 
	 * @param warehouseId
	 * @param cycleCountTypeId
	 * @param cycleCountNo
	 * @param movementTypeId
	 * @param subMovementTypeId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PerpetualHeader updatePerpetualHeader (String warehouseId, Long cycleCountTypeId, String cycleCountNo, Long movementTypeId, 
			Long subMovementTypeId, String loginUserID) throws IllegalAccessException, InvocationTargetException {
		PerpetualHeader dbPerpetualHeader = getPerpetualHeaderRecord(warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId, subMovementTypeId);
		
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
//	private List<PerpetualHeaderEntity> convertToEntity (List<PerpetualHeader> perpetualHeaderList, 
//			List<String> cycleCounterId, List<Long> lineStatusId) {
//		try {
//			List<PerpetualHeaderEntity> listPerpetualHeaderEntity = new ArrayList<>();
//			for (PerpetualHeader perpetualHeader : perpetualHeaderList) {
//				SearchPerpetualLine searchPerpetualLine = new SearchPerpetualLine(); 
//				searchPerpetualLine.setCycleCountNo(Arrays.asList(perpetualHeader.getCycleCountNo()));
//				
//				if (cycleCounterId != null && !cycleCounterId.isEmpty()) {
//					searchPerpetualLine.setCycleCounterId(cycleCounterId.get(0));
//				}
//				
//				if (lineStatusId != null) {
//					searchPerpetualLine.setLineStatusId(lineStatusId);
//				}
//				
//				PerpetualLineSpecification spec = new PerpetualLineSpecification (searchPerpetualLine);
//				List<PerpetualLine> perpetualLineList = perpetualLineRepository.findAll(spec);
////				log.info("perpetualLineList: " + perpetualLineList);
//				
//				List<PerpetualLineEntity> listPerpetualLineEntity = new ArrayList<>();
//				for (PerpetualLine perpetualLine : perpetualLineList) {
//					if (perpetualHeader.getCycleCountNo().equalsIgnoreCase(perpetualLine.getCycleCountNo())) {
//						PerpetualLineEntity perpetualLineEntity = new PerpetualLineEntity();
//						BeanUtils.copyProperties(perpetualLine, perpetualLineEntity, CommonUtils.getNullPropertyNames(perpetualLine));
//						listPerpetualLineEntity.add(perpetualLineEntity);
//					}
//				}
//				
//				PerpetualHeaderEntity perpetualHeaderEntity = new PerpetualHeaderEntity();
//				BeanUtils.copyProperties(perpetualHeader, perpetualHeaderEntity, CommonUtils.getNullPropertyNames(perpetualHeader));
//				perpetualHeaderEntity.setPerpetualLine(listPerpetualLineEntity);
//				listPerpetualHeaderEntity.add(perpetualHeaderEntity);
//			}
//			return listPerpetualHeaderEntity;
//		} catch (BeansException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
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
		perpetualHeader.setPerpetualLine(perpetualLineList);
		return perpetualHeader;
	}

	public int[][] batchInsert(List<PerpetualLine> perpetualLineList) {

		int batchSize=500;

		int[][] updateCounts = jdbcTemplate.batchUpdate(
				"insert into tblperpetualline (LANG_ID, C_ID, PLANT_ID, WH_ID,\n" +
						"CC_NO, ST_BIN, ITM_CODE, PACK_BARCODE, \n" +
						"ITM_DESC, MFR_PART, VAR_ID, VAR_SUB_ID, \n" +
						"STR_NO, STCK_TYP_ID, SP_ST_IND_ID, ST_SEC_ID, \n" +
						"INV_QTY, INV_UOM, CTD_QTY, VAR_QTY, \n" +
						"COUNTER_ID, COUNTER_NM, STATUS_ID, ACTION, \n" +
						"REF_NO, APP_PROCESS_ID, APP_LVL, APP_CODE, \n" +
						"APP_STATUS, REMARK, REF_FIELD_1, REF_FIELD_2, \n" +
						"REF_FIELD_3, REF_FIELD_4, REF_FIELD_5, REF_FIELD_6, \n" +
						"REF_FIELD_7, REF_FIELD_8, REF_FIELD_9, REF_FIELD_10, \n" +
						"IS_DELETED, CC_CTD_BY, CC_CTD_ON, CC_CNF_BY, \n" +
						"CC_CNF_ON, CC_CNT_BY, CC_CNT_ON) \n" +
						"values(?,?,?,?, \n" +
						"?,?,?,?, \n"+
						"?,?,?,?, \n"+
						"?,?,?,?, \n"+
						"?,?,?,?, \n"+
						"?,?,?,?, \n"+
						"?,?,?,?, \n"+
						"?,?,?,?, \n"+
						"?,?,?,?, \n"+
						"?,?,?,?, \n"+
						"?,?,?,?, \n"+
						"?,?,?)", perpetualLineList, batchSize,
				new ParameterizedPreparedStatementSetter<PerpetualLine>() {
					public void setValues(PreparedStatement ps, PerpetualLine perpetualLine) throws SQLException {
						ps.setString(1, perpetualLine.getLanguageId());
						ps.setString(2, perpetualLine.getCompanyCodeId());
						ps.setString(3, perpetualLine.getPlantId());
						ps.setString(4, perpetualLine.getWarehouseId());
						ps.setString(5, perpetualLine.getCycleCountNo());
						ps.setString(6, perpetualLine.getStorageBin());
						ps.setString(7, perpetualLine.getItemCode());
						ps.setString(8, perpetualLine.getPackBarcodes());

						ps.setString(9, perpetualLine.getItemDesc());
						ps.setString(10, perpetualLine.getManufacturerPartNo());

						if(perpetualLine.getVariantCode() != null) {
							ps.setLong(11, perpetualLine.getVariantCode());
						} else {
							ps.setLong(11, 0L);
						}

						ps.setString(12, perpetualLine.getVariantSubCode());
						ps.setString(13, perpetualLine.getBatchSerialNumber());

						if(perpetualLine.getStockTypeId() != null) {
							ps.setLong(14, perpetualLine.getStockTypeId());
						} else {
							ps.setLong(14, 0L);
						}

						ps.setString(15, perpetualLine.getSpecialStockIndicator());
						ps.setString(16, perpetualLine.getStorageSectionId());

						if(perpetualLine.getInventoryQuantity() != null) {
							ps.setDouble(17, perpetualLine.getInventoryQuantity());
						} else {
							ps.setDouble(17, 0D);
						}

						ps.setString(18, perpetualLine.getInventoryUom());

						if(perpetualLine.getCountedQty() != null) {
							ps.setDouble(19, perpetualLine.getCountedQty());
						} else {
							ps.setDouble(19, 0D);
						}

						if(perpetualLine.getVarianceQty() != null) {
							ps.setDouble(20, perpetualLine.getVarianceQty());
						} else {
							ps.setDouble(20, 0D);
						}

						ps.setString(21, perpetualLine.getCycleCounterId());
						ps.setString(22, perpetualLine.getCycleCounterName());
						if(perpetualLine.getStatusId() != null) {
							ps.setLong(23, perpetualLine.getStatusId());
						} else {
							ps.setLong(23, 0L);
						}

						ps.setString(24, perpetualLine.getCycleCountAction());
						ps.setString(25, perpetualLine.getReferenceNo());
						if(perpetualLine.getApprovalProcessId() != null) {
							ps.setLong(26, perpetualLine.getApprovalProcessId());
						} else {
							ps.setLong(26, 0L);
						}

						ps.setString(27, perpetualLine.getApprovalLevel());
						ps.setString(28, perpetualLine.getApproverCode());
						ps.setString(29, perpetualLine.getApprovalStatus());
						ps.setString(30, perpetualLine.getRemarks());
						ps.setString(31, perpetualLine.getReferenceField1());
						ps.setString(32, perpetualLine.getReferenceField2());
						ps.setString(33, perpetualLine.getReferenceField3());
						ps.setString(34, perpetualLine.getReferenceField4());
						ps.setString(35, perpetualLine.getReferenceField5());
						ps.setString(36, perpetualLine.getReferenceField6());
						ps.setString(37, perpetualLine.getReferenceField7());
						ps.setString(38, perpetualLine.getReferenceField8());
						ps.setString(39, perpetualLine.getReferenceField9());
						ps.setString(40, perpetualLine.getReferenceField10());

						if(perpetualLine.getDeletionIndicator() != null) {
							ps.setLong(41, perpetualLine.getDeletionIndicator());
						} else {
							ps.setLong(41,0L);
						}

						ps.setString(42, perpetualLine.getCreatedBy());

						if(perpetualLine.getCreatedOn() != null) {
							ps.setDate(43, new java.sql.Date(perpetualLine.getCreatedOn().getTime()));
						} else {
							ps.setDate(43,new java.sql.Date(new Date().getTime()));
						}

						ps.setString(44, perpetualLine.getConfirmedBy());

						if(perpetualLine.getConfirmedOn() != null) {
							ps.setDate(45, new java.sql.Date(perpetualLine.getConfirmedOn().getTime()));
						} else {
							ps.setDate(45, new java.sql.Date(new Date().getTime()));
						}

						ps.setString(46, perpetualLine.getCountedBy());

						if(perpetualLine.getCountedOn() != null) {
							ps.setDate(47, new java.sql.Date(perpetualLine.getCountedOn().getTime()));
						} else {
							ps.setDate(47, new java.sql.Date(new Date().getTime()));
						}

					}
				});
		return updateCounts;
	}
}
