package com.tekclover.wms.api.inbound.orders.model.inbound.stock.v2;

import lombok.Data;

import java.io.Serializable;

@Data
public class InventoryStockV2CompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PACK_BARCODE`, `ITM_CODE`, `ST_BIN`, `STCK_TYP_ID`, `SP_ST_IND_ID`
	 */
	private String manufacturerName;
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String packBarcodes;
	private String itemCode;
	private String storageBin;
	private Long specialStockIndicatorId;
}