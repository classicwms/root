package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;

@Repository
@Transactional
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement,Long>, JpaSpecificationExecutor<InventoryMovement> {
	
	/**
	 * 
	 */
	public List<InventoryMovement> findAll();
	
	/**
	 * 
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
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
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<InventoryMovement> 
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndMovementTypeAndSubmovementTypeAndPalletCodeAndCaseCodeAndPackBarcodesAndItemCodeAndVariantCodeAndVariantSubCodeAndBatchSerialNumberAndMovementDocumentNoAndDeletionIndicator(
				String languageId, String companyCodeId, String plantId, String warehouseId, Long movementType, Long submovementType, String palletCode, String caseCode, String packBarcodes, String itemCode, Long variantCode, String variantSubCode, String batchSerialNumber, String movementDocumentNo, Long deletionIndicator);

	public Optional<InventoryMovement> findByMovementType(Long movementType);
}