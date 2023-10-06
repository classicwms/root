package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.IImbasicData1;
import com.tekclover.wms.api.transaction.model.dto.StatusId;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.UpdateInventory;
import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.UpdateOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.pickup.AddPickupLine;
import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupLine;
import com.tekclover.wms.api.transaction.model.outbound.pickup.SearchPickupLine;
import com.tekclover.wms.api.transaction.model.outbound.pickup.UpdatePickupLine;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.PreOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.AddQualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityHeader;
import com.tekclover.wms.api.transaction.repository.ImBasicData1Repository;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.OutboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PickupHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PickupLineRepository;
import com.tekclover.wms.api.transaction.repository.PreOutboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.specification.PickupLineSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PickupLineService extends BaseService {

	@Autowired
	private PickupLineRepository pickupLineRepository;

	@Autowired
	private PickupHeaderRepository pickupHeaderRepository;

	@Autowired
	private PickupHeaderService pickupHeaderService;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private InventoryMovementService inventoryMovementService;

	@Autowired
	private OutboundLineService outboundLineService;
	
	@Autowired
	private OutboundHeaderService outboundHeaderService;
	
	@Autowired
	private PreOutboundHeaderService preOutboundHeaderService;
	
	@Autowired
	private OutboundHeaderRepository outboundHeaderRepository;
	
	@Autowired
	private PreOutboundHeaderRepository preOutboundHeaderRepository;

	@Autowired
	private QualityHeaderService qualityHeaderService;

	@Autowired
	private MastersService mastersService;

	@Autowired
	private AuthTokenService authTokenService;

	@Autowired
	private ImBasicData1Repository imbasicdata1Repository;

	/**
	 * getPickupLines
	 * 
	 * @return
	 */
	public List<PickupLine> getPickupLines() {
		List<PickupLine> pickupLineList = pickupLineRepository.findAll();
		pickupLineList = pickupLineList.stream().filter(n -> n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return pickupLineList;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @return
	 */
	public PickupLine getPickupLine(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
			Long lineNumber, String itemCode) {
		PickupLine pickupLine = pickupLineRepository
				.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
		if (pickupLine != null) {
			return pickupLine;
		}
		throw new BadRequestException("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId
				+ ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
				+ ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @return
	 */
	public List<PickupLine> getPickupLineForReversal(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode) {
		List<PickupLine> pickupLine = pickupLineRepository
				.findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
		if (pickupLine != null && !pickupLine.isEmpty()) {
			return pickupLine;
		}
		throw new BadRequestException("The given PickupLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:"
				+ preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:"
				+ lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param pickupNumber
	 * @param itemCode
	 * @param pickedStorageBin
	 * @param pickedPackCode
	 * @param actualHeNo
	 * @return
	 */
	private PickupLine getPickupLineForUpdate(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String pickupNumber, String itemCode, String pickedStorageBin,
			String pickedPackCode, String actualHeNo) {
		PickupLine pickupLine = pickupLineRepository
				.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndActualHeNoAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, pickupNumber, itemCode,
						pickedStorageBin, pickedPackCode, actualHeNo, 0L);
		if (pickupLine != null) {
			return pickupLine;
		}
		throw new BadRequestException("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId
				+ ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
				+ ",lineNumber:" + lineNumber + ",pickupNumber:" + pickupNumber + ",itemCode:" + itemCode
				+ ",pickedStorageBin:" + pickedStorageBin + ",pickedPackCode:" + pickedPackCode + ",actualHeNo:"
				+ actualHeNo + " doesn't exist.");
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @return
	 */
	public PickupLine getPickupLineForUpdate(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode) {
		PickupLine pickupLine = pickupLineRepository
				.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
		if (pickupLine != null) {
			return pickupLine;
		}
		log.info("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:"
				+ preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:"
				+ lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
		return null;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @return
	 */
	public List<PickupLine> getPickupLineForUpdateConfirmation(String warehouseId, String preOutboundNo,
			String refDocNumber, String partnerCode, Long lineNumber, String itemCode) {
		List<PickupLine> pickupLine = pickupLineRepository
				.findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
		if (pickupLine != null && !pickupLine.isEmpty()) {
			return pickupLine;
		}
		log.info("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:"
				+ preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:"
				+ lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
		return null;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumbers
	 * @param itemCodes
	 * @return
	 */
	public List<PickupLine> getPickupLine (String warehouseId, String preOutboundNo,
			String refDocNumber, String partnerCode, List<Long> lineNumbers, List<String> itemCodes) {
		List<PickupLine> pickupLine = pickupLineRepository
				.findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberInAndItemCodeInAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumbers, itemCodes, 0L);
		if (pickupLine != null && !pickupLine.isEmpty()) {
			return pickupLine;
		}
		return null;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @return
	 */
	public Double getPickupLineCount (String warehouseId, String preOutboundNo, List<String> refDocNumber) {
		Double pickupLineCount = pickupLineRepository.getCountByWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber);
		if (pickupLineCount != null) {
			return pickupLineCount;
		}
		return 0D;
	}
	
	/**
	 * 
	 * @param languageId
	 * @param companyCode
	 * @param plantId
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @return
	 */
	public double getPickupLineCount (String languageId, String companyCode, String plantId, String warehouseId, 
			List<String> preOutboundNo, List<String> refDocNumber, String partnerCode) {
		List<PickupLine> pickupLineList = pickupLineRepository
				.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoInAndRefDocNumberInAndPartnerCodeAndStatusIdAndDeletionIndicator(
						languageId, companyCode, plantId, warehouseId, preOutboundNo, refDocNumber, partnerCode, 50L, 0L);
		if (pickupLineList != null && !pickupLineList.isEmpty()) {
			return pickupLineList.size();
		}
		return 0;
		
//		throw new BadRequestException("The given PickupLine ID : " + "warehouseId:" + warehouseId
//				+ ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + " doesn't exist.");
	}
	
	/**
	 * 
	 * @param searchPickupLine
	 * @return
	 * @throws ParseException
	 */
	public List<PickupLine> findPickupLine(SearchPickupLine searchPickupLine)
			throws ParseException, java.text.ParseException {

		if (searchPickupLine.getFromPickConfirmedOn() != null && searchPickupLine.getToPickConfirmedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPickupLine.getFromPickConfirmedOn(),
					searchPickupLine.getToPickConfirmedOn());
			searchPickupLine.setFromPickConfirmedOn(dates[0]);
			searchPickupLine.setToPickConfirmedOn(dates[1]);
		}
		PickupLineSpecification spec = new PickupLineSpecification(searchPickupLine);
		List<PickupLine> results = pickupLineRepository.findAll(spec);
		return results;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param itemCode
	 * @param OB_ORD_TYP_ID
	 * @param proposedPackBarCode
	 * @param proposedStorageBin
	 * @return
	 */
	public List<Inventory> getAdditionalBins(String warehouseId, String itemCode, Long OB_ORD_TYP_ID,
			String proposedPackBarCode, String proposedStorageBin) {
		log.info("---OB_ORD_TYP_ID--------> : " + OB_ORD_TYP_ID);

		if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
			List<String> storageSectionIds = Arrays.asList("ZB", "ZC", "ZG", "ZT"); // ZB,ZC,ZG,ZT
			List<Inventory> inventoryAdditionalBins = fetchAdditionalBins(storageSectionIds, warehouseId, itemCode,
					proposedPackBarCode, proposedStorageBin);
			return inventoryAdditionalBins;
		}

		/*
		 * Pass the selected
		 * ST_BIN/WH_ID/ITM_CODE/ALLOC_QTY=0/STCK_TYP_ID=2/SP_ST_IND_ID=2 for
		 * OB_ORD_TYP_ID = 2 and fetch ST_BIN / PACK_BARCODE / INV_QTY values and
		 * display
		 */
		if (OB_ORD_TYP_ID == 2L) {
			List<String> storageSectionIds = Arrays.asList("ZD"); // ZD
			List<Inventory> inventoryAdditionalBins = fetchAdditionalBinsForOB2(storageSectionIds, warehouseId,
					itemCode, proposedPackBarCode, proposedStorageBin);
			return inventoryAdditionalBins;
		}
		return null;
	}

	/**
	 * createPickupLine
	 * 
	 * @param newPickupLine
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<PickupLine> createPickupLine(@Valid List<AddPickupLine> newPickupLines, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
		Long STATUS_ID = 0L;
		String warehouseId = null;
		String preOutboundNo = null;
		String refDocNumber = null;
		String partnerCode = null;
		String pickupNumber = null;
		boolean isQtyAvail = false;
		
		List<AddPickupLine> dupPickupLines = getDuplicates (newPickupLines);
		log.info("-------dupPickupLines--------> " + dupPickupLines);
		if (dupPickupLines != null && !dupPickupLines.isEmpty()) {
			newPickupLines.removeAll(dupPickupLines);
			newPickupLines.add(dupPickupLines.get(0));
			log.info("-------PickupLines---removed-dupPickupLines-----> " + newPickupLines);
		}

		// Create PickUpLine
		List<PickupLine> createdPickupLineList = new ArrayList<>();
		for (AddPickupLine newPickupLine : newPickupLines) {
			PickupLine dbPickupLine = new PickupLine();
			BeanUtils.copyProperties(newPickupLine, dbPickupLine, CommonUtils.getNullPropertyNames(newPickupLine));
			Warehouse warehouse = getWarehouse(newPickupLine.getWarehouseId());
			dbPickupLine.setLanguageId(warehouse.getLanguageId());
			dbPickupLine.setCompanyCodeId(warehouse.getCompanyCode());
			dbPickupLine.setPlantId(warehouse.getPlantId());

			// STATUS_ID
			if (newPickupLine.getPickConfirmQty() > 0) {
				isQtyAvail = true;
			}

			if (isQtyAvail) {
				STATUS_ID = 50L;
			} else {
				STATUS_ID = 51L;
			}

			log.info("newPickupLine STATUS: " + STATUS_ID);

			dbPickupLine.setStatusId(STATUS_ID);
			dbPickupLine.setDeletionIndicator(0L);
			dbPickupLine.setPickupCreatedBy(loginUserID);
			dbPickupLine.setPickupCreatedOn(new Date());
			dbPickupLine.setPickupUpdatedBy(loginUserID);
			dbPickupLine.setPickupUpdatedOn(new Date());
			
			// Checking for Duplicates
			PickupLine existingPickupLine = pickupLineRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndActualHeNoAndPickedStorageBinAndPickedPackCodeAndDeletionIndicator (
					dbPickupLine.getLanguageId(),
					dbPickupLine.getCompanyCodeId(),
					dbPickupLine.getPlantId(),
					dbPickupLine.getWarehouseId(),
					dbPickupLine.getPreOutboundNo(),
					dbPickupLine.getRefDocNumber(),
					dbPickupLine.getPartnerCode(),
					dbPickupLine.getLineNumber(),
					dbPickupLine.getPickupNumber(),
					dbPickupLine.getItemCode(),
					dbPickupLine.getActualHeNo(),
					dbPickupLine.getPickedStorageBin(),
					dbPickupLine.getPickedPackCode(),
					0L);
			log.info("existingPickupLine : " + existingPickupLine);
			if (existingPickupLine == null) {
				PickupLine createdPickupLine = pickupLineRepository.save(dbPickupLine);
				log.info("dbPickupLine created: " + createdPickupLine);
				createdPickupLineList.add(createdPickupLine);
			} else {
				throw new BadRequestException("PickupLine Record is getting duplicated. Given data already exists in the Database. : " + existingPickupLine);
			}
		}

		/*---------------------------------------------Inventory Updates-------------------------------------------*/
		// Updating respective tables
		for (PickupLine dbPickupLine : createdPickupLineList) {
			//------------------------UpdateLock-Applied------------------------------------------------------------
			Inventory inventory = inventoryService.getInventory (dbPickupLine.getWarehouseId(),
					dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(), dbPickupLine.getPickedStorageBin());
			log.info("inventory record queried: " + inventory);
			if (inventory != null) {
				if (dbPickupLine.getAllocatedQty() > 0D) {
					try {
						Double INV_QTY = (inventory.getInventoryQuantity() + dbPickupLine.getAllocatedQty())
								- dbPickupLine.getPickConfirmQty();
						Double ALLOC_QTY = inventory.getAllocatedQuantity() - dbPickupLine.getAllocatedQty();

						/*
						 * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
						 */
						// Start
						if (INV_QTY < 0D) {
							INV_QTY = 0D;
						}

						if (ALLOC_QTY < 0D) {
							ALLOC_QTY = 0D;
						}
						// End

						inventory.setInventoryQuantity(INV_QTY);
						inventory.setAllocatedQuantity(ALLOC_QTY);

						// INV_QTY > 0 then, update Inventory Table
						inventory = inventoryRepository.save(inventory);
						log.info("inventory updated : " + inventory);

						if (INV_QTY == 0) {
							// Setting up statusId = 0
							try {
								// Check whether Inventory has record or not
								Inventory inventoryByStBin = inventoryService.getInventoryByStorageBin(warehouseId, inventory.getStorageBin());
								if (inventoryByStBin == null) {
									StorageBin dbStorageBin = mastersService.getStorageBin(inventory.getStorageBin(),
											dbPickupLine.getWarehouseId(), authTokenForMastersService.getAccess_token());
									dbStorageBin.setStatusId(0L);
									dbStorageBin.setWarehouseId(dbPickupLine.getWarehouseId());
									mastersService.updateStorageBin(inventory.getStorageBin(), dbStorageBin, loginUserID,
											authTokenForMastersService.getAccess_token());
								}
							} catch (Exception e) {
								log.error("updateStorageBin Error :" + e.toString());
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						log.error("Inventory Update :" + e.toString());
						e.printStackTrace();
					}
				}

				if (dbPickupLine.getAllocatedQty() == null || dbPickupLine.getAllocatedQty() == 0D) {
					Double INV_QTY;
					try {
						INV_QTY = inventory.getInventoryQuantity() - dbPickupLine.getPickConfirmQty();
						/*
						 * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
						 */
						// Start
						if (INV_QTY < 0D) {
							INV_QTY = 0D;
						}
						// End
						inventory.setInventoryQuantity(INV_QTY);
						inventory = inventoryRepository.save(inventory);
						log.info("inventory updated : " + inventory);
						
						//-------------------------------------------------------------------
						// PASS PickedConfirmedStBin, WH_ID to inventory
						// 	If inv_qty && alloc_qty is zero or null then do the below logic.
						//-------------------------------------------------------------------
						Inventory inventoryBySTBIN = inventoryService.getInventoryByStorageBin(warehouseId, dbPickupLine.getPickedStorageBin());
						//if (INV_QTY == 0) {
						if (inventoryBySTBIN != null && (inventoryBySTBIN.getAllocatedQuantity() == null || inventoryBySTBIN.getAllocatedQuantity() == 0D) 
								&& (inventoryBySTBIN.getInventoryQuantity() == null || inventoryBySTBIN.getInventoryQuantity() == 0D)) {
							try {
								
								// Setting up statusId = 0
								StorageBin dbStorageBin = mastersService.getStorageBin(inventory.getStorageBin(),
										dbPickupLine.getWarehouseId(), authTokenForMastersService.getAccess_token());
								dbStorageBin.setStatusId(0L);
								mastersService.updateStorageBin(inventory.getStorageBin(), dbStorageBin, loginUserID,
										authTokenForMastersService.getAccess_token());
							} catch (Exception e) {
								log.error("updateStorageBin Error :" + e.toString());
								e.printStackTrace();
							}
						}
					} catch (Exception e1) {
						log.error("Inventory cum StorageBin update: Error :" + e1.toString());
						e1.printStackTrace();
					}
				}
			}

			// Inserting record in InventoryMovement
			Long subMvtTypeId;
			String movementDocumentNo;
			String stBin;
			String movementQtyValue;
			InventoryMovement inventoryMovement;
			try {
				subMvtTypeId = 1L;
				movementDocumentNo = dbPickupLine.getPickupNumber();
				stBin = dbPickupLine.getPickedStorageBin();
				movementQtyValue = "N";
				inventoryMovement = createInventoryMovement(dbPickupLine, subMvtTypeId, movementDocumentNo, stBin,
						movementQtyValue, loginUserID);
				log.info("InventoryMovement created : " + inventoryMovement);
			} catch (Exception e) {
				log.error("InventoryMovement create Error :" + e.toString());
				e.printStackTrace();
			}

			/*--------------------------------------------------------------------------*/
			// 3. Insert a new record in INVENTORY table as below
			// Fetch from PICKUPLINE table and insert WH_ID/ITM_CODE/ST_BIN = (ST_BIN value
			// of BIN_CLASS_ID=4
			// from STORAGEBIN table)/PACK_BARCODE/INV_QTY = PICK_CNF_QTY.
			// Checking Inventory table before creating new record inventory
			// Pass WH_ID/ITM_CODE/ST_BIN = (ST_BIN value of BIN_CLASS_ID=4 /PACK_BARCODE
			Long BIN_CLASS_ID = 4L;
			StorageBin storageBin = mastersService.getStorageBin(dbPickupLine.getWarehouseId(), BIN_CLASS_ID,
					authTokenForMastersService.getAccess_token());
			Inventory existingInventory = inventoryService.getInventory(dbPickupLine.getWarehouseId(),
					dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(), storageBin.getStorageBin());
			if (existingInventory != null) {
				try {
					Double INV_QTY = existingInventory.getInventoryQuantity() + dbPickupLine.getPickConfirmQty();
					UpdateInventory updateInventory = new UpdateInventory();
					updateInventory.setInventoryQuantity(INV_QTY);
					Inventory updatedInventory = inventoryService.updateInventory(dbPickupLine.getWarehouseId(),
							dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(), storageBin.getStorageBin(),
							dbPickupLine.getStockTypeId(), dbPickupLine.getSpecialStockIndicatorId(), updateInventory, loginUserID);
					log.info("Inventory is Updated : " + updatedInventory);
				} catch (Exception e) {
					log.error("Inventory update Error :" + e.toString());
					e.printStackTrace();
				}
			} else {
				if (dbPickupLine.getStatusId() == 50L) {
					try {
						AddInventory newInventory = new AddInventory();
						newInventory.setLanguageId(getLanguageId());
						newInventory.setCompanyCodeId(getCompanyCode());
						newInventory.setPlantId(getPlantId());
						newInventory.setBinClassId(BIN_CLASS_ID);
						newInventory.setStockTypeId(inventory.getStockTypeId());
						newInventory.setWarehouseId(dbPickupLine.getWarehouseId());
						newInventory.setPackBarcodes(dbPickupLine.getPickedPackCode());
						newInventory.setItemCode(dbPickupLine.getItemCode());
						newInventory.setStorageBin(storageBin.getStorageBin());
						newInventory.setInventoryQuantity(dbPickupLine.getPickConfirmQty());
						newInventory.setSpecialStockIndicatorId(dbPickupLine.getSpecialStockIndicatorId());

						List<IImbasicData1> imbasicdata1 = imbasicdata1Repository
								.findByItemCode(newInventory.getItemCode());
						if (imbasicdata1 != null && !imbasicdata1.isEmpty()) {
							newInventory.setReferenceField8(imbasicdata1.get(0).getDescription());
							newInventory.setReferenceField9(imbasicdata1.get(0).getManufacturePart());
						}
						if (storageBin != null) {
							newInventory.setReferenceField10(storageBin.getStorageSectionId());
							newInventory.setReferenceField5(storageBin.getAisleNumber());
							newInventory.setReferenceField6(storageBin.getShelfId());
							newInventory.setReferenceField7(storageBin.getRowId());
						}

						Inventory createdInventory = inventoryService.createInventory(newInventory, loginUserID);
						log.info("newInventory created : " + createdInventory);
					} catch (Exception e) {
						log.error("newInventory create Error :" + e.toString());
						e.printStackTrace();
					}
				}
			}

			/*
			 * ---------------------Update-OUTBOUNDLINE----------------------------------------------------
			 */
			try {
				UpdateOutboundLine updateOutboundLine = new UpdateOutboundLine();
				updateOutboundLine.setStatusId(STATUS_ID);
				OutboundLine outboundLine = outboundLineService.updateOutboundLine(dbPickupLine.getWarehouseId(),
						dbPickupLine.getPreOutboundNo(), dbPickupLine.getRefDocNumber(), dbPickupLine.getPartnerCode(),
						dbPickupLine.getLineNumber(), dbPickupLine.getItemCode(), loginUserID, updateOutboundLine);
				log.info("outboundLine updated : " + outboundLine);
			} catch (Exception e) {
				log.error("outboundLine update Error :" + e.toString());
				e.printStackTrace();
			}

			/*
			 * ------------------Record insertion in QUALITYHEADER table----------------------------------- 
			 * Allow to create QualityHeader only
			 * for STATUS_ID = 50
			 */
			if (dbPickupLine.getStatusId() == 50L) {
				String QC_NO = null;
				try {
					AddQualityHeader newQualityHeader = new AddQualityHeader();
					BeanUtils.copyProperties(dbPickupLine, newQualityHeader,
							CommonUtils.getNullPropertyNames(dbPickupLine));

					// QC_NO
					/*
					 * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE =11 in NUMBERRANGE table
					 * and fetch NUM_RAN_CURRENT value of FISCALYEAR=CURRENT YEAR and add +1 and
					 * insert
					 */
					Long NUM_RAN_CODE = 11L;
					QC_NO = getNextRangeNumber(NUM_RAN_CODE, dbPickupLine.getWarehouseId());
					newQualityHeader.setQualityInspectionNo(QC_NO);

					// ------ PROD FIX : 29/09/2022:HAREESH -------(CWMS/IW/2022/018)
					if (dbPickupLine.getPickConfirmQty() != null) {
						newQualityHeader.setQcToQty(String.valueOf(dbPickupLine.getPickConfirmQty()));
					}

					newQualityHeader.setReferenceField1(dbPickupLine.getPickedStorageBin());
					newQualityHeader.setReferenceField2(dbPickupLine.getPickedPackCode());
					newQualityHeader.setReferenceField3(dbPickupLine.getDescription());
					newQualityHeader.setReferenceField4(dbPickupLine.getItemCode());
					newQualityHeader.setReferenceField5(String.valueOf(dbPickupLine.getLineNumber()));

					// STATUS_ID - Hard Coded Value "54"
					newQualityHeader.setStatusId(54L);
					StatusId idStatus = idmasterService.getStatus(54L, dbPickupLine.getWarehouseId(), authTokenForIDService.getAccess_token());
					newQualityHeader.setReferenceField10(idStatus.getStatus());	
					
					QualityHeader createdQualityHeader = qualityHeaderService.createQualityHeader(newQualityHeader,
							loginUserID);
					log.info("createdQualityHeader : " + createdQualityHeader);
				} catch (Exception e) {
					log.error("createdQualityHeader Error :" + e.toString());
					e.printStackTrace();
				}

				/*-----------------------InventoryMovement----------------------------------*/
				// Inserting record in InventoryMovement
				try {
					subMvtTypeId = 2L;
					movementDocumentNo = QC_NO;
					stBin = storageBin.getStorageBin();
					movementQtyValue = "P";
					inventoryMovement = createInventoryMovement(dbPickupLine, subMvtTypeId, movementDocumentNo, stBin,
							movementQtyValue, loginUserID);
					log.info("InventoryMovement created for update2: " + inventoryMovement);
				} catch (Exception e) {
					log.error("InventoryMovement create Error for update2 :" + e.toString());
					e.printStackTrace();
				}
			}

			// Properties needed for updating PickupHeader
			warehouseId = dbPickupLine.getWarehouseId();
			preOutboundNo = dbPickupLine.getPreOutboundNo();
			refDocNumber = dbPickupLine.getRefDocNumber();
			partnerCode = dbPickupLine.getPartnerCode();
			pickupNumber = dbPickupLine.getPickupNumber();
		}
		
		/*
		 * Update OutboundHeader & Preoutbound Header STATUS_ID as 47 only if all OutboundLines are STATUS_ID is 51
		 */
		List<OutboundLine> outboundLineList = outboundLineService.getOutboundLine(warehouseId, preOutboundNo, refDocNumber);
		boolean hasStatus51 = false;
		List<Long> status51List = outboundLineList.stream().map(OutboundLine::getStatusId).collect(Collectors.toList());
		long status51IdCount = status51List.stream().filter(a -> a == 51L || a == 47L ).count();
		log.info("status count : " + (status51IdCount == status51List.size()));
		hasStatus51 = (status51IdCount == status51List.size());
		if (!status51List.isEmpty() && hasStatus51) {
			//------------------------UpdateLock-Applied------------------------------------------------------------
			OutboundHeader outboundHeader = outboundHeaderService.getOutboundHeader(refDocNumber, warehouseId);
			outboundHeader.setStatusId(51L);
			outboundHeader.setUpdatedBy(loginUserID);
			outboundHeader.setUpdatedOn(new Date());			
			outboundHeaderRepository.save(outboundHeader);
			log.info("outboundHeader updated as 51.");
			
			//------------------------UpdateLock-Applied------------------------------------------------------------
			PreOutboundHeader preOutboundHeader = preOutboundHeaderService.getPreOutboundHeader(warehouseId, refDocNumber);
			preOutboundHeader.setStatusId(51L);
			preOutboundHeader.setUpdatedBy(loginUserID);
			preOutboundHeader.setUpdatedOn(new Date());			
			preOutboundHeaderRepository.save(preOutboundHeader);
			log.info("PreOutboundHeader updated as 51.");
		}

		/*---------------------------------------------PickupHeader Updates---------------------------------------*/
		// -----------------logic for checking all records as 51 then only it should go o update header-----------*/
		try {
			boolean isStatus51 = false;
			List<Long> statusList = createdPickupLineList.stream().map(PickupLine::getStatusId)
					.collect(Collectors.toList());
			long statusIdCount = statusList.stream().filter(a -> a == 51L).count();
			log.info("status count : " + (statusIdCount == statusList.size()));
			isStatus51 = (statusIdCount == statusList.size());
			if (!statusList.isEmpty() && isStatus51) {
				STATUS_ID = 51L;
			} else {
				STATUS_ID = 50L;
			}

			//------------------------UpdateLock-Applied------------------------------------------------------------
			PickupHeader pickupHeader = pickupHeaderService.getPickupHeader(warehouseId, preOutboundNo, refDocNumber,
					partnerCode, pickupNumber);
			pickupHeader.setStatusId(STATUS_ID);
			
			StatusId idStatus = idmasterService.getStatus(STATUS_ID, warehouseId, authTokenForIDService.getAccess_token());
			pickupHeader.setReferenceField7(idStatus.getStatus());		// tblpickupheader REF_FIELD_7
			
			pickupHeader.setPickUpdatedBy(loginUserID);
			pickupHeader.setPickUpdatedOn(new Date());
			pickupHeader = pickupHeaderRepository.save(pickupHeader);
			log.info("PickupHeader updated: " + pickupHeader);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("PickupHeader update error: " + e.toString());
		}
		return createdPickupLineList;
	}
	
	/**
	 * 
	 * @param personList
	 * @return
	 */
	public static List<AddPickupLine> getDuplicates(@Valid List<AddPickupLine> newPickupLines) {
		return getDuplicatesMap(newPickupLines).values().stream()
	      .filter(duplicates -> duplicates.size() > 1)
	      .flatMap(Collection::stream)
	      .collect(Collectors.toList());
	}
	
	/**
	 * 
	 * @param personList
	 * @return
	 */
	private static Map<String, List<AddPickupLine>> getDuplicatesMap(@Valid List<AddPickupLine> newPickupLines) {
	  return newPickupLines.stream().collect(Collectors.groupingBy(AddPickupLine::uniqueAttributes));
	}

	/**
	 * 
	 * @param dbPickupLine
	 * @param subMvtTypeId
	 * @param movementDocumentNo
	 * @param storageBin
	 * @param movementQtyValue
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private InventoryMovement createInventoryMovement(PickupLine dbPickupLine, Long subMvtTypeId,
			String movementDocumentNo, String storageBin, String movementQtyValue, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		AddInventoryMovement inventoryMovement = new AddInventoryMovement();
		BeanUtils.copyProperties(dbPickupLine, inventoryMovement, CommonUtils.getNullPropertyNames(dbPickupLine));

		inventoryMovement.setCompanyCodeId(dbPickupLine.getCompanyCodeId());

		// MVT_TYP_ID
		inventoryMovement.setMovementType(3L);

		// SUB_MVT_TYP_ID
		inventoryMovement.setSubmovementType(subMvtTypeId);

		// VAR_ID
		inventoryMovement.setVariantCode(1L);

		// VAR_SUB_ID
		inventoryMovement.setVariantSubCode("1");

		// STR_MTD
		inventoryMovement.setStorageMethod("1");

		// STR_NO
		inventoryMovement.setBatchSerialNumber("1");

		// MVT_DOC_NO
		inventoryMovement.setMovementDocumentNo(movementDocumentNo);

		// ST_BIN
		inventoryMovement.setStorageBin(storageBin);

		// MVT_QTY_VAL
		inventoryMovement.setMovementQtyValue(movementQtyValue);

		// BAR_CODE
		inventoryMovement.setPackBarcodes(dbPickupLine.getPickedPackCode());

		// MVT_QTY
		inventoryMovement.setMovementQty(dbPickupLine.getPickConfirmQty());

		// MVT_UOM
		inventoryMovement.setInventoryUom(dbPickupLine.getPickUom());

		// IM_CTD_BY
		inventoryMovement.setCreatedBy(dbPickupLine.getPickupCreatedBy());

		// IM_CTD_ON
		inventoryMovement.setCreatedOn(dbPickupLine.getPickupCreatedOn());

		InventoryMovement createdInventoryMovement = inventoryMovementService.createInventoryMovement(inventoryMovement,
				loginUserID);
		return createdInventoryMovement;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @param loginUserID
	 * @param updatePickupLine
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PickupLine updatePickupLine(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode, String loginUserID, UpdatePickupLine updatePickupLine)
			throws IllegalAccessException, InvocationTargetException {
		PickupLine dbPickupLine = getPickupLineForUpdate(warehouseId, preOutboundNo, refDocNumber, partnerCode,
				lineNumber, itemCode);
		if (dbPickupLine != null) {
			BeanUtils.copyProperties(updatePickupLine, dbPickupLine,
					CommonUtils.getNullPropertyNames(updatePickupLine));
			dbPickupLine.setPickupUpdatedBy(loginUserID);
			dbPickupLine.setPickupUpdatedOn(new Date());
			return pickupLineRepository.save(dbPickupLine);
		}
		return null;
	}

	public List<PickupLine> updatePickupLineForConfirmation(String warehouseId, String preOutboundNo,
			String refDocNumber, String partnerCode, Long lineNumber, String itemCode, String loginUserID,
			UpdatePickupLine updatePickupLine) throws IllegalAccessException, InvocationTargetException {
		List<PickupLine> dbPickupLine = getPickupLineForUpdateConfirmation(warehouseId, preOutboundNo, refDocNumber,
				partnerCode, lineNumber, itemCode);
		if (dbPickupLine != null && !dbPickupLine.isEmpty()) {
			List<PickupLine> toSave = new ArrayList<>();
			for (PickupLine data : dbPickupLine) {
				BeanUtils.copyProperties(updatePickupLine, data, CommonUtils.getNullPropertyNames(updatePickupLine));
				data.setPickupUpdatedBy(loginUserID);
				data.setPickupUpdatedOn(new Date());
				toSave.add(data);
			}
			return pickupLineRepository.saveAll(toSave);
		}
		return null;
	}

	/**
	 * 
	 * @param actualHeNo
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param pickupNumber
	 * @param itemCode
	 * @param pickedStorageBin
	 * @param pickedPackCode
	 * @param loginUserID
	 * @param updatePickupLine
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PickupLine updatePickupLine(String actualHeNo, String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String pickupNumber, String itemCode, String pickedStorageBin,
			String pickedPackCode, String loginUserID, UpdatePickupLine updatePickupLine)
			throws IllegalAccessException, InvocationTargetException {
		PickupLine dbPickupLine = getPickupLineForUpdate(warehouseId, preOutboundNo, refDocNumber, partnerCode,
				lineNumber, pickupNumber, itemCode, pickedStorageBin, pickedPackCode, actualHeNo);
		if (dbPickupLine != null) {
			BeanUtils.copyProperties(updatePickupLine, dbPickupLine,
					CommonUtils.getNullPropertyNames(updatePickupLine));
			dbPickupLine.setPickupUpdatedBy(loginUserID);
			dbPickupLine.setPickupUpdatedOn(new Date());
			return pickupLineRepository.save(dbPickupLine);
		}
		return null;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param pickupNumber
	 * @param itemCode
	 * @param actualHeNo
	 * @param pickedStorageBin
	 * @param pickedPackCode
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PickupLine deletePickupLine(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String pickupNumber, String itemCode, String actualHeNo,
			String pickedStorageBin, String pickedPackCode, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		PickupLine dbPickupLine = getPickupLineForUpdate(warehouseId, preOutboundNo, refDocNumber, partnerCode,
				lineNumber, pickupNumber, itemCode, pickedStorageBin, pickedPackCode, actualHeNo);
		if (dbPickupLine != null) {
			dbPickupLine.setDeletionIndicator(1L);
			dbPickupLine.setPickupUpdatedBy(loginUserID);
			dbPickupLine.setPickupUpdatedOn(new Date());
			return pickupLineRepository.save(dbPickupLine);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
		}
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PickupLine deletePickupLine(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		PickupLine dbPickupLine = getPickupLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
				itemCode);
		if (dbPickupLine != null) {
			dbPickupLine.setDeletionIndicator(1L);
			dbPickupLine.setPickupUpdatedBy(loginUserID);
			dbPickupLine.setPickupUpdatedOn(new Date());
			return pickupLineRepository.save(dbPickupLine);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
		}
	}

	public List<PickupLine> deletePickupLineForReversal(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		List<PickupLine> dbPickupLine = getPickupLineForReversal(warehouseId, preOutboundNo, refDocNumber, partnerCode,
				lineNumber, itemCode);
		if (dbPickupLine != null && !dbPickupLine.isEmpty()) {
			List<PickupLine> toSavePickupLineList = new ArrayList<>();
			dbPickupLine.forEach(data -> {
				data.setDeletionIndicator(1L);
				data.setPickupUpdatedBy(loginUserID);
				data.setPickupUpdatedOn(new Date());
				toSavePickupLineList.add(data);
			});
			return pickupLineRepository.saveAll(toSavePickupLineList);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
		}
	}

	/**
	 * 
	 * @param storageSectionIds
	 * @param storageSectionIds2
	 * @param warehouseId
	 * @param itemCode
	 * @param proposedStorageBin
	 * @param proposedPackBarCode
	 * @return
	 */
	private List<Inventory> fetchAdditionalBins(List<String> storageSectionIds, String warehouseId, String itemCode,
			String proposedPackBarCode, String proposedStorageBin) {
		List<Inventory> finalizedInventoryList = new ArrayList<>();
		List<Inventory> listInventory = inventoryService.getInventoryForAdditionalBins(warehouseId, itemCode,
				storageSectionIds);
		log.info("selected listInventory--------: " + listInventory);
		boolean toBeIncluded = false;
		for (Inventory inventory : listInventory) {
			if (inventory.getPackBarcodes().equalsIgnoreCase(proposedPackBarCode)) {
				toBeIncluded = false;
				log.info("toBeIncluded----Pack----: " + toBeIncluded);
				if (inventory.getStorageBin().equalsIgnoreCase(proposedStorageBin)) {
					toBeIncluded = false;
				} else {
					toBeIncluded = true;
				}
			} else {
				toBeIncluded = true;
			}

			log.info("toBeIncluded--------: " + toBeIncluded);
			if (toBeIncluded) {
				finalizedInventoryList.add(inventory);
			}
		}
		return finalizedInventoryList;
	}

	/**
	 * 
	 * @param storageSectionIds
	 * @param warehouseId
	 * @param itemCode
	 * @return
	 */
	private List<Inventory> fetchAdditionalBinsForOB2(List<String> storageSectionIds, String warehouseId,
			String itemCode, String proposedPackBarCode, String proposedStorageBin) {
		List<Inventory> listInventory = inventoryService.getInventoryForAdditionalBinsForOB2(warehouseId, itemCode,
				storageSectionIds, 1L /* STCK_TYP_ID */);
		listInventory = listInventory.stream().filter(i -> !i.getPackBarcodes().equalsIgnoreCase(proposedPackBarCode))
				.collect(Collectors.toList());
		listInventory = listInventory.stream().filter(i -> !i.getStorageBin().equalsIgnoreCase(proposedStorageBin))
				.collect(Collectors.toList());
		return listInventory;
	}

//	private List<Inventory> fetchAdditionalBins (List<String> stBins, List<String> storageSectionIds, 
//			String warehouseId, String itemCode, String proposedPackBarCode, String proposedStorageBin) {
//		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//		List<Inventory> responseInventoryList = new ArrayList<>();
//		
//		StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
//		storageBinPutAway.setStorageBin(stBins);
//		storageBinPutAway.setStorageSectionIds(storageSectionIds);
//		storageBinPutAway.setWarehouseId(warehouseId);
//		StorageBin[] storageBin = mastersService.getStorageBin(storageBinPutAway, authTokenForMastersService.getAccess_token());
//		log.info("storageBin : " + Arrays.asList(storageBin));
//		
//		if (storageBin != null && storageBin.length > 0) {
//			// Pass the filtered ST_BIN/WH_ID/ITM_CODE/BIN_CL_ID=01/STCK_TYP_ID=1 in Inventory table and 
//			
//			List<Inventory> finalizedInventoryList = new ArrayList<>();
//			for (StorageBin dbStorageBin : storageBin) {
//				List<Inventory> listInventory = 
//						inventoryService.getInventoryForAdditionalBins (warehouseId, itemCode, dbStorageBin.getStorageBin());
//				log.info("selected listInventory--------: " + listInventory);
//				boolean toBeIncluded = false;
//				for (Inventory inventory : listInventory) {
//					if (inventory.getPackBarcodes().equalsIgnoreCase(proposedPackBarCode)) {
//						toBeIncluded = false;
//						log.info("toBeIncluded----Pack----: " + toBeIncluded);
//						if (inventory.getStorageBin().equalsIgnoreCase(proposedStorageBin)) {
//							toBeIncluded = false;
//						} else {
//							toBeIncluded = true;
//						}
//					} else {
//						toBeIncluded = true;
//					}
//					
//					log.info("toBeIncluded--------: " + toBeIncluded);
//					if (toBeIncluded) {
//						finalizedInventoryList.add(inventory);
//					}
//				}
//			}
//			return finalizedInventoryList;
//		}
//		return responseInventoryList;
//	}
//	
//	/**
//	 * 
//	 * @param storageSectionIds
//	 * @param warehouseId
//	 * @param itemCode
//	 * @return
//	 */
//	private List<Inventory> fetchAdditionalBinsForOB2 (List<String> stBins, List<String> storageSectionIds, 
//			String warehouseId, String itemCode, String proposedPackBarCode, String proposedStorageBin) {
//		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//		List<Inventory> responseInventoryList = new ArrayList<>();
//		StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
//		storageBinPutAway.setStorageBin(stBins);
//		storageBinPutAway.setStorageSectionIds(storageSectionIds);
//		storageBinPutAway.setWarehouseId(warehouseId);
//		StorageBin[] storageBin = mastersService.getStorageBin(storageBinPutAway, authTokenForMastersService.getAccess_token());
//		if (storageBin != null && storageBin.length > 0) {
//			/* Discussed to remove SP_INND_ID parameter from get */
//			// Pass the selected ST_BIN/WH_ID/ITM_CODE/ALLOC_QTY=0/STCK_TYP_ID=2 for OB_ORD_TYP_ID = 2
//			for (StorageBin dbStorageBin : storageBin) {
//				List<Inventory> listInventory = 
//						inventoryService.getInventoryForAdditionalBinsForOB2(warehouseId, itemCode, 
//									dbStorageBin.getStorageBin(), 1L /*STCK_TYP_ID*/);
//				listInventory = listInventory.stream().filter(i -> !i.getPackBarcodes().equalsIgnoreCase(proposedPackBarCode)).collect(Collectors.toList());
//				listInventory = listInventory.stream().filter(i -> !i.getStorageBin().equalsIgnoreCase(proposedStorageBin)).collect(Collectors.toList());
//				responseInventoryList.addAll(listInventory);
//			}
//		}
//		return responseInventoryList;
//	}
}
