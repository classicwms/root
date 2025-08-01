package com.tekclover.wms.core.model.warehouse.inbound.almailem;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class SOReturnLineV2  {

	//	@Column(nullable = false)
//	@NotNull(message = "Line Reference is mandatory")
	private Long lineReference;

	//	@Column(nullable = false)
//	@NotBlank(message = "sku is mandatory")
	private String sku;

	//	@Column(nullable = false)
//	@NotBlank(message = "sku description is mandatory")
	private String skuDescription;

	//	@Column(nullable = false)
//	@NotBlank(message = "Invoice Number is mandatory")
	private String invoiceNumber;
	private String storeID;
	private String supplierPartNumber;

	//	@Column(nullable = false)
//	@NotBlank(message = "Manufacturer Name is mandatory")
	private String manufacturerName;

	private String storageSectionId;

	//	@Column(nullable = false)
//	@NotBlank(message = "Expected Date is mandatory")
	private String expectedDate;

	//	@Column(nullable = false)
//	@NotNull(message = "expectedQty is mandatory")
	private Double expectedQty;

	//	@Column(nullable = false)
//	@NotBlank(message = "uom is mandatory")
	private String uom;
	private Double packQty;
	private String origin;

	//	@Column(nullable = false)
//	@NotBlank(message = "Manufacturer Code is mandatory")
	private String manufacturerCode;
	private String brand;
	private String salesOrderReference;
	private String orderType;

	private String manufacturerFullName;
	//MiddleWare Fields
	private Long middlewareId;
	private Long middlewareHeaderId;
	private String middlewareTable;

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

	/*------------------------Namratha Fields -------------------------------*/
	private String inwardDate;
	private Double expectedQtyInPieces;
	private Double expectedQtyInCases;
	private String unloadingIncharge;
	private Long totalUnLoaders;
	private String vehicleNo;
	private Date vehicleReportingDate;
	private Date vehicleUnloadingDate;
	private String customerId;
	private String customerName;
}