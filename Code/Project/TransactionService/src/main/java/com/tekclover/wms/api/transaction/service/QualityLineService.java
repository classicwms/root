package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.UpdateInventory;
import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.UpdateOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.AddQualityLine;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityLine;
import com.tekclover.wms.api.transaction.model.outbound.quality.SearchQualityLine;
import com.tekclover.wms.api.transaction.model.outbound.quality.UpdateQualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.UpdateQualityLine;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.OutboundLineRepository;
import com.tekclover.wms.api.transaction.repository.QualityLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.QualityLineSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QualityLineService extends BaseService {
	
	@Autowired
	private QualityLineRepository qualityLineRepository;
	
	@Autowired
	private OutboundLineRepository outboundLineRepository;
	
	@Autowired
	private QualityHeaderService qualityHeaderService;
	
	@Autowired
	private OutboundHeaderService outboundHeaderService;
	
	@Autowired
	private OutboundLineService outboundLineService;
	
	@Autowired
	private InventoryRepository inventoryRepository;
	
	@Autowired
	private InventoryService inventoryService;
	
	@Autowired
	private MastersService mastersService;
	
	@Autowired
	private AuthTokenService authTokenService;
	
	@Autowired
	private InventoryMovementService inventoryMovementService;
	
	/**
	 * getQualityLines
	 * @return
	 */
	public List<QualityLine> getQualityLines () {
		List<QualityLine> qualityLineList =  qualityLineRepository.findAll();
		qualityLineList = qualityLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return qualityLineList;
	}
	
	/**
	 * getQualityLine
	 * @return
	 */
	public QualityLine getQualityLine (String partnerCode) {
		QualityLine qualityLine = qualityLineRepository.findByPartnerCode(partnerCode).orElse(null);
		if (qualityLine != null && qualityLine.getDeletionIndicator() == 0) {
			return qualityLine;
		} else {
			throw new BadRequestException("The given QualityLine ID : " + partnerCode + " doesn't exist.");
		}
	}
	
	/**
	 * 
	 * // Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @return
	 */
	public QualityLine getQualityLine (String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
			Long lineNumber, String itemCode) {
		QualityLine qualityLine = qualityLineRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
				warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
		if (qualityLine != null) {
			return qualityLine;
		} 
		throw new BadRequestException ("The given QualityLine ID : " + 
					"warehouseId:" + warehouseId +
					",preOutboundNo:" + preOutboundNo +
					",refDocNumber:" + refDocNumber +
					",partnerCode:" + partnerCode +
					",lineNumber:" + lineNumber +
					",itemCode:" + itemCode +
					" doesn't exist.");
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
	public QualityLine getQualityLineForUpdate (String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
			Long lineNumber, String itemCode) {
		QualityLine qualityLine = qualityLineRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
				warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
		if (qualityLine != null) {
			return qualityLine;
		} 
		log.info("The given QualityLine ID : " + 
					"warehouseId:" + warehouseId +
					",preOutboundNo:" + preOutboundNo +
					",refDocNumber:" + refDocNumber +
					",partnerCode:" + partnerCode +
					",lineNumber:" + lineNumber +
					",itemCode:" + itemCode +
					" doesn't exist.");
		return null;
	}
	
	/**
	 * 
	 * @param searchQualityLine
	 * @return
	 * @throws ParseException
	 */
	public List<QualityLine> findQualityLine(SearchQualityLine searchQualityLine) 
			throws ParseException {
		QualityLineSpecification spec = new QualityLineSpecification(searchQualityLine);
		List<QualityLine> results = qualityLineRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createQualityLine
	 * @param newQualityLine
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<QualityLine> createQualityLine (List<AddQualityLine> newQualityLines, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		List<QualityLine> qualityLineList = new ArrayList<>();
		for (AddQualityLine newQualityLine : newQualityLines) {
			QualityLine dbQualityLine = new QualityLine();
			BeanUtils.copyProperties(newQualityLine, dbQualityLine, CommonUtils.getNullPropertyNames(newQualityLine));
			
			// STATUS_ID - HardCoded Value "55"
			dbQualityLine.setStatusId(55L);
			dbQualityLine.setDeletionIndicator(0L);
			dbQualityLine.setQualityCreatedBy(loginUserID);
			dbQualityLine.setQualityUpdatedBy(loginUserID);
			dbQualityLine.setQualityCreatedOn(new Date());
			dbQualityLine.setQualityUpdatedOn(new Date());
			QualityLine createdQualityLine = qualityLineRepository.save(dbQualityLine);
			log.info("createdQualityLine: " + createdQualityLine);
			qualityLineList.add(dbQualityLine);

			/*-----------------STATUS updates in QualityHeader-----------------------*/
			UpdateQualityHeader updateQualityHeader = new UpdateQualityHeader();
			updateQualityHeader.setStatusId(55L);
			QualityHeader qualityHeader = qualityHeaderService.updateQualityHeader(dbQualityLine.getWarehouseId(), 
					dbQualityLine.getPreOutboundNo(), dbQualityLine.getRefDocNumber(), dbQualityLine.getQualityInspectionNo(), 
					dbQualityLine.getActualHeNo(), loginUserID, updateQualityHeader);
			log.info("qualityHeader updated : " + qualityHeader);
			
			/*-------------OTUBOUNDHEADER/OUTBOUNDLINE table updates------------------------------*/
			/*
			 * DLV_ORD_NO
			 * ------------------------------------------------------------------------------------
			 * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE = 12  in NUMBERRANGE table and 
			 * fetch NUM_RAN_CURRENT value of FISCALYEAR=CURRENT YEAR and add +1 and insert
			 */
			Long NUM_RAN_CODE = 12L;
			String DLV_ORD_NO = getNextRangeNumber(NUM_RAN_CODE, dbQualityLine.getWarehouseId());
			
			/*-------------------OUTBOUNDLINE------Update---------------------------*/
			/*
			 * Pass WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE /OB_LINE_NO/_ITM_CODE values in QUALITYILINE table and 
			 * fetch QC_QTY values  and pass the same values in OUTBOUNDLINE table and update DLV_QTY
			 * 
			 * Pass Unique keys in OUTBOUNDLINE table and update STATUS_ID as "57"
			 */
			OutboundLine outboundLine = outboundLineService.getOutboundLine(dbQualityLine.getWarehouseId(), 
					dbQualityLine.getPreOutboundNo(), dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode(),
					dbQualityLine.getLineNumber(), dbQualityLine.getItemCode());
			if (outboundLine != null) {
				outboundLine.setDeliveryOrderNo(DLV_ORD_NO);
				outboundLine.setStatusId(57L);
				outboundLine.setDeliveryQty(dbQualityLine.getQualityQty());
				outboundLine = outboundLineRepository.save(outboundLine);
				log.info("outboundLine updated : " + outboundLine);
			}
			
			boolean isStatus57 = false;
			List<OutboundLine> outboundLines = outboundLineService.getOutboundLine(dbQualityLine.getWarehouseId(), dbQualityLine.getPreOutboundNo(), 
					dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode());
			outboundLines = outboundLines.stream().filter(o -> o.getStatusId() == 57L).collect(Collectors.toList());
			if (outboundLines != null) {
				isStatus57 = true;
			}
			
			/*-------------------OUTBOUNDHEADER------Update---------------------------*/
			UpdateOutboundHeader updateOutboundHeader = new UpdateOutboundHeader();
			updateOutboundHeader.setDeliveryOrderNo(DLV_ORD_NO);
			if (isStatus57) { // If Status if 57 then update OutboundHeader with Status 57.
				updateOutboundHeader.setStatusId(57L);
			}
			
			OutboundHeader outboundHeader = outboundHeaderService.updateOutboundHeader(dbQualityLine.getWarehouseId(), 
					dbQualityLine.getPreOutboundNo(), dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode(), 
					updateOutboundHeader, loginUserID);
			log.info("outboundHeader updated : " + outboundHeader);
			
			/*-----------------Inventory Updates--------------------------------------*/
			// Pass WH_ID/ITM_CODE/ST_BIN/PACK_BARCODE in INVENTORY table 
			AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
			Long BIN_CLASS_ID = 4L;
			StorageBin storageBin = mastersService.getStorageBin(dbQualityLine.getWarehouseId(), BIN_CLASS_ID, authTokenForMastersService.getAccess_token());
			Warehouse warehouse = getWarehouse(dbQualityLine.getWarehouseId());
			
			Inventory inventory = inventoryService.getInventory(dbQualityLine.getWarehouseId(), dbQualityLine.getPickPackBarCode(), 
					dbQualityLine.getItemCode(), storageBin.getStorageBin());
			log.info("inventory---BIN_CLASS_ID-4----> : " + inventory);
			
			if (inventory != null) {
				Double INV_QTY = inventory.getInventoryQuantity() - dbQualityLine.getQualityQty(); // INV_QTY (of Inventory) - QC_QTY
				inventory.setInventoryQuantity(INV_QTY);
				
				// INV_QTY > 0 then, update Inventory Table
				inventory = inventoryRepository.save(inventory);
				log.info("inventory updated : " + inventory);
				
				if (INV_QTY == 0) {
					inventoryRepository.delete(inventory);
					log.info("inventory record is deleted...");
				}
			}
			
			/*-------------------Inserting record in InventoryMovement-------------------------------------*/
			Long subMvtTypeId = 2L;
			String movementDocumentNo = dbQualityLine.getQualityInspectionNo();
			String stBin = storageBin.getStorageBin();
			String movementQtyValue = "N";
			InventoryMovement inventoryMovement = createInventoryMovement(dbQualityLine, subMvtTypeId, movementDocumentNo, stBin, 
					movementQtyValue, loginUserID);
			log.info("InventoryMovement created : " + inventoryMovement);
			
			/*--------------------------------------------------------------------------*/
			// 2.Insert a new record in INVENTORY table as below
			// Fetch from QUALITYLINE table and insert WH_ID/ITM_CODE/ST_BIN= (ST_BIN value of BIN_CLASS_ID=5 
			// from STORAGEBIN table)/PACK_BARCODE/INV_QTY = QC_QTY - INVENTORY UPDATE 2			
			BIN_CLASS_ID = 5L;
			storageBin = mastersService.getStorageBin(dbQualityLine.getWarehouseId(), BIN_CLASS_ID, authTokenForMastersService.getAccess_token());
			warehouse = getWarehouse(dbQualityLine.getWarehouseId());
			
			/*
			 * Checking Inventory table before creating new record inventory
			 */
			// Pass WH_ID/ITM_CODE/ST_BIN = (ST_BIN value of BIN_CLASS_ID=5 /PACK_BARCODE
			Inventory existingInventory = inventoryService.getInventory (dbQualityLine.getWarehouseId(), dbQualityLine.getPickPackBarCode(),
					dbQualityLine.getItemCode(), storageBin.getStorageBin());
			log.info("existingInventory : " + existingInventory);
			if (existingInventory != null) {
				Double INV_QTY = existingInventory.getInventoryQuantity() + dbQualityLine.getQualityQty();
				UpdateInventory updateInventory = new UpdateInventory();
				updateInventory.setInventoryQuantity(INV_QTY);
				Inventory updatedInventory = inventoryService.updateInventory(dbQualityLine.getWarehouseId(), dbQualityLine.getPickPackBarCode(), 
						dbQualityLine.getItemCode(), storageBin.getStorageBin(), 1L, 1L, updateInventory);
				log.info("updatedInventory----------> : " + updatedInventory);
			} else {
				log.info("AddInventory========>");
				AddInventory newInventory = new AddInventory();
				newInventory.setLanguageId(warehouse.getLanguageId());
				newInventory.setCompanyCodeId(warehouse.getCompanyCode());
				newInventory.setPlantId(warehouse.getPlantId());
				newInventory.setStockTypeId(inventory.getStockTypeId());
				newInventory.setBinClassId(BIN_CLASS_ID);
				newInventory.setWarehouseId(dbQualityLine.getWarehouseId());
				newInventory.setPackBarcodes(dbQualityLine.getPickPackBarCode());
				newInventory.setItemCode(dbQualityLine.getItemCode());
				newInventory.setStorageBin(storageBin.getStorageBin());
				newInventory.setInventoryQuantity(dbQualityLine.getQualityQty());
				newInventory.setSpecialStockIndicatorId(1L);
				Inventory createdInventory = inventoryService.createInventory(newInventory, loginUserID);
				log.info("newInventory created : " + createdInventory);
			}
			
			/*-----------------------InventoryMovement----------------------------------*/
			// Inserting record in InventoryMovement
			subMvtTypeId = 2L;
			movementDocumentNo = DLV_ORD_NO;
			stBin = storageBin.getStorageBin();
			movementQtyValue = "P";
			inventoryMovement = createInventoryMovement(dbQualityLine, subMvtTypeId, movementDocumentNo, stBin, 
					movementQtyValue, loginUserID);
			log.info("InventoryMovement created for update2: " + inventoryMovement);
		}
		return qualityLineList;
	}
	
	/**
	 * 
	 * @param dbQualityLine
	 * @param subMvtTypeId
	 * @param movementDocumentNo
	 * @param storageBin
	 * @param movementQtyValue
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private InventoryMovement createInventoryMovement (QualityLine dbQualityLine, Long subMvtTypeId,
			String movementDocumentNo, String storageBin, String movementQtyValue, String loginUserID) 
					throws IllegalAccessException, InvocationTargetException {
		AddInventoryMovement inventoryMovement = new AddInventoryMovement();
		BeanUtils.copyProperties(dbQualityLine, inventoryMovement, CommonUtils.getNullPropertyNames(dbQualityLine));
		
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
		
		// PACK_BAR_CODE
		inventoryMovement.setPackBarcodes(dbQualityLine.getPickPackBarCode());
		
		// MVT_QTY
		inventoryMovement.setMovementQty(dbQualityLine.getPickConfirmQty());
		
		// MVT_UOM
		inventoryMovement.setInventoryUom(dbQualityLine.getQualityConfirmUom());
		
		// IM_CTD_BY
		inventoryMovement.setCreatedBy(dbQualityLine.getQualityConfirmedBy());
		
		// IM_CTD_ON
		inventoryMovement.setCreatedOn(dbQualityLine.getQualityCreatedOn());

		InventoryMovement createdInventoryMovement = inventoryMovementService.createInventoryMovement(inventoryMovement, loginUserID);
		return createdInventoryMovement;
	}
	
	/**
	 * updateQualityLine
	 * @param loginUserId 
	 * @param partnerCode
	 * @param loginUserID2 
	 * @param updateQualityLine
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public QualityLine updateQualityLine (String warehouseId, String preOutboundNo, String refDocNumber, 
			String partnerCode, Long lineNumber, String itemCode, String loginUserID, UpdateQualityLine updateQualityLine) 
			throws IllegalAccessException, InvocationTargetException {
		QualityLine dbQualityLine = getQualityLineForUpdate (warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode);
		if (dbQualityLine != null) {
			BeanUtils.copyProperties(updateQualityLine, dbQualityLine, CommonUtils.getNullPropertyNames(updateQualityLine));
			dbQualityLine.setQualityUpdatedBy(loginUserID);
			dbQualityLine.setQualityUpdatedOn(new Date());
			return qualityLineRepository.save(dbQualityLine);
		}
		return null;
	}
	
	/**
	 * deleteQualityLine
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
	public QualityLine deleteQualityLine (String warehouseId, String preOutboundNo, String refDocNumber, 
			String partnerCode, Long lineNumber, String itemCode, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		QualityLine dbQualityLine = getQualityLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode);
		if ( dbQualityLine != null) {
			dbQualityLine.setDeletionIndicator(1L);
			dbQualityLine.setQualityUpdatedBy(loginUserID);
			dbQualityLine.setQualityUpdatedOn(new Date());
			return qualityLineRepository.save(dbQualityLine);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
		}
	}
}
