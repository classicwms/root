package com.tekclover.wms.core.batch.dto;

import java.util.Date;

import lombok.Data;

@Data
public class HandlingEquipment { 
	
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String handlingEquipmentId;
	private String category;
	private String handlingUnit;
	private String acquistionDate;
	private String acquistionValue;
	private String currencyId;
	private String modelNo;
	private String manufacturerPartNo;
	private String countryOfOrigin;
	private String heBarcode;
	private Long statusId;
	private Long deletionIndicator;
	private String createdBy;

	
	/**
	* @param languageId
	* @param companyCodeId
	* @param plantId
	* @param warehouseId
	* @param handlingEquipmentId
	* @param category
	* @param handlingUnit
	* @param acquistionDate
	* @param acquistionValue
	* @param currencyId
	* @param modelNo
	* @param manufacturerPartNo
	* @param countryOfOrigin
	* @param heBarcode
	* @param statusId
	* @param deletionIndicator
	* @param createdby
	*/
	public HandlingEquipment (String languageId, String companyCodeId, String plantId, String warehouseId, String handlingEquipmentId, 
					String category, String handlingUnit, String acquistionDate, String acquistionValue, String currencyId, 
					String modelNo, String manufacturerPartNo, String countryOfOrigin, String heBarcode, Long statusId, 
					Long deletionIndicator, String createdBy) {
		this.languageId = languageId;
		this.companyCodeId = companyCodeId;
		this.plantId = plantId;
		this.warehouseId = warehouseId;
		this.handlingEquipmentId = handlingEquipmentId;
		this.category = category;
		this.handlingUnit = handlingUnit;
		this.acquistionDate = acquistionDate;
		this.acquistionValue = acquistionValue;
		this.currencyId = currencyId;
		this.modelNo = modelNo;
		this.manufacturerPartNo = manufacturerPartNo;
		this.countryOfOrigin = countryOfOrigin;
		this.heBarcode = heBarcode;
		this.statusId = statusId;
		this.deletionIndicator = deletionIndicator;
		this.createdBy = createdBy;
	}
}
