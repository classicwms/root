package com.tekclover.wms.api.idmaster.model.hhtuser;

import java.util.Date;

import lombok.Data;

@Data
public class UpdateHhtUser {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String userId;
	private String password;
	private String userName;
	private Long statusId;
	private Boolean caseReceipts;
	private Boolean itemReceipts;
	private Boolean putaway;
	private Boolean transfer;
	private Boolean picking;
	private Boolean quality;
	private Boolean inventory;
	private Boolean customerReturn;
	private Boolean supplierReturn;
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
}
