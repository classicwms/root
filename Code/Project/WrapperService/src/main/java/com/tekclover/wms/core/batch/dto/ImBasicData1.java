package com.tekclover.wms.core.batch.dto;

import lombok.Data;

@Data
public class ImBasicData1 { 
	
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String itemCode;
	private String uomId;
	private String description;
	private String manufacturerPartNo;
	private Long itemType;
	private Long itemGroup;
	private Long subItemGroup;
	private String storageSectionId;
	private Long statusId;
	private Long deletionIndicator;
	private String createdBy;
	
	/**
	* @param languageId
	* @param companyCodeId
	* @param plantId
	* @param warehouseId
	* @param itemCode
	* @param uomId
	* @param description
	* @param manufacturerPartNo
	* @param itemType
	* @param itemGroup
	* @param subItemGroup
	* @param storageSectionId
	* @param statusId
	* @param deletionIndicator
	* @param createdBy
	*/
	public ImBasicData1 (String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode, String uomId,
						 String description, String manufacturerPartNo, Long itemType, Long itemGroup, Long subItemGroup, 
						 String storageSectionId, Long statusId, Long deletionIndicator, String createdBy) {
		this.languageId = languageId;
		this.companyCodeId = companyCodeId;
		this.plantId = plantId;
		this.warehouseId = warehouseId;
		this.itemCode = itemCode;
		this.uomId = uomId;
		this.description = description;
		this.manufacturerPartNo = manufacturerPartNo;
		this.itemType = itemType;
		this.itemGroup = itemGroup;
		this.subItemGroup = subItemGroup;
		this.storageSectionId = storageSectionId;
		this.statusId = statusId;
		this.deletionIndicator = deletionIndicator;
		this.createdBy = createdBy;
	}
}
