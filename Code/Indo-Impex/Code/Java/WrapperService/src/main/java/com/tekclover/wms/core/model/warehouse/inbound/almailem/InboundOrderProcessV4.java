package com.tekclover.wms.core.model.warehouse.inbound.almailem;

import lombok.Data;

import java.util.Date;

@Data
public class InboundOrderProcessV4 {

	private String asnNumber;
	private String companyCode;
	private String branchCode;
	private String languageId;
	private String warehouseId;
	private Long inboundOrderTypeId;
	private String purchaseOrderNumber;
	private String supplierCode;
	private String loginUserId;

	private String toCompanyCode;
	private String toBranchCode;
	private String transferOrderNumber;
	private String sourceCompanyCode;
	private String sourceBranchCode;
	private Date transferOrderDate;

	//Line fields
	private Long lineReference;
	private String sku;
	private String skuDescription;
	private Double expectedQty;
	private String uom;

	private String manufacturerName;
	private String manufacturerCode;
	private String manufacturerFullName;
	private String containerNumber;
	private String supplierPartNumber;
	private String expectedDate;
	private Date receivedDate;

	private Double receivedQty;
	private Double packQty;
	private String receivedBy;
	private String origin;
	private String brand;
	private String supplierName;
	private String supplierInvoiceNo;
	private String batchSerialNumber;
	private String barcodeId;
	private String gender;
	private String color;
	private String size;

	private String itemType;
	private String itemGroup;

	private String invoiceNumber;
	private String storeId;
	private String salesOrderReference;
	/*----------------------Impex--------------------------------------------------*/
	private String alternateUom;
	private Double noBags;
	private Double bagSize;
	private Double mrp;
}