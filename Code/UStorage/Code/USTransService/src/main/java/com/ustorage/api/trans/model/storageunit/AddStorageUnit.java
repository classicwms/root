package com.ustorage.api.trans.model.storageunit;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddStorageUnit {
	
	@NotNull(message = "Description cannot be Null")
	@NotBlank(message = "Description is mandatory")	
	private String description;

	private String codeId;
	
	private String itemType;

	private String sbu;
	
	private String itemGroup;
	
	private String doorType;
	
	private String storageType;
	
	private String phase;
	
	private String zone;
	
	private String room;
	
	private String rack;
	
	private String bin;
	
	private String priceMeterSquare;
	
	private String weekly;
	
	private String monthly;
	
	private String quarterly;
	
	private String halfYearly;
	
	private String yearly;
	
	private String length;
	
	private String width;
	
	private String originalDimention;

	private String roundedDimention;

	private String availability;
	
	private Date occupiedFrom;

	private Date availableAfter;
	
	private String linkedAgreement;

	private String status;

	private String storeSizeMeterSquare;
	private String aisle;
		
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
