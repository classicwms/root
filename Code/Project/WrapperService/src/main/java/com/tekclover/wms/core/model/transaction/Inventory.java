package com.tekclover.wms.core.model.transaction;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Inventory { 
	
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String palletCode;
	private String caseCode;
	private String packBarcodes;
	private String itemCode;
	private Long variantCode;
	private String variantSubCode;
	private String batchSerialNumber;
	private String storageBin;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private String referenceOrderNo;
	private String storageMethod;
	private Long binClassId;
	private String description;
	private Double inventoryQuantity;
	private Double allocatedQuantity;
	private String inventoryUom;
	private Date manufacturerDate;
	private Date expiryDate;
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
	private String referenceField10;	//Storage Section Id
	private String createdBy;
	private Date createdOn = new Date();
}
