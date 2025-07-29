package com.tekclover.wms.core.batch.dto;

import java.util.Date;

import lombok.Data;

@Data
public class BomHeader { 
	
	private String languageId;
	private String companyCode;
	private String plantId;
	private String warehouseId;
	private String parentItemCode;
	private Long bomNumber;
	private Double parentItemQuantity;
	private Long statusId;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn;
	
	/**
	 * 
	 * @param languageId
	 * @param companyCode
	 * @param plantId
	 * @param warehouseId
	 * @param parentItemCode
	 * @param bomNumber
	 * @param parentItemQuantity
	 * @param statusId
	 * @param deletionIndicator
	 * @param createdBy
	 */
	public BomHeader (String languageId, String companyCode, String plantId, String warehouseId, String parentItemCode,
			Long bomNumber, Double parentItemQuantity, Long statusId, Long deletionIndicator, String createdBy, Date createdOn) {
		this.languageId = languageId;
		this.companyCode = companyCode;
		this.plantId = plantId;
		this.warehouseId = warehouseId;
		this.parentItemCode = parentItemCode;
		this.bomNumber = bomNumber;
		this.parentItemQuantity = parentItemQuantity;
		this.statusId = statusId;
		this.deletionIndicator = deletionIndicator;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
	}
}
