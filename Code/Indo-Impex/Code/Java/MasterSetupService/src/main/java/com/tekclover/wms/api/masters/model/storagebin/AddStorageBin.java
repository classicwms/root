package com.tekclover.wms.api.masters.model.storagebin;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AddStorageBin {

	@NotBlank(message = "Language is mandatory")
	private String languageId;
	
	@NotBlank(message = "Company Code Id is mandatory")
	private String companyCodeId;
	
	@NotBlank(message = "Plant Id is mandatory")
	private String plantId;
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;
	
	@NotBlank(message = "Storage Bin is mandatory")
	private String storageBin;
	
	private Long floorId;

	private Long levelId;
	
	private String storageSectionId;
	
	private String rowId;
	
	private String aisleNumber;
	
	private String spanId;	
	
	private String shelfId;
	
	private Long binSectionId;
	
	private Long storageTypeId;
	
	private Long binClassId;
	
	private String description ;

	private String occupiedVolume;

	private String occupiedWeight;

	private String occupiedQuantity;

	private String remainingVolume;

	private String remainingWeight;

	private String remainingQuantity;

	private String totalVolume;

	private String totalQuantity;

	private String totalWeight;
	
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
    
}
