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
public class PeriodicLine { 
	
	private String companyCode;
	private String plantId;
	private String warehouseId;
	private String cycleCountNo;
	private String storageBin;
	private String itemCode;
	private String packBarcodes;
	private Long variantCode;
	private String variantSubCode;
	private String batchSerialNumber;
	private Long stockTypeId;
	private String specialStockIndicator;
	private Double inventoryQuantity;
	private String inventoryUom;
	private Double countedQty;
	private Double varianceQty;
	private String cycleCounterId;
	private String cycleCounterName;
	private Long statusId;
	private String cycleCountAction;
	private String referenceNo;
	private Long approvalProcessId;
	private String approvalLevel;
	private String approverCode;
	private String approvalStatus;
	private String remarks;
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
	private String confirmedBy;
	private Date confirmedOn = new Date();
	private String countedBy;
	private Date countedOn = new Date();

}
