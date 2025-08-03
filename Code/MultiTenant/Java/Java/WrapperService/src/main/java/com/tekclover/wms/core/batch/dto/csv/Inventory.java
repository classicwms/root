package com.tekclover.wms.core.batch.dto.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Data
public class Inventory { 
	
	@CsvBindByPosition(position = 0)
	@CsvBindByName
	private String languageId;
	
	@CsvBindByPosition(position = 1)
	@CsvBindByName
	private String companyCodeId;
	
	@CsvBindByPosition(position = 2)
	@CsvBindByName
	private String plantId;
	
	@CsvBindByPosition(position = 3)
	@CsvBindByName
	private String warehouseId;
	
	@CsvBindByPosition(position = 4)
	@CsvBindByName
	private String itemCode;
	
	@CsvBindByPosition(position = 5)
	@CsvBindByName
	private String packBarcode;
	
	@CsvBindByPosition(position = 6)
	@CsvBindByName
	private String storageBin;
	
	@CsvBindByPosition(position = 7)
	@CsvBindByName
	private Long stockTypeId;
	
	@CsvBindByPosition(position = 8)
	@CsvBindByName
	private Long specialStockIndicatorId;
	
	@CsvBindByPosition(position = 9)
	@CsvBindByName
	private Long binClassId;
	
	@CsvBindByPosition(position = 10)
	@CsvBindByName
	private String description;
	
	@CsvBindByPosition(position = 11)
	@CsvBindByName
	private Double inventoryQuantity;
	
	@CsvBindByPosition(position = 12)
	@CsvBindByName
	private String inventoryUom;
	
	@CsvBindByPosition(position = 13)
	@CsvBindByName
	private Long deletionIndicator;
	
	@CsvBindByPosition(position = 14)
	@CsvBindByName
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
