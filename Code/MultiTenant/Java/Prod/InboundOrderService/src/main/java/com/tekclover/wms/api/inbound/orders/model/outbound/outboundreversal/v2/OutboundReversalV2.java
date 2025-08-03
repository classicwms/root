package com.tekclover.wms.api.inbound.orders.model.outbound.outboundreversal.v2;

import com.tekclover.wms.api.inbound.orders.model.outbound.outboundreversal.OutboundReversal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(callSuper = true)
public class OutboundReversalV2 extends OutboundReversal {

	@Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
	private String companyDescription;

	@Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
	private String plantDescription;

	@Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
	private String warehouseDescription;

	@Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
	private String statusDescription;

	@Column(name = "MIDDLEWARE_ID")
	private Long middlewareId;

	@Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
	private String middlewareTable;

	@Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
	private String referenceDocumentType;

	@Column(name = "SALES_ORDER_NUMBER", columnDefinition = "nvarchar(150)")
	private String salesOrderNumber;

	@Column(name = "TARGET_BRANCH_CODE", columnDefinition = "nvarchar(50)")
	private String targetBranchCode;

	@Column(name = "SALES_INVOICE_NUMBER", columnDefinition = "nvarchar(150)")
	private String salesInvoiceNumber;

	@Column(name = "SUPPLIER_INVOICE_NO", columnDefinition = "nvarchar(150)")
	private String supplierInvoiceNo;

	@Column(name = "PICK_LIST_NUMBER", columnDefinition = "nvarchar(150)")
	private String pickListNumber;

	@Column(name = "TOKEN_NUMBER", columnDefinition = "nvarchar(150)")
	private String tokenNumber;

	@Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
	private String manufacturerName;

	@Column(name = "PARTNER_ITEM_BARCODE", columnDefinition = "nvarchar(255)")
	private String barcodeId;

	@Column(name = "IMS_SALE_TYP_CODE")
	private Integer imsSaleTypeCode;

	@Column(name = "CSTR_COD", columnDefinition = "nvarchar(50)")
	private String customerCode;

	@Column(name = "TFR_REQ_TYP", columnDefinition = "nvarchar(50)")
	private String TransferRequestType;

}