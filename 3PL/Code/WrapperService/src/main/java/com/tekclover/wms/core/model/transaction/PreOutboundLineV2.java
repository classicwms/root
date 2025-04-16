package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class PreOutboundLineV2 extends PreOutboundLine {

	private String manufacturerCode;
	private String manufacturerName;
	private String origin;
	private String brand;
	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;
	private String statusDescription;

	private String middlewareId;
	private String middlewareTable;
	private String middlewareHeaderId;
	private String referenceDocumentType;
	private String manufactureFullName;
	private String salesOrderNumber;
	private String supplierInvoiceNo;
	private String salesInvoiceNumber;
	private String pickListNumber;
	private String tokenNumber;
	private String targetBranchCode;
	private Integer imsSaleTypeCode;
}