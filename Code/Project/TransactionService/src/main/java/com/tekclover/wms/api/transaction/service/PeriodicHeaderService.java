package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.AddPeriodicHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.AddPeriodicLine;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicHeaderEntity;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicLine;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicLineEntity;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.SearchPeriodicHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.SearchPeriodicLine;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.UpdatePeriodicHeader;
import com.tekclover.wms.api.transaction.model.dto.IInventory;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.PeriodicHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PeriodicLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.PeriodicHeaderSpecification;
import com.tekclover.wms.api.transaction.repository.specification.PeriodicLineSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PeriodicHeaderService extends BaseService {
	
	@Autowired
	AuthTokenService authTokenService;
	
	@Autowired
	InventoryService inventoryService;
	
	@Autowired
	MastersService mastersService;
	
	@Autowired
	PeriodicLineService periodicLineService;

	@Autowired
	PeriodicHeaderRepository periodicHeaderRepository;
	
	@Autowired
	PeriodicLineRepository periodicLineRepository;
	
	@Autowired
	InventoryRepository inventoryRepository;

	/**
	 * getPeriodicHeaders
	 * @return
	 */
	public List<PeriodicHeaderEntity> getPeriodicHeaders() {
		List<PeriodicHeader> periodicHeaderList = periodicHeaderRepository.findAll();
		periodicHeaderList = periodicHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return convertToEntity (periodicHeaderList);
	}

	/**
	 * getPeriodicHeader
	 * @param cycleCountTypeId
	 * @return
	 */
	public PeriodicHeader getPeriodicHeader(String warehouseId, Long cycleCountTypeId, String cycleCountNo) {
		PeriodicHeader periodicHeader = 
				periodicHeaderRepository.findByCompanyCodeAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNo(
						getCompanyCode(), getPlantId(), warehouseId, cycleCountTypeId, cycleCountNo);
		if (periodicHeader != null && periodicHeader.getDeletionIndicator() == 0) {
			return periodicHeader;
		}
		throw new BadRequestException("The given PeriodicHeader ID : " + cycleCountTypeId + " doesn't exist.");
	}

	/**
	 * 
	 * @param searchPeriodicHeader
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException 
	 */
	public List<PeriodicHeaderEntity> findPeriodicHeader(SearchPeriodicHeader searchPeriodicHeader) 
			throws ParseException, java.text.ParseException {
		if (searchPeriodicHeader.getStartCreatedOn() != null && searchPeriodicHeader.getStartCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPeriodicHeader.getStartCreatedOn(),
					searchPeriodicHeader.getEndCreatedOn());
			searchPeriodicHeader.setStartCreatedOn(dates[0]);
			searchPeriodicHeader.setEndCreatedOn(dates[1]);
		}
		PeriodicHeaderSpecification spec = new PeriodicHeaderSpecification(searchPeriodicHeader);
		List<PeriodicHeader> periodicHeaderResults = periodicHeaderRepository.findAll(spec);
		return convertToEntity (periodicHeaderResults, searchPeriodicHeader);
	}
	
	/**
	 * 
	 * @param periodicheaderList
	 * @return
	 */
	private List<PeriodicHeaderEntity> convertToEntity (List<PeriodicHeader> periodicheaderList, SearchPeriodicHeader searchPeriodicHeader) {
		List<PeriodicHeaderEntity> listPeriodicHeaderEntity = new ArrayList<>();
		for (PeriodicHeader periodicheader : periodicheaderList) {
			SearchPeriodicLine searchPeriodicLine = new SearchPeriodicLine();
			searchPeriodicLine.setCycleCountNo(periodicheader.getCycleCountNo());
			
			if (searchPeriodicHeader.getCycleCounterId() != null) {
				searchPeriodicLine.setCycleCounterId(searchPeriodicHeader.getCycleCounterId());
			}
			
			if (searchPeriodicHeader.getLineStatusId() != null) {
				searchPeriodicLine.setLineStatusId(searchPeriodicHeader.getLineStatusId());
			}
			
			PeriodicLineSpecification spec = new PeriodicLineSpecification (searchPeriodicLine);
			List<PeriodicLine> periodicLineList = periodicLineRepository.findAll(spec);
//			log.info("periodicLineList: " + periodicLineList);
			
			List<PeriodicLineEntity> listPeriodicLineEntity = new ArrayList<>();
			for (PeriodicLine periodicLine : periodicLineList) {
				PeriodicLineEntity perpetualLineEntity = new PeriodicLineEntity();
				BeanUtils.copyProperties(periodicLine, perpetualLineEntity, CommonUtils.getNullPropertyNames(periodicLine));
				listPeriodicLineEntity.add(perpetualLineEntity);
			}
			
			PeriodicHeaderEntity periodicheaderEntity = new PeriodicHeaderEntity();
			BeanUtils.copyProperties(periodicheader, periodicheaderEntity, CommonUtils.getNullPropertyNames(periodicheader));
			periodicheaderEntity.setPeriodicLine(listPeriodicLineEntity);
			listPeriodicHeaderEntity.add(periodicheaderEntity);
		}
		return listPeriodicHeaderEntity;
	}
	
	/**
	 * 
	 * @param periodicheaderList
	 * @return
	 */
	private List<PeriodicHeaderEntity> convertToEntity (List<PeriodicHeader> periodicheaderList) {
		List<PeriodicHeaderEntity> listPeriodicHeaderEntity = new ArrayList<>();
		for (PeriodicHeader periodicheader : periodicheaderList) {
			List<PeriodicLine> periodicLineList = periodicLineService.getPeriodicLine(periodicheader.getCycleCountNo());
			List<PeriodicLineEntity> listPeriodicLineEntity = new ArrayList<>();
			for (PeriodicLine periodicLine : periodicLineList) {
				PeriodicLineEntity perpetualLineEntity = new PeriodicLineEntity();
				BeanUtils.copyProperties(periodicLine, perpetualLineEntity, CommonUtils.getNullPropertyNames(periodicLine));
				listPeriodicLineEntity.add(perpetualLineEntity);
			}
			
			PeriodicHeaderEntity periodicheaderEntity = new PeriodicHeaderEntity();
			BeanUtils.copyProperties(periodicheader, periodicheaderEntity, CommonUtils.getNullPropertyNames(periodicheader));
			periodicheaderEntity.setPeriodicLine(listPeriodicLineEntity);
			listPeriodicHeaderEntity.add(periodicheaderEntity);
		}
		return listPeriodicHeaderEntity;
	}
	
	/**
	 * 
	 * @param periodicheader
	 * @return
	 */
	private PeriodicHeaderEntity convertToEntity (PeriodicHeader periodicheader) {
		List<PeriodicLine> perpetualLineList = periodicLineService.getPeriodicLine(periodicheader.getCycleCountNo());
		
		List<PeriodicLineEntity> listPeriodicLineEntity = new ArrayList<>();
		for (PeriodicLine periodicLine : perpetualLineList) {
			PeriodicLineEntity perpetualLineEntity = new PeriodicLineEntity();
			BeanUtils.copyProperties(periodicLine, perpetualLineEntity, CommonUtils.getNullPropertyNames(periodicLine));
			listPeriodicLineEntity.add(perpetualLineEntity);
		}
		
		PeriodicHeaderEntity periodicheaderEntity = new PeriodicHeaderEntity();
		BeanUtils.copyProperties(periodicheader, periodicheaderEntity, CommonUtils.getNullPropertyNames(periodicheader));
		periodicheaderEntity.setPeriodicLine(listPeriodicLineEntity);
		return periodicheaderEntity;
	}
	
	/**
	 * Pass the selected ST_SEC_ID values into STORAGEBIN table and fetch ST_BIN
	 * values Pass the fetched WH_ID/ST_BIN values into INVENOTRY tables and fetch
	 * the below values
	 * -----------------------------------------------------------------------------------------
	 * 
	 * @param warehouseId
	 * @param stSecIds
	 * @param sortBy 
	 * @param pageSize 
	 * @param pageNo 
	 * @return
	 */
	public List<PeriodicLineEntity> runPeriodicHeader(String warehouseId, List<String> stSecIds) {
		List<PeriodicLineEntity> globalPeriodicLineList = new ArrayList<>();
		try {
			AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
			StorageBin[] storageBin = mastersService.getStorageBinBySectionId(stSecIds, authTokenForMastersService.getAccess_token());
			if (storageBin == null) {
				throw new BadRequestException("Storage Bin returned as null");
			}
			
			List<String> stBins = Arrays.asList(storageBin).stream().map(StorageBin::getStorageBin).collect(Collectors.toList());
			log.info("stBins------> : " + stBins);
			if (stBins != null && stBins.size() > 1000) {
				List[] splitLists = split (stBins);
				
				for (List<String> stbinList : splitLists) {
					List<PeriodicLineEntity> invList = getInventory (warehouseId, stbinList, authTokenForMastersService);
					globalPeriodicLineList.addAll(invList);
				}
			} else {
				globalPeriodicLineList = getInventory (warehouseId, stBins, authTokenForMastersService);
			}
			
			return globalPeriodicLineList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param stbinList
	 * @param authTokenForMastersService
	 * @return
	 */
	private List<PeriodicLineEntity> getInventory (String warehouseId, List<String> stbinList, AuthToken authTokenForMastersService) {
		List<PeriodicLineEntity> periodicLineList = new ArrayList<>();
		List<Inventory> inventoryList = inventoryService.getInventoryByStorageBin (warehouseId, stbinList);
		log.info("inventoryList--size----> : " + inventoryList.size());
		for (Inventory inventory : inventoryList) {
			PeriodicLineEntity periodicLine = new PeriodicLineEntity();
			
			periodicLine.setLanguageId(inventory.getLanguageId());
			periodicLine.setCompanyCode(inventory.getCompanyCodeId());
			periodicLine.setPlantId(inventory.getPlantId());
			periodicLine.setWarehouseId(inventory.getWarehouseId());
			
			// ITM_CODE
			periodicLine.setItemCode(inventory.getItemCode());
			
			// Pass ITM_CODE in IMBASICDATA table and fetch ITEM_TEXT values
			ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCode(inventory.getItemCode(), 
					inventory.getWarehouseId(), authTokenForMastersService.getAccess_token());
			periodicLine.setItemDesc(imBasicData1.getDescription());
			
			// ST_BIN
			periodicLine.setStorageBin(inventory.getStorageBin());
			
			// ST_SEC_ID/ST_SEC
			// Pass the ST_BIN in STORAGEBIN table and fetch ST_SEC_ID/ST_SEC values
			StorageBin dbStorageBin = mastersService.getStorageBin(inventory.getStorageBin(),
					authTokenForMastersService.getAccess_token());
			periodicLine.setStorageSectionId(dbStorageBin.getStorageSectionId());
			
			// MFR_PART
			// Pass ITM_CODE in IMBASICDATA table and fetch MFR_PART values
			periodicLine.setManufacturerPartNo(imBasicData1.getManufacturerPartNo());
			
			// STCK_TYP_ID
			periodicLine.setStockTypeId(inventory.getStockTypeId());
			
			// SP_ST_IND_ID
			periodicLine.setSpecialStockIndicator(inventory.getSpecialStockIndicatorId());
			
			// PACK_BARCODE
			periodicLine.setPackBarcodes(inventory.getPackBarcodes());
			
			/*
			 * INV_QTY
			 * -------------
			 * Pass the filled WH_ID/ITM_CODE/PACK_BARCODE/ST_BIN
			 * values in INVENTORY table and fetch INV_QTY/INV_UOM values and 
			 * fill against each ITM_CODE values and this is non-editable"
			 */
//			Inventory dbInventory = inventoryService.getInventory(inventory.getWarehouseId(), 
//					inventory.getPackBarcodes(), inventory.getItemCode(), inventory.getStorageBin());
//			log.info("dbInventory : " + dbInventory);
			
			IInventory dbInventory = inventoryRepository.findInventoryForPeriodicRun (inventory.getWarehouseId(), 
					inventory.getPackBarcodes(), inventory.getItemCode(), inventory.getStorageBin());
			
			if (dbInventory != null) {
				periodicLine.setInventoryQuantity(inventory.getInventoryQuantity());
				periodicLine.setInventoryUom(inventory.getInventoryUom());
			}
			periodicLineList.add(periodicLine);
		}
		return periodicLineList;
	}
	
	/**
	 * 
	 * @param list
	 * @return
	 */
	public static List[] split (List<String> list) {
        int size = list.size();
        List<String> first = new ArrayList<>(list.subList(0, (size) / 2));
        List<String> second = new ArrayList<>(list.subList((size) / 2, size));
        return new List[] { first, second };
    }
	
//	public static void main(String[] args) {
//		List<String> list = new ArrayList<String>();
//		 
//        list.add("Geeks");
//        list.add("Practice");
//        list.add("Contribute");
//        list.add("IDE");
//        list.add("Courses");
//        
//        List[] l = split (list);
//        log.info("A1 : " + l[0]);
//        log.info("A2 : " + l[1]);
//	}

	/**
	 * createPeriodicHeader
	 * @param newPeriodicHeader
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PeriodicHeaderEntity createPeriodicHeader(AddPeriodicHeader newPeriodicHeader, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		PeriodicHeader dbPeriodicHeader = new PeriodicHeader();
		BeanUtils.copyProperties(newPeriodicHeader, dbPeriodicHeader, CommonUtils.getNullPropertyNames(newPeriodicHeader));
		dbPeriodicHeader.setLanguageId(getLanguageId());
		dbPeriodicHeader.setCompanyCode(getCompanyCode());
		dbPeriodicHeader.setPlantId(getPlantId());
		
		/*
		 * Cycle Count No
		 * --------------------
		 * Pass WH_ID - User logged in WH_ID and NUM_RAN_ID=15 values in NUMBERRANGE table and fetch NUM_RAN_CURRENT value 
		 * and add +1 and then update in PERIODICHEADER table during Save
		 */
		AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
		long NUM_RAN_ID = 15;
		String nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, newPeriodicHeader.getWarehouseId(),
				authTokenForIDMasterService.getAccess_token());
		dbPeriodicHeader.setCycleCountNo(nextRangeNumber);
		
		// CC_TYP_ID - HardCoded Value "01"
		dbPeriodicHeader.setCycleCountTypeId(1L);
		
		// STATUS_ID - HardCoded Value "70"
		dbPeriodicHeader.setStatusId(70L);
		dbPeriodicHeader.setDeletionIndicator(0L);
		dbPeriodicHeader.setCreatedBy(loginUserID);
		dbPeriodicHeader.setCreatedOn(new Date());
		dbPeriodicHeader.setConfirmedBy(loginUserID);
		dbPeriodicHeader.setConfirmedOn(new Date());
		PeriodicHeader createdPeriodicHeader = periodicHeaderRepository.save(dbPeriodicHeader);
		List<PeriodicLine> periodicLineList = new ArrayList<>();
		for (AddPeriodicLine newPeriodicLine : newPeriodicHeader.getAddPeriodicLine()) {
			PeriodicLine dbPeriodicLine = new PeriodicLine();
			BeanUtils.copyProperties(newPeriodicLine, dbPeriodicLine, CommonUtils.getNullPropertyNames(newPeriodicLine));
			
			// LANG_ID
			dbPeriodicLine.setLanguageId(getLanguageId());
			
			// WH_ID
			dbPeriodicLine.setWarehouseId(createdPeriodicHeader.getWarehouseId());
			
			// C_ID
			dbPeriodicLine.setCompanyCode(createdPeriodicHeader.getCompanyCode());
			
			// PLANT_ID
			dbPeriodicLine.setPlantId(createdPeriodicHeader.getPlantId());
			
			// CC_NO
			dbPeriodicLine.setCycleCountNo(createdPeriodicHeader.getCycleCountNo());
			dbPeriodicLine.setStatusId(70L);
			dbPeriodicLine.setDeletionIndicator(0L);
			dbPeriodicLine.setCreatedBy(loginUserID);
			dbPeriodicLine.setCreatedOn(new Date());
			PeriodicLine createdPeriodicLine = periodicLineRepository.save(dbPeriodicLine);
			log.info("createdPeriodicLine : " + createdPeriodicLine);
			periodicLineList.add(createdPeriodicLine);
		}
		PeriodicHeaderEntity periodicheaderEntity = new PeriodicHeaderEntity();
		BeanUtils.copyProperties(createdPeriodicHeader, periodicheaderEntity, CommonUtils.getNullPropertyNames(createdPeriodicHeader));
		
		List<PeriodicLineEntity> listPeriodicLineEntity = new ArrayList<>();
		for (PeriodicLine periodicLine : periodicLineList) {
			PeriodicLineEntity perpetualLineEntity = new PeriodicLineEntity();
			BeanUtils.copyProperties(periodicLine, perpetualLineEntity, CommonUtils.getNullPropertyNames(periodicLine));
			listPeriodicLineEntity.add(perpetualLineEntity);
		}
		
		periodicheaderEntity.setPeriodicLine(listPeriodicLineEntity);
		return periodicheaderEntity;
	}

	/**
	 * updatePeriodicHeader
	 * @param loginUserId
	 * @param cycleCountTypeId
	 * @param updatePeriodicHeader
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PeriodicHeader updatePeriodicHeader (String warehouseId, Long cycleCountTypeId, String cycleCountNo, 
			String loginUserID, UpdatePeriodicHeader updatePeriodicHeader) 
			throws IllegalAccessException, InvocationTargetException {
		// Update Line Details
		List<PeriodicLine> lines = periodicLineService.updatePeriodicLineForMobileCount (updatePeriodicHeader.getUpdatePeriodicLine(), loginUserID);
		log.info("Lines Updated : " + lines);
		
		PeriodicHeader dbPeriodicHeader = getPeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo);
		BeanUtils.copyProperties(updatePeriodicHeader, dbPeriodicHeader, CommonUtils.getNullPropertyNames(updatePeriodicHeader));
		
		/*
		 * Pass CC_NO in PERPETUALLINE table and validate STATUS_ID of the selected records. 
		 * 1. If STATUS_ID=78 for all the selected records, update STATUS_ID of periodicheader table as "78" by passing CC_NO
		 * 2. If STATUS_ID=74 for all the selected records, Update STATUS_ID of periodicheader table as "74" by passing CC_NO
		 * Else Update STATUS_ID as "73"
		 */
		List<PeriodicLine> PeriodicLines = periodicLineService.getPeriodicLine (cycleCountNo);
		long count_78 = PeriodicLines.stream().filter(a->a.getStatusId() == 78L).count();
		long count_74 = PeriodicLines.stream().filter(a->a.getStatusId() == 74L).count();
		
		if (PeriodicLines.size() == count_78) {
			dbPeriodicHeader.setStatusId(78L);
		} else if (PeriodicLines.size() == count_74) {
			dbPeriodicHeader.setStatusId(74L);
		} else {
			dbPeriodicHeader.setStatusId(73L);
		}
		
		dbPeriodicHeader.setCountedBy(loginUserID);
		dbPeriodicHeader.setCountedOn(new Date());
		return periodicHeaderRepository.save(dbPeriodicHeader);
	}

	/**
	 * deletePeriodicHeader
	 * @param loginUserID
	 * @param cycleCountTypeId
	 */
	public void deletePeriodicHeader(String companyCode, String plantId, String warehouseId, Long cycleCountTypeId,
			String cycleCountNo, String loginUserID) {
		PeriodicHeader periodicHeader = getPeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo);
		if (periodicHeader != null) {
			periodicHeader.setDeletionIndicator(1L);
			periodicHeader.setConfirmedBy(loginUserID);
			periodicHeader.setConfirmedOn(new Date());
			periodicHeaderRepository.save(periodicHeader);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + cycleCountTypeId);
		}
	}

	/**
	 * 
	 * @param cycleCountNo
	 * @return
	 */
	public PeriodicHeader getPeriodicHeader(String cycleCountNo) {
		PeriodicHeader periodicHeader = periodicHeaderRepository.findByCycleCountNo(cycleCountNo);
		return periodicHeader;
	}
}
