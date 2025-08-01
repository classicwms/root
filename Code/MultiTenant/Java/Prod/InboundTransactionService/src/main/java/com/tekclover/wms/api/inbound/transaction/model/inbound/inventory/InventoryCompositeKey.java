package com.tekclover.wms.api.inbound.transaction.model.inbound.inventory;

import java.io.Serializable;

import lombok.Data;

@Data
public class InventoryCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `INV_ID`, `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PACK_BARCODE`, `ITM_CODE`, `ST_BIN`, `STCK_TYP_ID`, `SP_ST_IND_ID`
	 */
	private Long inventoryId;
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String packBarcodes;
	private String itemCode;
	private String storageBin;
	private Long specialStockIndicatorId;
	private String manufacturerCode;
}
