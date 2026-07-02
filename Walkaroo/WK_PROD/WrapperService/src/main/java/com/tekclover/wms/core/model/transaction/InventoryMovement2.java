package com.tekclover.wms.core.model.transaction;

import java.text.ParseException;
import java.util.Date;

import com.tekclover.wms.core.util.DateUtils;

import lombok.Data;

@Data
public class InventoryMovement2 {
	
	private String warehouseId;
	private Long movementType;
	private Long submovementType;
	private String palletCode;
	private String packBarcodes;
	private String itemCode;
	private String storageBin;
	private String description;
	private Double movementQty;
	private String inventoryUom;
	private String refDocNumber;
	private Date createdOn;
	private String movementDocumentNo;
	
	/**
	 * InventoryMovement2
	 * @param warehouseId
	 * @param movementType
	 * @param submovementType
	 * @param palletCode
	 * @param packBarcodes
	 * @param itemCode
	 * @param storageBin
	 * @param description
	 * @param movementQty
	 * @param inventoryUom
	 * @param refDocNumber
	 * @param createdOn
	 * @throws ParseException 
	 */
	public InventoryMovement2(String warehouseId,
			Long movementType,
			Long submovementType,
			String palletCode,
			String packBarcodes,
			String itemCode,
			String storageBin,
			String description,
			Double movementQty,
			String inventoryUom,
			String refDocNumber,
			Date createdOn,
			String movementDocumentNo) {
		this.warehouseId = warehouseId;
		this.movementType = movementType;
		this.submovementType = submovementType;
		this.palletCode = palletCode;
		this.itemCode = itemCode;
		this.storageBin = storageBin;
		this.description = description;
		this.movementQty = movementQty;
		this.inventoryUom = inventoryUom;
		this.refDocNumber = refDocNumber;
		this.packBarcodes = packBarcodes;
		this.createdOn = createdOn;
		this.movementDocumentNo = movementDocumentNo;
	}
}
