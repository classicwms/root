package com.tekclover.wms.core.model.transaction;

import java.util.List;

import lombok.Data;

@Data
public class SearchInventoryMovement {
	/*
	 * WH_ID
	 * MVT_TYP_ID
	 * SUB_MVT_TYP_ID
	 * PAL_CODE
	 * CASE_CODE
	 * PACK_BARCODE
	 * ITM_CODE
	 * VAR_ID
	 * VAR_SUB_ID
	 * STR_NO
	 * MVT_DOC_NO
	 */
	 
	private List<String> warehouseId;
	private List<Long> movementType;
	private List<Long> submovementType;
	private List<String> palletCode;
	private List<String> caseCode;
	private List<String> packBarcodes;
	private List<String> itemCode;
	private List<Long> variantCode;
	private List<String> variantSubCode;
	private List<String> batchSerialNumber;
	private List<String> movementDocumentNo;
}
