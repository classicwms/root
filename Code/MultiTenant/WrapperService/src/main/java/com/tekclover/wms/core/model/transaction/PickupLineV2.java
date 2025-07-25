package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PickupLineV2 extends PickupLine {

	private Double inventoryQuantity;
	private Double pickedCbm;
	private String cbmUnit;
    private String manufacturerCode;
	private String manufacturerName;
	private String origin;
	private String brand;
	private String barcodeId;
	private String levelId;
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
	private Double varianceQuantity;
	private Integer imsSaleTypeCode;
	private String manufacturerFullName;
	private Long isPickupLineCreated;
	private String storageSectionId;
	private String customerId;
	private String customerName;
	private String materialNo;
	private String priceSegment;
	private String articleNo;
	private String gender;
	private String color;
	private String size;
	private String noPairs;
	private Double allocatedQty;
	private String alternateUom;
	private Double noBags;
	private Double bagSize;
	private Double mrp;
	private String itemType;
	private String itemGroup;
	private Double actualInventoryQty;
	private Double qtyInCase;
	private Double qtyInPiece;
	private Double qtyInCrate;
	private Date manufacturerDate;
	private Date expiryDate;
}