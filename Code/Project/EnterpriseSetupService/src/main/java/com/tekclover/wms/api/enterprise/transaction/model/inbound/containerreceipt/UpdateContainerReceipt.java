package com.tekclover.wms.api.enterprise.transaction.model.inbound.containerreceipt;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateContainerReceipt {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preInboundNo;
	private String refDocNumber;
	private String containerReceiptNo;
	private Date containerReceivedDate;
	private String containerNo;
	private Long statusId;
	private String containerType;
	private String partnerCode;
	private String invoiceNo;
	private String consignmentType;
	private String origin;
	private String numberOfPallets;
	private String numberOfCases;
	private String dockAllocationNo;
	private String remarks;
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
	private String referenceField10;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}