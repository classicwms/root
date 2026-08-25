package com.tekclover.wms.api.enterprise.transaction.model.mnc;

import lombok.Data;

@Data
public class UpdateInhouseTransferHeader {

	private String languageId;
	
	private String companyCodeId;

	private String plantId;

	private String warehouseId;
	
	private String transferNumber;

	private Long transferTypeId;	
	
	private String transferMethod;	
	
	private Long statusId;
	
	private String remarks;	

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

	private String updatedBy;
	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;
	private String statusDescription;
}