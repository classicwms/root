package com.tekclover.wms.api.enterprise.transaction.model.outbound.preoutbound;

import lombok.Data;

import java.util.Date;

@Data
public class UpdatePreOutboundHeader {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String refDocNumber;
	private String preOutboundNo;
	private String partnerCode;
	private Long outboundOrderTypeId;
	private String referenceDocumentType;
	private Long statusId;
	private Date refDocDate;
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
	private Date createdOn;
	private String updatedBy;
	private Date updatedOn;
}