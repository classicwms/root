package com.tekclover.wms.api.transaction.model.outbound;

import java.util.Date;

import lombok.Data;

import javax.persistence.Column;

@Data
public class UpdateOutboundHeader {

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
	private String deliveryConfirmedBy;
	private Date deliveryConfirmedOn;
	private String updatedBy;
	private Date updatedOn;
	private String reversedBy;
	private Date reversedOn;
	private Boolean pdfPrint;
}
