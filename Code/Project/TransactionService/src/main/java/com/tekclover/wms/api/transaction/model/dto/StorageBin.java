package com.tekclover.wms.api.transaction.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class StorageBin { 
	
	private String storageBin;
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long floorId;
	private String storageSectionId;
	private String rowId;
	private String aisleNumber;
	private String spanId;
	private String shelfId;
	private Long binSectionId;
	private Long storageTypeId;
	private Long binClassId;
	private String description;
	private String binBarcode;
	private Integer putawayBlock;
	private Integer pickingBlock;
	private String blockReason;
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
    private Date createdOn = new Date();
    private String updatedBy;
	private Date updatedOn = new Date();
}
