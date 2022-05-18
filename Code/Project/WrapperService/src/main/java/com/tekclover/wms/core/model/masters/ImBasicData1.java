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

public class ImBasicData1 { 
	
	private String languageId;
	
	private String companyCodeId;
	
	private String plantId;
	
	private String warehouseId;
	
	private String itemCode;
	
	private String uomId;
	
	private String description;
	
	private String model;
	
	private String specifications1;
	
	private String specifications2;
	
	private String eanUpcNo;
	
	private String manufacturerPartNo;
	
	private String hsnCode;
	
	private Long itemType;
	
	private Long itemGroup;
	
	private Long subItemGroup;
	
	private String storageSectionId;
	
	private Double totalStock;
	
	private Double minimumStock;
	
	private Double maximumStock;
	
	private Double reorderLevel;
	
	private Double replenishmentQty;
	
	private Double safetyStock;
	
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
