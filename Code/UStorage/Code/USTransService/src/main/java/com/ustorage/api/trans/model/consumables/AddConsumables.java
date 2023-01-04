package com.ustorage.api.trans.model.consumables;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddConsumables {

	@NotNull(message = "Description cannot be Null")
	@NotBlank(message = "Description is mandatory")		
	private String description;

	private String codeId;
	
	private String itemType;

	private String sbu;
	
	private String itemGroup;
	
	private String purchaseUnitOfMeasure;
	
	private String saleUnitOfMeasure;
	
	private String inventoryUnitOfMeasure;
	
	private String unitPrice;
	private String saleUnitPrice;
	
	private String length;
	
	private String width;
	
	private String height;
	
	private String volume;
	
	private String weight;
	
	private String warehouse;
	
	private String status;
	
	private String inStock;
	
	private String openWo;
	
	private String lastReceipt;
		
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
