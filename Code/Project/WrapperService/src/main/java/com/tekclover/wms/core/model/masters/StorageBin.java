package com.tekclover.wms.core.model.masters;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class StorageBin { 
	
	private String languageId;
	
	private String companyCodeId;
	
	private String plantId;
	
	private String warehouseId;
	
	private String storageBin;
	
	private Long floorId;
	
	private String storageSectionId;
	
	private String rowId;
	
	private String aisleNumber;
	
	private String spanId;
	
	private String shelfId;
	
	private Long binSectionId;
	
	private Long storageTypeId;
	
	private Long binClassId;
	
	private String description ;
	
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
	
	private Long deletionIndicator = 0L;
	
	private String createdBy;
	
	private Date createdOn;
	
	private String updatedBy;
	
	private Date updatedOn;

}
