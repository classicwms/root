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
public class PickupHeader { 
	
private String languageId;
	
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private String pickupNumber;
	private Long lineNumber;
	private String itemCode;
	private String proposedStorageBin;
	private String proposedPackBarCode;
	private Long outboundOrderTypeId;
	private Double pickToQty;
	private String pickUom;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private String manufacturerPartNo;
	private Long statusId;
	private String assignedPickerId;
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
	private String remarks;
	private String pickupCreatedBy;
	private Date pickupCreatedOn;
	private String pickConfimedBy;
	private String pickUpdatedBy;
	private Date pickUpdatedOn;
	private String pickupReversedBy;
	private Date pickupReversedOn;
}
