package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.SearchInventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.UpdateInventoryMovement;
import com.tekclover.wms.api.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.transaction.repository.specification.InventoryMovementSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InventoryMovementService extends BaseService {
	
	@Autowired
	private InventoryMovementRepository inventoryMovementRepository;
	
	/**
	 * getInventoryMovements
	 * @return
	 */
	public List<InventoryMovement> getInventoryMovements () {
		List<InventoryMovement> inventoryMovementList =  inventoryMovementRepository.findAll();
		inventoryMovementList = 
				inventoryMovementList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return inventoryMovementList;
	}
	
	/**
	 * getInventoryMovement
	 * @param movementType
	 * @return
	 */
	public InventoryMovement getInventoryMovement (String warehouseId, Long movementType, Long submovementType, String palletCode, 
			String caseCode, String packBarcodes, String itemCode, Long variantCode, String variantSubCode, 
			String batchSerialNumber, String movementDocumentNo) {
		Optional<InventoryMovement> inventoryMovement = 
				inventoryMovementRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndMovementTypeAndSubmovementTypeAndPalletCodeAndCaseCodeAndPackBarcodesAndItemCodeAndVariantCodeAndVariantSubCodeAndBatchSerialNumberAndMovementDocumentNoAndDeletionIndicator(
						getLanguageId(),
						getCompanyCode(),
						getPlantId(),
						warehouseId, 
						movementType, 
						submovementType, 
						palletCode, 
						caseCode, 
						packBarcodes, 
						itemCode, 
						variantCode, 
						variantSubCode, 
						batchSerialNumber, 
						movementDocumentNo,
						0L
						);
		if (inventoryMovement.isEmpty()) {
			throw new BadRequestException("The given PreInboundHeader ID : " +
										", warehouseId: " + warehouseId + 
										", movementType: " + movementType + 
										", submovementType: " + submovementType + 
										", palletCode: " + palletCode + 	
										", caseCode: " + caseCode +
										", packBarcodes: " + packBarcodes + 
										", itemCode: " + itemCode + 
										", variantCode: " + variantCode + 
										", variantSubCode: " + variantSubCode + 
										", batchSerialNumber: " + batchSerialNumber + 
										" doesn't exist.");
		} 
		return inventoryMovement.get();
	}
	
	/**
	 * 
	 * @param searchInventoryMovement
	 * @return
	 */
	public List<InventoryMovement> findInventoryMovement(SearchInventoryMovement searchInventoryMovement) 
				throws ParseException {
		InventoryMovementSpecification spec = new InventoryMovementSpecification(searchInventoryMovement);
		List<InventoryMovement> results = inventoryMovementRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * 		
	 * @param newInventoryMovement
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public InventoryMovement createInventoryMovement (AddInventoryMovement newInventoryMovement, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		InventoryMovement dbInventoryMovement = new InventoryMovement();
		log.info("newInventoryMovement : " + newInventoryMovement);
		BeanUtils.copyProperties(newInventoryMovement, dbInventoryMovement, CommonUtils.getNullPropertyNames(newInventoryMovement));
		dbInventoryMovement.setDeletionIndicator(0L);
		dbInventoryMovement.setCreatedBy(loginUserID);
		dbInventoryMovement.setCreatedOn(new Date());
		return inventoryMovementRepository.save(dbInventoryMovement);
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param movementType
	 * @param submovementType
	 * @param palletCode
	 * @param caseCode
	 * @param packBarcodes
	 * @param itemCode
	 * @param variantCode
	 * @param variantSubCode
	 * @param batchSerialNumber
	 * @param movementDocumentNo
	 * @param loginUserID
	 * @param updateInventoryMovement
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public InventoryMovement updateInventoryMovement (String warehouseId, Long movementType, Long submovementType, String palletCode, 
			String caseCode, String packBarcodes, String itemCode, Long variantCode, String variantSubCode, String batchSerialNumber,
			String movementDocumentNo, UpdateInventoryMovement updateInventoryMovement) 
			throws IllegalAccessException, InvocationTargetException {
		InventoryMovement dbInventoryMovement = 
				getInventoryMovement(warehouseId, movementType, submovementType, palletCode, caseCode, packBarcodes, itemCode, 
						variantCode, variantSubCode, batchSerialNumber, movementDocumentNo);
		BeanUtils.copyProperties(updateInventoryMovement, dbInventoryMovement, 
				CommonUtils.getNullPropertyNames(updateInventoryMovement));
		return inventoryMovementRepository.save(dbInventoryMovement);
	}
	
	/**
	 * deleteInventoryMovement
	 * @param loginUserID 
	 * @param movementType
	 */
	public void deleteInventoryMovement (String warehouseId, Long movementType, Long submovementType, String palletCode, 
			String caseCode, String packBarcodes, String itemCode, Long variantCode, String variantSubCode, String batchSerialNumber, 
			String movementDocumentNo, String loginUserID) {
		InventoryMovement inventoryMovement = getInventoryMovement(warehouseId, movementType, submovementType, palletCode, 
				caseCode, packBarcodes, itemCode, 
				variantCode, variantSubCode, batchSerialNumber, movementDocumentNo);
		if ( inventoryMovement != null) {
			inventoryMovement.setDeletionIndicator(1L);
			inventoryMovementRepository.save(inventoryMovement);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + movementType);
		}
	}
}
