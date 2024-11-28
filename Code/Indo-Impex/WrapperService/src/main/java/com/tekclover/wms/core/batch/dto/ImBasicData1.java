package com.tekclover.wms.core.batch.dto;

import lombok.Data;

@Data
public class ImBasicData1 {

	private String uomId;
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String itemCode;
	private String manufacturerPartNo;
	private String description;
	private Long itemType;
	private Long itemGroup;
	private Long subItemGroup;
	private String storageSectionId;
	private Long statusId;
	private Long deletionIndicator;
	private String createdBy;

	private String model;
	private String specifications1;
	private String specifications2;
	private String eanUpcNo;
	private String hsnCode;
	private Double totalStock;
	private Double minimumStock;
	private Double maximumStock;
	private Double reorderLevel;
	private Boolean capacityCheck;
	private Double replenishmentQty;
	private Double safetyStock;
	private String capacityUnit;
	private String capacityUom;
	private String quantity;
	private Double weight;
	private Boolean shelfLifeIndicator;
	private String dType;

	//fields from ImBasicData2
	private Double length;
	private Double width;
	private Double height;
	private String dimensionUom;
	private Double volume;

	//V2 fields
	private String manufacturerName;
	private String manufacturerFullName;
	private String manufacturerCode;
	private String brand;
	private String supplierPartNumber;
	private String remarks;

	/**
	 * @param uomId
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param itemCode
	 * @param manufacturerPartNo
	 * @param description
	 * @param model
	 * @param specifications1
	 * @param specifications2
	 * @param eanUpcNo
	 * @param hsnCode
	 * @param itemType
	 * @param itemGroup
	 * @param subItemGroup
	 * @param storageSectionId
	 * @param totalStock
	 * @param minimumStock
	 * @param maximumStock
	 * @param reorderLevel
	 * @param capacityCheck
	 * @param replenishmentQty
	 * @param safetyStock
	 * @param capacityUnit
	 * @param capacityUom
	 * @param quantity
	 * @param weight
	 * @param shelfLifeIndicator
	 * @param statusId
	 * @param length
	 * @param width
	 * @param height
	 * @param dimensionUom
	 * @param volume
	 * @param manufacturerName
	 * @param manufacturerFullName
	 * @param manufacturerCode
	 * @param brand
	 * @param supplierPartNumber
	 * @param remarks
	 * @param dType
	 * @param deletionIndicator
	 * @param createdBy
	 */
	public ImBasicData1(String uomId, String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode,
						String manufacturerPartNo, String description, String model, String specifications1, String specifications2,
						String eanUpcNo, String hsnCode, Long itemType, Long itemGroup, Long subItemGroup, String storageSectionId,
						Double totalStock, Double minimumStock, Double maximumStock, Double reorderLevel, Boolean capacityCheck,
						Double replenishmentQty, Double safetyStock, String capacityUnit, String capacityUom, String quantity,
						Double weight, Boolean shelfLifeIndicator, Long statusId, Double length, Double width, Double height,
						String dimensionUom, Double volume, String manufacturerName, String manufacturerFullName, String manufacturerCode,
						String brand, String supplierPartNumber, String remarks, String dType, Long deletionIndicator, String createdBy) {

		this.uomId = uomId;
		this.languageId = languageId;
		this.companyCodeId = companyCodeId;
		this.plantId = plantId;
		this.warehouseId = warehouseId;
		this.itemCode = itemCode;
		this.manufacturerPartNo = manufacturerPartNo;
		this.description = description;
		this.model = model;
		this.specifications1 = specifications1;
		this.specifications2 = specifications2;
		this.eanUpcNo = eanUpcNo;
		this.hsnCode = hsnCode;
		this.itemType = itemType;
		this.itemGroup = itemGroup;
		this.subItemGroup = subItemGroup;
		this.storageSectionId = storageSectionId;
		this.totalStock = totalStock;
		this.minimumStock = minimumStock;
		this.maximumStock = maximumStock;
		this.reorderLevel = reorderLevel;
		this.capacityCheck = capacityCheck;
		this.replenishmentQty = replenishmentQty;
		this.safetyStock = safetyStock;
		this.capacityUnit = capacityUnit;
		this.capacityUom = capacityUom;
		this.quantity = quantity;
		this.weight = weight;
		this.shelfLifeIndicator = shelfLifeIndicator;
		this.statusId = statusId;
		this.length = length;
		this.width = width;
		this.height = height;
		this.dimensionUom = dimensionUom;
		this.volume = volume;
		this.manufacturerName = manufacturerName;
		this.manufacturerFullName = manufacturerFullName;
		this.manufacturerCode = manufacturerCode;
		this.brand = brand;
		this.supplierPartNumber = supplierPartNumber;
		this.remarks = remarks;
		this.dType = dType;
		this.deletionIndicator = deletionIndicator;
		this.createdBy = createdBy;
	}
}
