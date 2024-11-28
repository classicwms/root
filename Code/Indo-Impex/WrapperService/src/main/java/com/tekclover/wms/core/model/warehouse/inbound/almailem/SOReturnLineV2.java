package com.tekclover.wms.core.model.warehouse.inbound.almailem;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SOReturnLineV2  {

	@NotNull(message = "Line Reference is mandatory")
	private Long lineReference;
	@NotBlank(message = "sku is mandatory")
	private String sku;
	@NotBlank(message = "sku description is mandatory")
	private String skuDescription;
//	@NotBlank(message = "Invoice Number is mandatory")
	private String invoiceNumber;
	private String storeID;
	private String supplierPartNumber;
//	@NotBlank(message = "Manufacturer Name is mandatory")
	private String manufacturerName;
	@NotBlank(message = "Expected Date is mandatory")
	private String expectedDate;
	@NotNull(message = "expectedQty is mandatory")
	private Double expectedQty;
	@NotBlank(message = "uom is mandatory")
	private String uom;
	private String origin;
//	@NotBlank(message = "Manufacturer Code is mandatory")
	private String manufacturerCode;
	private String brand;
	private String salesOrderReference;
	private Double packQty;

	private String manufacturerFullName;

	/*----------------Walkaroo changes------------------------------------------------------*/
	private String materialNo;
	private String priceSegment;
	private String articleNo;
	private String gender;
	private String color;
	private String size;
	private String noPairs;
	private String barcodeId;
	/*----------------------Impex--------------------------------------------------*/
	private String alternateUom;
	private Double noBags;
	private Double bagSize;
	private Double mrp;
}