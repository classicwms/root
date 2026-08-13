package com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2;

import java.util.Date;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

import lombok.Data;

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

	private String warehouseId;
	private String languageId;
	//almailem fields
	private String purchaseOrderNumber;
	private String parentProductionOrderNo;

	private String isCompleted;
	private Date updatedOn;
	private String isCancelled;
	private Long inboundOrderTypeId;
	private String supplierCode;
	private String loginUserId;

	//MiddleWare Fields
	private Long middlewareId;
	private String middlewareTable;
	private String reversalFlag;
	
	//-------InboundUpload Vs SAP API -------Differentiator
	private Boolean isSapOrder;
}