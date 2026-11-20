package com.tekclover.wms.core.repository;

import java.io.Serializable;

import lombok.Data;

@Data
public class InventoryMovementCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `MVT_TYP_ID`, `SUB_MVT_TYP_ID`, `PACK_BARCODE`, `ITM_CODE`, `STR_NO`, `MVT_DOC_NO`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long movementType;
	private Long submovementType;
	private String packBarcodes;
	private String itemCode;
	private String batchSerialNumber;
	private String movementDocumentNo;
}
