package com.tekclover.wms.api.transaction.model.report;

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
}
