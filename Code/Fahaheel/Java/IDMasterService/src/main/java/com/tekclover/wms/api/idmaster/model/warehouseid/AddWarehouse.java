package com.tekclover.wms.api.idmaster.model.warehouseid;

import java.util.Date;

import lombok.Data;

@Data
public class AddWarehouse { 
	
	private String companyCodeId;
	private String warehouseId;
	private String languageId;
	private String plantId;
	private String warehouseDesc;
	private Long deletionIndicator = 0L;
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
	private Long noOfUsers;
}
