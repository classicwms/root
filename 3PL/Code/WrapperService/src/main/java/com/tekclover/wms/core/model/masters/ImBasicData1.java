package com.tekclover.wms.core.model.masters;

import java.util.Date;

import lombok.Data;

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

	private Boolean capacityCheck;

	private Boolean shelfLifeIndicator;

	private Double minimumStock;
	
	private Double maximumStock;
	
	private Double reorderLevel;

	private Double replenishmentQty;

	private Double safetyStock;

	private String capacityUnit;

	private String capacityUom;

	private String quantity;

	private Double weight;

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

	private Double length;

	private Double width;

	private Double height;

	private String dimensionUom;

	private Double volume;

	private String manufacturerName;
	private String manufacturerFullName;
	private String manufacturerCode;
	private String brand;
	private String supplierPartNumber;
	private String remarks;

	private String isNew;
	private String isUpdate;
	private String isCompleted;

	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;

	//MiddleWare Fields
	private Long middlewareId;
	private String middlewareTable;

	private Long selfLife;
}
