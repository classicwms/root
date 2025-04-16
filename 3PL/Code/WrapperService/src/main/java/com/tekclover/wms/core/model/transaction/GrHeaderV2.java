package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString(callSuper = true)
public class GrHeaderV2 extends GrHeader {

	private Double acceptedQuantity;
	private Double damagedQuantity;
	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;
	private String statusDescription;
	private String middlewareId;
	private String middlewareTable;
	private String referenceDocumentType;
	private Date transferOrderDate;
	private String isCompleted;
	private String isCancelled;
	private Date mUpdatedOn;
	private String sourceBranchCode;
	private String sourceCompanyCode;
	private String customerCode;
	private String TransferRequestType;
	private String AMSSupplierInvoiceNo;
}