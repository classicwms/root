package com.tekclover.wms.api.outbound.transaction.model.stock;

import lombok.Data;

import java.io.Serializable;

@Data
public class InventoryStockCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PACK_BARCODE`, `ITM_CODE`, `ST_BIN`, `STCK_TYP_ID`, `SP_ST_IND_ID`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String packBarcodes;
	private String itemCode;
	private String storageBin;
	private Long specialStockIndicatorId;
}
