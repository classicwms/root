package com.tekclover.wms.core.model.masters;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class BomHeader { 
	
	private String languageId;
	private String companyCode;
	private String plantId;
	private String warehouseId;
	private String parentItemCode;
	private Long bomNumber;
	private Double parentItemQuantity;
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
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
	private List<AddBomLine> bomLines;
	
	// Temp
	private List<String> lines;
}
