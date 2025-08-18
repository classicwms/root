package com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class ASNHeaderV2  {

	@Column(nullable = false)
	@NotBlank(message = "Branch Code is mandatory")
	private String branchCode;

	@Column(nullable = false)
	@NotBlank(message = "Company Code is mandatory")
	private String companyCode;

	@Column(nullable = false)
	@NotBlank(message = "ASN number is mandatory")
	private String asnNumber;
	private String AMSSupplierInvoiceNo;

	private String warehouseId;
	private String languageId;
	private String parentProductionOrderNo;

	//almailem fields
	private String purchaseOrderNumber;
	private Long inboundOrderTypeId;
	private String supplierCode;
	private String loginUserId;

	//almailem fields
//	private String purchaseOrderNumber;

	private String isCompleted;
	private Date updatedOn;
	private String isCancelled;

	//MiddleWare Fields
	private Long middlewareId;
	private String middlewareTable;
	private String customerId;
	private  String customerName;

}
