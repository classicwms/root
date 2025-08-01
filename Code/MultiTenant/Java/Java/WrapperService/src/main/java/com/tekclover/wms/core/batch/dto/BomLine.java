package com.tekclover.wms.core.batch.dto;

import java.util.Date;

import lombok.Data;

@Data
public class BomLine { 
	// LANG_ID	C_ID	PLANT_ID	WH_ID	BOM_NO	CHL_ITEM_CODE	CHL_ITM_QTY	STATUS_ID	Isdeleted	CTD_BY
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long bomNumber;
	private String childItemCode;
	private Double childItemQuantity ;
	private Long statusId;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn;
	
	/**
	* @param languageId
	* @param companyCodeId
	* @param plantId
	* @param warehouseId
	* @param bomNumber
	* @param childItemCode
	* @param childItemQuantity 
	* @param statusId
	* @param deletionIndicator
	* @param createdBy
	*/
	public BomLine (String languageId, String companyCodeId, String plantId, String warehouseId, Long bomNumber, String childItemCode, 
					Double childItemQuantity , Long statusId, Long deletionIndicator, String createdBy, Date createdOn) {
		this.languageId = languageId;
		this.companyCodeId = companyCodeId;
		this.plantId = plantId;
		this.warehouseId = warehouseId;
		this.bomNumber = bomNumber;
		this.childItemCode = childItemCode;
		this.childItemQuantity  = childItemQuantity;
		this.statusId = statusId;
		this.deletionIndicator = deletionIndicator;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
	}
}
