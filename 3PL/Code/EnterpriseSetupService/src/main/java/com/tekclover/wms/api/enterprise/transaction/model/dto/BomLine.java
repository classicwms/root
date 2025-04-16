package com.tekclover.wms.api.enterprise.transaction.model.dto;

import lombok.Data;

@Data
public class BomLine {

	private String languageId;
	
	private String companyCode;
	
	private String plantId;
	
	private String warehouseId;
	
	private Long bomNumber;
	
	private String childItemCode;
	
	private Double childItemQuantity;

	private Long statusId;
	
	private String referenceField1;
	
	private String referenceField2;
	
	private String referenceField3;
	
	private String referenceField4;
	
	private String referenceField5;
	
	private String referenceField6;
	
	private String referenceField7;
	
	private String referenceField8;
	
	private String referenceField9;
	
	private String referenceField10;
	
	private Long deletionIndicator;
	
	private String createdBy;
    
}