package com.tekclover.wms.core.batch.dto;

import lombok.Data;

@Data
public class Inventory { 
	
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String itemCode;
	private String packBarcode;
	private String storageBin;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private Long binClassId;
	private String description;
	private Double inventoryQuantity;
	private String inventoryUom;
	private Long deletionIndicator;
	private String createdBy;
	
	/**
	* @param languageId
	* @param companyCodeId
	* @param plantId
	* @param warehouseId
	* @param itemCode
	* @param packBarcode
	* @param storageBin
	* @param stockTypeId
	* @param specialStockIndicatorId
	* @param binClassId
	* @param description
	* @param inventoryQuantity
	* @param inventoryUom
	* @param deletionIndicator
	* @param createdBy
	*/
	public Inventory (String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode, String packBarcode, 
					  String storageBin, Long stockTypeId, Long specialStockIndicatorId, Long binClassId, String description, 
					  Double inventoryQuantity, String inventoryUom, Long deletionIndicator, String createdBy) {
		this.languageId = languageId;
		this.companyCodeId = companyCodeId;
		this.plantId = plantId;
		this.warehouseId = warehouseId;
		this.itemCode = itemCode;
		this.packBarcode = packBarcode;
		this.storageBin = storageBin;
		this.stockTypeId = stockTypeId;
		this.specialStockIndicatorId = specialStockIndicatorId;
		this.binClassId = binClassId;
		this.description = description;
		this.inventoryQuantity = inventoryQuantity;
		this.inventoryUom = inventoryUom;
		this.deletionIndicator = deletionIndicator;
		this.createdBy = createdBy;
	}
}
