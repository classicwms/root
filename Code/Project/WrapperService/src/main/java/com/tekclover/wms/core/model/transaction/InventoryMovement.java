package com.tekclover.wms.core.model.transaction;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

	public class InventoryMovement { 

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long movementType;
	private Long submovementType;
	private String palletCode;
	private String caseCode;
	private String packBarcodes;
	private String itemCode;
	private Long variantCode;
	private String variantSubCode;
	private String batchSerialNumber;
	private String movementDocumentNo;
	private String storageBin;
	private String storageMethod;
	private String description;
	private Long stockTypeId;
	private String specialStockIndicator;
	private String movementQtyValue;
	private Double movementQty;
	private String inventoryUom;
	private String refDocNumber;
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
	private Double balanceOHQty;
}
