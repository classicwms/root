package com.tekclover.wms.api.inbound.orders.model.report;

import lombok.Data;

@Data
public class InventoryReport {

	private String warehouseId;			// WH_ID
	private String itemCode;			// ITM_CODE
	private String description;			// ITEM_TEXT
	private String uom;					// INV_UOM
	private String storageBin;			// ST_BIN
	private String storageSectionId;	// ST_SEC_ID 
	private String packBarcodes;		// PACK_BARCODE
	private Double inventoryQty;		// INV_QTY
	private Long stockType;			    // STCK_TYP_TEXT
	private String mfrPartNumber;       // MFR PART NO
	private Double allocatedQty;		// ALLOCATED_QTY
	private Double totalQuantity;		// ALLOCATED_QTY

	//from storage bin
	private String aisleId;       // AISLE_ID
	private String levelId;		// SHELF_ID
	private String rowId;		// ROW_ID

}
