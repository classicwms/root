package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.expression.ParseException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.AddPeriodicHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicHeaderEntity;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicLine;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicLineEntity;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.SearchPeriodicHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.SearchPeriodicLine;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.UpdatePeriodicHeader;
import com.tekclover.wms.api.transaction.model.dto.IImbasicData1;
import com.tekclover.wms.api.transaction.model.dto.IInventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.repository.ImBasicData1Repository;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.PeriodicHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PeriodicLineRepository;
import com.tekclover.wms.api.transaction.repository.StorageBinRepository;
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
	
	@Autowired
	StorageBinRepository storageBinRepository;
	
	@Autowired
	private ImBasicData1Repository imbasicdata1Repository;

	@Autowired
	JdbcTemplate jdbcTemplate;

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
	 * @throws Exception
	 */
	public List<PeriodicHeaderEntity> findPeriodicHeader(SearchPeriodicHeader searchPeriodicHeader) 
			throws Exception {
		if (searchPeriodicHeader.getStartCreatedOn() != null && searchPeriodicHeader.getStartCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPeriodicHeader.getStartCreatedOn(),
					searchPeriodicHeader.getEndCreatedOn());
			searchPeriodicHeader.setStartCreatedOn(dates[0]);
			searchPeriodicHeader.setEndCreatedOn(dates[1]);
		}
		PeriodicHeaderSpecification spec = new PeriodicHeaderSpecification(searchPeriodicHeader);
		List<PeriodicHeader> periodicHeaderResults = periodicHeaderRepository.findAll(spec);
		List<PeriodicHeaderEntity> periodicHeaderEntityList = convertToEntity (periodicHeaderResults, searchPeriodicHeader);
		return periodicHeaderEntityList;
	}
	/**
	 *
	 * @param searchPeriodicHeader - Stream
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	public Stream<PeriodicHeader> findPeriodicHeaderStream(SearchPeriodicHeader searchPeriodicHeader)
			throws Exception {
		if (searchPeriodicHeader.getStartCreatedOn() != null && searchPeriodicHeader.getStartCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPeriodicHeader.getStartCreatedOn(),
					searchPeriodicHeader.getEndCreatedOn());
			searchPeriodicHeader.setStartCreatedOn(dates[0]);
			searchPeriodicHeader.setEndCreatedOn(dates[1]);
		}
		PeriodicHeaderSpecification spec = new PeriodicHeaderSpecification(searchPeriodicHeader);
		Stream<PeriodicHeader> periodicHeaderResults = periodicHeaderRepository.stream(spec, PeriodicHeader.class);
		return periodicHeaderResults;
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
	 *
	 * @param warehouseId
	 * @param pageNo
	 * @param pageSize
	 * @param sortBy
	 * @return
	 */
	public Page<PeriodicLineEntity> runPeriodicHeader(String warehouseId, Integer pageNo, 
			Integer pageSize, String sortBy) {
		try {
			Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
			Page<Inventory> inventoryList = inventoryRepository.findByWarehouseIdAndDeletionIndicator(warehouseId, 0L, pageable);
			log.info("inventoryList--size----> : " + inventoryList.getTotalElements());
			
			List<PeriodicLineEntity> periodicLineList = new ArrayList<>();
			for (Inventory inventory : inventoryList) {
				PeriodicLineEntity periodicLine = new PeriodicLineEntity();
				
				periodicLine.setLanguageId(inventory.getLanguageId());
				periodicLine.setCompanyCode(inventory.getCompanyCodeId());
				periodicLine.setPlantId(inventory.getPlantId());
				periodicLine.setWarehouseId(inventory.getWarehouseId());
				
				// ITM_CODE
				periodicLine.setItemCode(inventory.getItemCode());
				
				// Pass ITM_CODE in IMBASICDATA table and fetch ITEM_TEXT values
				List<IImbasicData1> imbasicdata1 = imbasicdata1Repository.findByItemCode(inventory.getItemCode());
//				log.info("imbasicdata1 : " + imbasicdata1);
				periodicLine.setItemDesc(imbasicdata1.get(0).getDescription());
				
				// ST_BIN
				periodicLine.setStorageBin(inventory.getStorageBin());
				
				// ST_SEC_ID/ST_SEC
				// Pass the ST_BIN in STORAGEBIN table and fetch ST_SEC_ID/ST_SEC values
				periodicLine.setStorageSectionId(storageBinRepository.findByStorageBin (inventory.getStorageBin())); 
				
				// MFR_PART
				// Pass ITM_CODE in IMBASICDATA table and fetch MFR_PART values
				periodicLine.setManufacturerPartNo(imbasicdata1.get(0).getManufacturePart());
				
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
				IInventory dbInventory = inventoryRepository.findInventoryForPeriodicRun (inventory.getWarehouseId(), 
						inventory.getPackBarcodes(), inventory.getItemCode(), inventory.getStorageBin());
				
				if (dbInventory != null) {
					periodicLine.setInventoryQuantity(inventory.getInventoryQuantity());
					periodicLine.setInventoryUom(inventory.getInventoryUom());
				}
				periodicLineList.add(periodicLine);
			}
			final Page<PeriodicLineEntity> page = new PageImpl<>(periodicLineList, pageable, inventoryList.getTotalElements());
			return page;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
		try {
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
			log.info("newPeriodicHeader.getPeriodicLine() : " + newPeriodicHeader.getPeriodicLine());
			
			/*
			 * Checking whether PeriodicLine has Status_ID 70
			 */
			Set<String> setItemCodes = periodicLineList.stream().map(PeriodicLine::getItemCode).collect(Collectors.toSet());
//			List<String> listItemCodes = periodicLineRepository.findByStatusIdNotIn70(setItemCodes);
//			if (listItemCodes != null && listItemCodes.isEmpty()) {
//				throw new BadRequestException("Selected Items are already in Stock Count process.");
//			}
			
			for (PeriodicLine newPeriodicLine : newPeriodicHeader.getPeriodicLine()) {
//				if (listItemCodes.contains(newPeriodicLine.getItemCode())) {
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
	//				PeriodicLine createdPeriodicLine = periodicLineRepository.save(dbPeriodicLine);
	//				log.info("createdPeriodicLine : " + createdPeriodicLine);
	//				periodicLineList.add(createdPeriodicLine);
					periodicLineList.add(dbPeriodicLine);
//				}
			}

			// Batch Insert
//			List<PeriodicLine> createdPeriodicLine = periodicLineRepository.saveAll(periodicLineList);
			batchInsert(periodicLineList);

//			log.info("createdPeriodicLines : " + createdPeriodicLine.size());
			log.info("createdPeriodicLines : " + periodicLineList.size());

			PeriodicHeaderEntity periodicheaderEntity = new PeriodicHeaderEntity();
			BeanUtils.copyProperties(createdPeriodicHeader, periodicheaderEntity, CommonUtils.getNullPropertyNames(createdPeriodicHeader));
//
//			List<PeriodicLineEntity> listPeriodicLineEntity = new ArrayList<>();
//			for (PeriodicLine periodicLine : periodicLineList) {
//				PeriodicLineEntity perpetualLineEntity = new PeriodicLineEntity();
//				BeanUtils.copyProperties(periodicLine, perpetualLineEntity, CommonUtils.getNullPropertyNames(periodicLine));
//				listPeriodicLineEntity.add(perpetualLineEntity);
//			}
//
//			periodicheaderEntity.setPeriodicLine(listPeriodicLineEntity);
			return periodicheaderEntity;
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
	 * @param loginUserID
	 * @param updatePeriodicHeader
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PeriodicHeader updatePeriodicHeader (String warehouseId, Long cycleCountTypeId, String cycleCountNo, String loginUserID,
			UpdatePeriodicHeader updatePeriodicHeader) throws IllegalAccessException, InvocationTargetException {
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
	 * 
	 * @param warehouseId
	 * @param cycleCountTypeId
	 * @param cycleCountNo
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PeriodicHeader updatePeriodicHeaderFromPeriodicLine (String warehouseId, Long cycleCountTypeId, String cycleCountNo, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		// Update Line Details
//		List<PeriodicLine> lines = periodicLineService.updatePeriodicLineForMobileCount (updatePeriodicHeader.getUpdatePeriodicLine(), loginUserID);
//		log.info("Lines Updated : " + lines);
		
		PeriodicHeader dbPeriodicHeader = getPeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo);
//		BeanUtils.copyProperties(updatePeriodicHeader, dbPeriodicHeader, CommonUtils.getNullPropertyNames(updatePeriodicHeader));
		
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

	public int[][] batchInsert(List<PeriodicLine> periodicLineList) {

		int batchSize=500;

		int[][] updateCounts = jdbcTemplate.batchUpdate(
				"insert into tblperiodicline (LANG_ID, C_ID, PLANT_ID, WH_ID,\n" +
						"CC_NO, ST_BIN, ITM_CODE, PACK_BARCODE, \n" +
						"VAR_ID, VAR_SUB_ID, \n" +
						"STR_NO, STCK_TYP_ID, SP_ST_IND_ID, \n" +
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
						"?,?, \n"+
						"?,?,?, \n"+
						"?,?,?,?, \n"+
						"?,?,?,?, \n"+
						"?,?,?,?, \n"+
						"?,?,?,?, \n"+
						"?,?,?,?, \n"+
						"?,?,?,?, \n"+
						"?,?,?,?, \n"+
						"?,?,?)", periodicLineList, batchSize,
				new ParameterizedPreparedStatementSetter<PeriodicLine>() {

					public void setValues(PreparedStatement ps, PeriodicLine periodicLine) throws SQLException {
						ps.setString(1, periodicLine.getLanguageId());
						ps.setString(2, periodicLine.getCompanyCode());
						ps.setString(3, periodicLine.getPlantId());
						ps.setString(4, periodicLine.getWarehouseId());
						ps.setString(5, periodicLine.getCycleCountNo());
						ps.setString(6, periodicLine.getStorageBin());
						ps.setString(7, periodicLine.getItemCode());
						ps.setString(8, periodicLine.getPackBarcodes());

						if(periodicLine.getVariantCode() != null) {
							ps.setLong(9, periodicLine.getVariantCode());
						} else {
							ps.setLong(9, 0L);
						}

						ps.setString(10, periodicLine.getVariantSubCode());
						ps.setString(11, periodicLine.getBatchSerialNumber());

						if(periodicLine.getStockTypeId() != null) {
							ps.setLong(12, periodicLine.getStockTypeId());
						} else {
							ps.setLong(12, 0L);
						}

						ps.setString(13, periodicLine.getSpecialStockIndicator());

						if(periodicLine.getInventoryQuantity() != null) {
							ps.setDouble(14, periodicLine.getInventoryQuantity());
						} else {
							ps.setDouble(14, 0D);
						}

						ps.setString(15, periodicLine.getInventoryUom());

						if(periodicLine.getCountedQty() != null) {
							ps.setDouble(16, periodicLine.getCountedQty());
						} else {
							ps.setDouble(16, 0D);
						}

						if(periodicLine.getVarianceQty() != null) {
							ps.setDouble(17, periodicLine.getVarianceQty());
						} else {
							ps.setDouble(17, 0D);
						}

						ps.setString(18, periodicLine.getCycleCounterId());
						ps.setString(19, periodicLine.getCycleCounterName());
						if(periodicLine.getStatusId() != null) {
							ps.setLong(20, periodicLine.getStatusId());
						} else {
							ps.setLong(20, 0L);
						}

						ps.setString(21, periodicLine.getCycleCountAction());
						ps.setString(22, periodicLine.getReferenceNo());
						if(periodicLine.getApprovalProcessId() != null) {
							ps.setLong(23, periodicLine.getApprovalProcessId());
						} else {
							ps.setLong(23, 0L);
						}

						ps.setString(24, periodicLine.getApprovalLevel());
						ps.setString(25, periodicLine.getApproverCode());
						ps.setString(26, periodicLine.getApprovalStatus());
						ps.setString(27, periodicLine.getRemarks());
						ps.setString(28, periodicLine.getReferenceField1());
						ps.setString(29, periodicLine.getReferenceField2());
						ps.setString(30, periodicLine.getReferenceField3());
						ps.setString(31, periodicLine.getReferenceField4());
						ps.setString(32, periodicLine.getReferenceField5());
						ps.setString(33, periodicLine.getReferenceField6());
						ps.setString(34, periodicLine.getReferenceField7());
						ps.setString(35, periodicLine.getReferenceField8());
						ps.setString(36, periodicLine.getReferenceField9());
						ps.setString(37, periodicLine.getReferenceField10());

						if(periodicLine.getDeletionIndicator() != null) {
							ps.setLong(38, periodicLine.getDeletionIndicator());
						} else {
							ps.setLong(38,0L);
						}

						ps.setString(39, periodicLine.getCreatedBy());

						if(periodicLine.getCreatedOn() != null) {
							ps.setDate(40,  new java.sql.Date(periodicLine.getCreatedOn().getTime()));
						} else {
							ps.setDate(40, new java.sql.Date(new Date().getTime()));
						}

						ps.setString(41, periodicLine.getConfirmedBy());

						if(periodicLine.getConfirmedOn() != null) {
							ps.setDate(42, new java.sql.Date(periodicLine.getConfirmedOn().getTime()));
						} else {
							ps.setDate(42, new java.sql.Date(new Date().getTime()));
						}

						ps.setString(43, periodicLine.getCountedBy());

						if(periodicLine.getCountedOn() != null) {
							ps.setDate(44, new java.sql.Date(periodicLine.getCountedOn().getTime()));
						} else {
							ps.setDate(44, new java.sql.Date(new Date().getTime()));
						}
					}
				});
		return updateCounts;
	}
}
