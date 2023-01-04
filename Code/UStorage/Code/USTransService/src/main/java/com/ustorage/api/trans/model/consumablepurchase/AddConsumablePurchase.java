package com.ustorage.api.trans.model.consumablepurchase;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddConsumablePurchase {

	@NotNull(message = "Item Code cannot be Null")
	private String itemCode;

	@NotBlank(message = "Receipt No is mandatory")
	private String receiptNo;

	private String receiptDate;
	private String vendor;
	private String quantity;
	private String warehouse;
	private String value;

	private String status;
		
	private Long deletionIndicator;

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

}
