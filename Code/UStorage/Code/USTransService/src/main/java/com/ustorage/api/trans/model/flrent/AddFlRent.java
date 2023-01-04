package com.ustorage.api.trans.model.flrent;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddFlRent {

	private String description;

	private String codeId;

	private String itemType;

	private String sbu;

	private String itemGroup;

	private String saleUnitOfMeasure;

	private String unitPrice;

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
