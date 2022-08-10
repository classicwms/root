package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.putaway.AddPutAwayLine;
import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayHeader;
import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayLine;
import com.tekclover.wms.api.transaction.model.inbound.putaway.SearchPutAwayLine;
import com.tekclover.wms.api.transaction.model.inbound.putaway.UpdatePutAwayLine;
import com.tekclover.wms.api.transaction.repository.InboundLineRepository;
import com.tekclover.wms.api.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.PutAwayHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PutAwayLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.PutAwayLineSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PutAwayLineService extends BaseService {
	
	@Autowired
	private PutAwayHeaderRepository putAwayHeaderRepository;
	
	@Autowired
	private PutAwayLineRepository putAwayLineRepository;
	
	@Autowired
	private PutAwayHeaderService putAwayHeaderService;
	
	@Autowired
	private InventoryRepository inventoryRepository;
	
	@Autowired
	private InboundLineRepository inboundLineRepository;
	
	@Autowired
	private InventoryMovementRepository inventoryMovementRepository;
	
	@Autowired
	private InventoryService inventoryService;
	
	@Autowired
	private MastersService mastersService;
	
	@Autowired
	private AuthTokenService authTokenService;
	
	@Autowired
	private InboundLineService inboundLineService;
	
	/**
	 * getPutAwayLines
	 * @return
	 */
	public List<PutAwayLine> getPutAwayLines () {
		List<PutAwayLine> putAwayLineList =  putAwayLineRepository.findAll();
		putAwayLineList = putAwayLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return putAwayLineList;
	}
	
	/**
	 * WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param lineNo
	 * @param itemCode
	 * @return
	 */
	public List<PutAwayLine> getPutAwayLine (String warehouseId, String preInboundNo, String refDocNumber, Long lineNo, String itemCode) {
		List<PutAwayLine> putAwayLine = 
				putAwayLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndDeletionIndicator(
						getLanguageId(),
						getCompanyCode(),
						getPlantId(),
						warehouseId, 
						preInboundNo, 
						refDocNumber, 
						lineNo, 
						itemCode, 
						0L);
		if (putAwayLine.isEmpty()) {
			throw new BadRequestException("The given values in PutAwayLine: warehouseId:" + warehouseId + 
					",preInboundNo: " + preInboundNo + 
					",lineNo: " + lineNo + 
					",itemCode: " + itemCode + 
					",lineNo: " + lineNo + 
					" doesn't exist.");
		} 
		
		return putAwayLine;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param refDocNumber
	 * @param putAwayNumber
	 * @return
	 */
	public List<PutAwayLine> getPutAwayLine (String warehouseId, String refDocNumber, String putAwayNumber) {
		List<PutAwayLine> putAwayLine = 
				putAwayLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
						getLanguageId(),
						getCompanyCode(),
						getPlantId(),
						warehouseId, 
						refDocNumber, 
						putAwayNumber, 
						0L);
		if (putAwayLine.isEmpty()) {
			throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
					",refDocNumber: " + refDocNumber + 
					",putAwayNumber: " + putAwayNumber + 
					" doesn't exist.");
		} 
		
		return putAwayLine;
	}
	
	/**
	 * getPutAwayLine
	 * @param confirmedStorageBin
	 * @return
	 */
	public PutAwayLine getPutAwayLine (String warehouseId, String goodsReceiptNo, String preInboundNo, String refDocNumber, 
			String putAwayNumber, Long lineNo, String itemCode, String proposedStorageBin, List<String> confirmedStorageBin) {
		Optional<PutAwayLine> putAwayLine = 
				putAwayLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndLineNoAndItemCodeAndProposedStorageBinAndConfirmedStorageBinInAndDeletionIndicator(
						getLanguageId(),
						getCompanyCode(),
						getPlantId(),
						warehouseId, 
						goodsReceiptNo,
						preInboundNo, 
						refDocNumber, 
						putAwayNumber, 
						lineNo, 
						itemCode, 
						proposedStorageBin, 
						confirmedStorageBin,
						0L);
		if (putAwayLine.isEmpty()) {
			throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
					",goodsReceiptNo: " + goodsReceiptNo + "," +
					",preInboundNo: " + preInboundNo + "," +
					",refDocNumber: " + refDocNumber + "," +
					",putAwayNumber: " + putAwayNumber + "," +
					",putAwayNumber: " + putAwayNumber + "," +
					",lineNo: " + lineNo + "," +
					",itemCode: " + itemCode + "," +
					",lineNo: " + lineNo + "," +
					",proposedStorageBin: " + proposedStorageBin + 
					",confirmedStorageBin: " + confirmedStorageBin + 
					" doesn't exist.");
		} 
		
		return putAwayLine.get();
	}
	
	/**
	 * 
	 * @param refDocNumber
	 * @param packBarcodes
	 * @return
	 */
	public List<PutAwayLine> getPutAwayLine (String refDocNumber, String packBarcodes) {
		List<PutAwayLine> putAwayLine = 
				putAwayLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator (
					getLanguageId(),
					getCompanyCode(),
					getPlantId(),
					refDocNumber, 
					packBarcodes,
					0L
					);
			if (putAwayLine.isEmpty()) {
				throw new BadRequestException("The given values: " + 
						",refDocNumber: " + refDocNumber + "," +
						",packBarcodes: " + packBarcodes + "," +
						" doesn't exist.");
			} 
		return putAwayLine;
	}
	
	/**
	 * 
	 * @param refDocNumber
	 * @return
	 */
	public List<PutAwayLine> getPutAwayLine(String refDocNumber) {
		List<PutAwayLine> putAwayLine = 
				putAwayLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndDeletionIndicator(
						getLanguageId(),
						getCompanyCode(),
						getPlantId(),
						refDocNumber, 
						0L);
		if (putAwayLine.isEmpty()) {
			throw new BadRequestException("The given values: " + 
					"refDocNumber: " + refDocNumber + 
					" doesn't exist.");
		} 
		return putAwayLine;
	}
	
	/**
	 * 
	 * @param searchPutAwayLine
	 * @return
	 * @throws Exception
	 */
	public List<PutAwayLine> findPutAwayLine(SearchPutAwayLine searchPutAwayLine) throws Exception {
		PutAwayLineSpecification spec = new PutAwayLineSpecification(searchPutAwayLine);
		List<PutAwayLine> results = putAwayLineRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createPutAwayLine
	 * @param newPutAwayLine
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PutAwayLine createPutAwayLine (AddPutAwayLine newPutAwayLine, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PutAwayLine dbPutAwayLine = new PutAwayLine();
		log.info("newPutAwayLine : " + newPutAwayLine);
		BeanUtils.copyProperties(newPutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(newPutAwayLine));
		dbPutAwayLine.setDeletionIndicator(0L);
		dbPutAwayLine.setCreatedBy(loginUserID);
		dbPutAwayLine.setUpdatedBy(loginUserID);
		dbPutAwayLine.setCreatedOn(new Date());
		dbPutAwayLine.setUpdatedOn(new Date());
		return putAwayLineRepository.save(dbPutAwayLine);
	}
	
	/**
	 * 
	 * @param newPutAwayLine
	 * @param loginUserID
	 * @param loginUserID2 
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public List<PutAwayLine> putAwayLineConfirm(@Valid List<AddPutAwayLine> newPutAwayLines, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		List<PutAwayLine> createdPutAwayLines = new ArrayList<>();
		
		try {
			for ( AddPutAwayLine newPutAwayLine : newPutAwayLines) {
				PutAwayLine dbPutAwayLine = new PutAwayLine();
				Warehouse warehouse = getWarehouse(newPutAwayLine.getWarehouseId());
				
				BeanUtils.copyProperties(newPutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(newPutAwayLine));
				if (newPutAwayLine.getCompanyCode() == null) {
					dbPutAwayLine.setCompanyCode(warehouse.getCompanyCode());
				} else {
					dbPutAwayLine.setCompanyCode(newPutAwayLine.getCompanyCode());
				}
				dbPutAwayLine.setPutawayConfirmedQty(newPutAwayLine.getPutawayConfirmedQty());
				dbPutAwayLine.setConfirmedStorageBin(newPutAwayLine.getConfirmedStorageBin());
				dbPutAwayLine.setStatusId(20L);
				dbPutAwayLine.setDeletionIndicator(0L);
				dbPutAwayLine.setCreatedBy(loginUserID);
				dbPutAwayLine.setUpdatedBy(loginUserID);
				dbPutAwayLine.setCreatedOn(new Date());
				dbPutAwayLine.setUpdatedOn(new Date());
				PutAwayLine createdPutAwayLine = putAwayLineRepository.save(dbPutAwayLine);
				log.info("createdPutAwayLine created: " + createdPutAwayLine);
				createdPutAwayLines.add(createdPutAwayLine);
				
				if (createdPutAwayLine != null && createdPutAwayLine.getPutawayConfirmedQty() > 0L ) {
				
					// Insert a record into INVENTORY table as below
					Inventory inventory = new Inventory();
					BeanUtils.copyProperties(createdPutAwayLine, inventory, CommonUtils.getNullPropertyNames(createdPutAwayLine));
					inventory.setCompanyCodeId(createdPutAwayLine.getCompanyCode());
					inventory.setVariantCode(1L); 				// VAR_ID
					inventory.setVariantSubCode("1"); 			// VAR_SUB_ID
					inventory.setStorageMethod("1"); 			// STR_MTD
					inventory.setBatchSerialNumber("1"); 		// STR_NO
					inventory.setStorageBin(createdPutAwayLine.getConfirmedStorageBin());
					
					AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
					StorageBin dbStorageBin = mastersService.getStorageBin(dbPutAwayLine.getConfirmedStorageBin(), authTokenForMastersService.getAccess_token());
					inventory.setBinClassId(dbStorageBin.getBinClassId());
					
					/*
					 * Insert PA_CNF_QTY value in this field. 
					 * Also Pass WH_ID/PACK_BARCODE/ITM_CODE/BIN_CL_ID=3 in INVENTORY table and fetch ST_BIN/INV_QTY value. 
					 * Update INV_QTY value by (INV_QTY - PA_CNF_QTY) . If this value becomes Zero, then delete the record"
					 */
					try {
						Inventory existinginventory = inventoryService.getInventory(createdPutAwayLine.getWarehouseId(), 
								createdPutAwayLine.getPackBarcodes(), dbPutAwayLine.getItemCode(), 3L);					
						double INV_QTY = existinginventory.getInventoryQuantity() - createdPutAwayLine.getPutawayConfirmedQty();
						log.info("INV_QTY : " + INV_QTY);

                        // [Prod Fix: 14-07] - Hareesh - Don't need to delete the inventory just update the existing inventory quantity
						// Deleting inventory
//						if (INV_QTY == 0) {
////							[Prod Fix: 28-06] - Discussed to comment delete Inventory operation to avoid unwanted delete of Inventory
////							inventoryRepository.delete(existinginventory);
//						} else
                            if (INV_QTY >= 0) {
							existinginventory.setInventoryQuantity(INV_QTY);
							Inventory updatedInventory = inventoryRepository.save(existinginventory);
							log.info("updatedInventory--------> : " + updatedInventory);
						}
					} catch (Exception e) {
						log.info("Existing Inventory---Error-----> : " + e.toString());
					}
					
					// INV_QTY
					inventory.setInventoryQuantity(createdPutAwayLine.getPutawayConfirmedQty());
					
					// INV_UOM
					inventory.setInventoryUom(createdPutAwayLine.getPutAwayUom());
					inventory.setCreatedBy(createdPutAwayLine.getCreatedBy());
					inventory.setCreatedOn(createdPutAwayLine.getCreatedOn());
					Inventory createdInventory = inventoryRepository.save(inventory);
					log.info("createdInventory : " + createdInventory);
					
					/* Insert a record into INVENTORYMOVEMENT table */
					InventoryMovement createdInventoryMovement = createInventoryMovement (createdPutAwayLine);
					log.info("inventoryMovement created: " + createdInventoryMovement);
				}
			}
			
			/*---------------STATUS updates----Update PUTAWAYHEADER table ------------------------
			 * Update PUTAWAYHEADER table with STATUS_ID = 20 by passing WH_ID/PRE_IB_NO/REF_DOC_NO/PA_NO
			 */
			for (PutAwayLine putAwayLine : createdPutAwayLines) {
				List<PutAwayHeader> headers = putAwayHeaderService.getPutAwayHeader(putAwayLine.getWarehouseId(), 
						putAwayLine.getPreInboundNo(), putAwayLine.getRefDocNumber(), putAwayLine.getPutAwayNumber());
				for (PutAwayHeader putAwayHeader : headers) {
					putAwayHeader.setStatusId(20L);
					putAwayHeader = putAwayHeaderRepository.save(putAwayHeader);
					log.info("putAwayHeader updated: " + putAwayHeader);
				}
				
				/*--------------------- INBOUNDTABLE Updates ------------------------------------------*/
				// Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE values in PUTAWAYLINE table and 
				// fetch PA_CNF_QTY values and QTY_TYPE values and updated STATUS_ID as 20
				
				double addedAcceptQty = 0.0;
				double addedDamageQty = 0.0;
				
				InboundLine inboundLine = inboundLineService.getInboundLine(putAwayLine.getWarehouseId(), 
						putAwayLine.getRefDocNumber(), putAwayLine.getPreInboundNo(), putAwayLine.getLineNo(), 
						putAwayLine.getItemCode());
				log.info("inboundLine-------------> " + inboundLine);
				
				// If QTY_TYPE = A, add PA_CNF_QTY with existing value in ACCEPT_QTY field
				if (putAwayLine.getQuantityType().equalsIgnoreCase("A")) {
					if (inboundLine.getAcceptedQty() != null) {
						addedAcceptQty = inboundLine.getAcceptedQty() + putAwayLine.getPutawayConfirmedQty();
					} else {
						addedAcceptQty = putAwayLine.getPutawayConfirmedQty();
					}
					
					inboundLine.setAcceptedQty(addedAcceptQty);
				}
				
				// if QTY_TYPE = D, add PA_CNF_QTY with existing value in DAMAGE_QTY field
				if (putAwayLine.getQuantityType().equalsIgnoreCase("D")) {
					if (inboundLine.getDamageQty() != null) {
						addedDamageQty = inboundLine.getDamageQty() + putAwayLine.getPutawayConfirmedQty();
					} else {
						addedDamageQty = putAwayLine.getPutawayConfirmedQty();
					}
					
					inboundLine.setDamageQty(addedDamageQty);
				}
				
				inboundLine.setStatusId(20L);
				inboundLine = inboundLineRepository.save(inboundLine);
				log.info("inboundLine updated : " + inboundLine);
			}
			return createdPutAwayLines;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 
	 * @param dbPutAwayLine
	 * @param createdinventory
	 * @return 
	 */
	private InventoryMovement createInventoryMovement(PutAwayLine dbPutAwayLine) {
		InventoryMovement inventoryMovement = new InventoryMovement();
		BeanUtils.copyProperties(dbPutAwayLine, inventoryMovement, CommonUtils.getNullPropertyNames(dbPutAwayLine));
		inventoryMovement.setCompanyCodeId(dbPutAwayLine.getCompanyCode());
		
		// MVT_TYP_ID
		inventoryMovement.setMovementType(1L);
		
		// SUB_MVT_TYP_ID
		inventoryMovement.setSubmovementType(2L);
		
		// VAR_ID
		inventoryMovement.setVariantCode(1L);
		
		// VAR_SUB_ID
		inventoryMovement.setVariantSubCode("1");
		
		// STR_MTD
		inventoryMovement.setStorageMethod("1");
		
		// STR_NO
		inventoryMovement.setBatchSerialNumber("1");
		
		// CASE_CODE
		inventoryMovement.setCaseCode("999999");
		
		// PAL_CODE
		inventoryMovement.setPalletCode("999999");
		
		// MVT_DOC_NO
		inventoryMovement.setMovementDocumentNo(dbPutAwayLine.getRefDocNumber());
		
		// ST_BIN
		inventoryMovement.setStorageBin(dbPutAwayLine.getConfirmedStorageBin());
		
		// MVT_QTY
		inventoryMovement.setMovementQty(dbPutAwayLine.getPutawayConfirmedQty());
		
		// MVT_QTY_VAL
		inventoryMovement.setMovementQtyValue("P");
		
		// MVT_UOM
		inventoryMovement.setInventoryUom(dbPutAwayLine.getPutAwayUom());
		
		/*
		 * -----THE BELOW IS NOT USED-------------
		 * Pass WH_ID/ITM_CODE/PACK_BARCODE/BIN_CL_ID is equal to 1 in INVENTORY table and fetch INV_QTY
		 * BAL_OH_QTY = INV_QTY
		 */
		// PASS WH_ID/ITM_CODE/BIN_CL_ID and sum the INV_QTY for all selected inventory
		List<Inventory> inventoryList = 
				inventoryService.getInventory (dbPutAwayLine.getWarehouseId(), dbPutAwayLine.getItemCode(), 1L);
		double sumOfInvQty = inventoryList.stream().mapToDouble(a->a.getInventoryQuantity()).sum();
		inventoryMovement.setBalanceOHQty(sumOfInvQty);
		
		// IM_CTD_BY
		inventoryMovement.setCreatedBy(dbPutAwayLine.getCreatedBy());
		
		// IM_CTD_ON
		inventoryMovement.setCreatedOn(dbPutAwayLine.getCreatedOn());
		inventoryMovement = inventoryMovementRepository.save(inventoryMovement);
		return inventoryMovement;
	}
	
	/**
	 * updatePutAwayLine
	 * @param loginUserId 
	 * @param confirmedStorageBin
	 * @param updatePutAwayLine
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PutAwayLine updatePutAwayLine (String warehouseId, String goodsReceiptNo, String preInboundNo, String refDocNumber, 
			String putAwayNumber, Long lineNo, String itemCode, String proposedStorageBin, List<String> confirmedStorageBin, 
			String loginUserID, UpdatePutAwayLine updatePutAwayLine) 
			throws IllegalAccessException, InvocationTargetException {
		PutAwayLine dbPutAwayLine = getPutAwayLine(warehouseId, goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, 
				lineNo, itemCode, proposedStorageBin, confirmedStorageBin);
		BeanUtils.copyProperties(updatePutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(updatePutAwayLine));
		dbPutAwayLine.setUpdatedBy(loginUserID);
		dbPutAwayLine.setUpdatedOn(new Date());
		return putAwayLineRepository.save(dbPutAwayLine);
	}
	
	/**
	 * 
	 * @param updatePutAwayLine
	 * @param loginUserID
	 * @return
	 */
	public PutAwayLine updatePutAwayLine ( UpdatePutAwayLine updatePutAwayLine, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PutAwayLine dbPutAwayLine = new PutAwayLine();
		BeanUtils.copyProperties(updatePutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(updatePutAwayLine));
		dbPutAwayLine.setUpdatedBy(loginUserID);
		dbPutAwayLine.setUpdatedOn(new Date());
		return putAwayLineRepository.save(dbPutAwayLine);
	}
	
	/**
	 * 
	 * @param asnNumber
	 */
	public void updateASN (String asnNumber) {
		List<PutAwayLine> putAwayLines = getPutAwayLines();
		putAwayLines.forEach(p -> p.setReferenceField1(asnNumber));
		putAwayLineRepository.saveAll(putAwayLines);
	}
	
	/**
	 * deletePutAwayLine
	 * @param loginUserID 
	 * @param confirmedStorageBin
	 */
	public void deletePutAwayLine (String languageId, String companyCodeId, String plantId, String warehouseId, 
			String goodsReceiptNo, String preInboundNo, String refDocNumber, String putAwayNumber, Long lineNo, 
			String itemCode, String proposedStorageBin, List<String> confirmedStorageBin, String loginUserID) {
		PutAwayLine putAwayLine = getPutAwayLine(warehouseId, goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, 
				lineNo, itemCode, proposedStorageBin, confirmedStorageBin);
		if ( putAwayLine != null) {
			putAwayLine.setDeletionIndicator(1L);
			putAwayLine.setUpdatedBy(loginUserID);
			putAwayLineRepository.save(putAwayLine);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + confirmedStorageBin);
		}
	}
}
