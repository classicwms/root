package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class InventoryV2 extends Inventory {

	private Long inventoryId;
	private String barcodeId;
	private String cbm;
	private String cbmUnit;
	private String cbmPerQuantity;
	private String manufacturerCode;
	private String manufacturerName;
	private String origin;
	private String brand;
	private String referenceDocumentNo;
	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;
//	private String statusDescription;
	private String levelId;

	private String sCreatedOn;

    private String stockTypeDescription;

	//threePl
	private Double threePLCbm;
	private String threePLUom;
	private String threePLBillStatus;
	private Double threePLLength;
	private Double threePLHeight;
	private Double threePLWidth;
	private Double totalThreePLCbm;
	private Double totalRate;
	private String threePLPartnerId;
	private String threePLPartnerText;

	//DashBoard
	private Double totalVolumes;
	private Double occupiedVolumes;
	private Double remainingVolumes;
}