package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OutboundReversalV2 extends OutboundReversal {

	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;
	private String statusDescription;

	private String middlewareId;
	private String middlewareTable;
	private String referenceDocumentType;
	private String salesOrderNumber;
	private String targetBranchCode;
	private String salesInvoiceNumber;
	private String supplierInvoiceNo;
	private String pickListNumber;
	private String tokenNumber;
	private String manufacturerName;
	private String barcodeId;
	private Integer imsSaleTypeCode;

}