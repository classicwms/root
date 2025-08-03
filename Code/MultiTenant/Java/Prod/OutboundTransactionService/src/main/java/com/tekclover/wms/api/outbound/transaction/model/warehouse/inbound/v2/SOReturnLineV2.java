package com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.v2;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SOReturnLineV2  {

	@Column(nullable = false)
	@NotNull(message = "Line Reference is mandatory")
	private Long lineReference;

	@Column(nullable = false)
	@NotBlank(message = "sku is mandatory")
	private String sku;

	@Column(nullable = false)
	@NotBlank(message = "sku description is mandatory")
	private String skuDescription;

	@Column(nullable = false)
	@NotBlank(message = "Invoice Number is mandatory")
	private String invoiceNumber;
	private String storeID;
	private String supplierPartNumber;

	@Column(nullable = false)
	@NotBlank(message = "Manufacturer Name is mandatory")
	private String manufacturerName;

	@Column(nullable = false)
	@NotBlank(message = "Expected Date is mandatory")
	private String expectedDate;

	@Column(nullable = false)
	@NotNull(message = "expectedQty is mandatory")
	private Double expectedQty;

	@Column(nullable = false)
	@NotBlank(message = "uom is mandatory")
	private String uom;
	private Double packQty;
	private String origin;

	@Column(nullable = false)
	@NotBlank(message = "Manufacturer Code is mandatory")
	private String manufacturerCode;
	private String brand;
	private String salesOrderReference;
	private String orderType;

	private String manufacturerFullName;
	//MiddleWare Fields
	private Long middlewareId;
	private Long middlewareHeaderId;
	private String middlewareTable;


}
