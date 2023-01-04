package com.ustorage.core.batch.dto;

import lombok.Data;

import java.util.Date;

@Data
public class StorageUnit {
	private String itemCode;
	private String codeId;
	private String description;
	private String itemType;
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

	public StorageUnit(String itemCode,
					   String codeId,
					   String description,
					   String itemType,
					   String itemGroup,
					   String doorType,
					   String storageType,
					   String phase,
					   String zone,
					   String room,
					   String rack,
					   String bin,
					   String priceMeterSquare,
					   String weekly,
					   String monthly,
					   String quarterly,
					   String halfYearly,
					   String yearly,
					   String length,
					   String width,
					   String originalDimention,
					   String roundedDimention,
					   String availability,
					   Date occupiedFrom,
					   Date availableAfter,
					   String linkedAgreement,
					   String status,
					   String storeSizeMeterSquare,
					   String aisle,

					   Long deletionIndicator,
					   String referenceField1,
					   String referenceField2,
					   String referenceField3,
					   String referenceField4,
					   String referenceField5,
					   String referenceField6,
					   String referenceField7,
					   String referenceField8,
					   String referenceField9,
					   String referenceField10,
					   String createdBy,
					   Date createdOn,
					   String updatedBy,
					   Date updatedOn) {

		this.itemCode= itemCode;
		this.codeId= codeId;
		this.description= description;
		this.itemType= itemType;
		this.itemGroup= itemGroup;
		this.doorType= doorType;
		this.storageType= storageType;
		this.phase= phase;
		this.zone= zone;
		this.room= room;
		this.rack= rack;
		this.bin= bin;
		this.priceMeterSquare= priceMeterSquare;
		this.weekly= weekly;
		this.monthly= monthly;
		this.quarterly= quarterly;
		this.halfYearly= halfYearly;
		this.yearly= yearly;
		this.length= length;
		this.width= width;
		this.originalDimention= originalDimention;
		this.roundedDimention= roundedDimention;
		this.availability= availability;
		this.occupiedFrom= occupiedFrom;
		this.availableAfter= availableAfter;
		this.linkedAgreement= linkedAgreement;
		this.status= status;
		this.storeSizeMeterSquare= storeSizeMeterSquare;
		this.aisle= aisle;

		this.deletionIndicator= deletionIndicator ;
		this.referenceField1= referenceField1;
		this.referenceField2= referenceField2;
		this.referenceField3= referenceField3;
		this.referenceField4= referenceField4;
		this.referenceField5= referenceField5;
		this.referenceField6= referenceField6;
		this.referenceField7= referenceField7;
		this.referenceField8= referenceField8;
		this.referenceField9= referenceField9;
		this.referenceField10= referenceField10;
		this.createdBy= createdBy;
		this.createdOn= createdOn;
		this.updatedBy= updatedBy;
		this.updatedOn= updatedOn;
	}
}
