package com.tekclover.wms.api.inbound.transaction.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class HandlingEquipment { 
	
	private Long handlingEquipmentId;
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;	
	private String category;
	private String handlingUnit;
	private Date acquistionDate;
	private Double acquistionValue;
	private Long currencyId;
	private String modelNo;
	private String manufacturerPartNo;
	private String countryOfOrigin;
	private String heBarcode;
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
