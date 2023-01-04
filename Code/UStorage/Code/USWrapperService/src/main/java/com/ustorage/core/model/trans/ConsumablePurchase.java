package com.ustorage.core.model.trans;

import lombok.Data;

import java.util.Date;

@Data
public class ConsumablePurchase {

	//private String id;
	private String itemCode;
	private String receiptNo;
	private String receiptDate;
	private String vendor;
	private String quantity;
	private String warehouse;
	private String value;
	private String status;

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
	private String createdBy;
	private Date createdOn;
	private String updatedBy;
	private Date updatedOn;
}
