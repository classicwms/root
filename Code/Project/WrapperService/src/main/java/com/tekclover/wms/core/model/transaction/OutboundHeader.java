package com.tekclover.wms.core.model.transaction;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class OutboundHeader {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private String deliveryOrderNo;
	private String referenceDocumentType;
	private Long outboundOrderTypeId;
	private Long statusId;
	private Date refDocDate = new Date();
	private Date requiredDeliveryDate;
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
	private String createdBy;
	private Date createdOn = new Date();
	private String deliveryConfirmedBy;
	private Date deliveryConfirmedOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
	private String reversedBy;
	private Date reversedOn = new Date();

	private List<OutboundHeader> addOutboundHeader;
}
