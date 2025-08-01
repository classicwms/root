package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;

@Data
@EqualsAndHashCode(callSuper = true)
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
	private String customerId;
	private String customerName;

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
	private String itemType;
	private String itemGroup;

	/*----------------------REEFERON--------------------------------------------------*/
	private Double qtyInCase;
	private Double qtyInPiece;
	private Double qtyInCrate;

}