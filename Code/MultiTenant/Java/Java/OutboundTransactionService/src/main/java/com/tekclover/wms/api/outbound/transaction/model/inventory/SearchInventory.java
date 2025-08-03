package com.tekclover.wms.api.outbound.transaction.model.inventory;

import lombok.Data;

import java.util.List;

@Data
public class SearchInventory {
	/*
	 * WH_ID
	 * PACK_BARCODE
	 * ITM_CODE
	 * ST_BIN
	 * STCK_TYP_ID
	 * SP_ST_IND_ID
	 */
	 
	private List<String> warehouseId;
	private List<String> packBarcodes;
	private List<String> itemCode;
	private List<String> storageBin;
	private List<String> storageSectionId;
	private List<Long> stockTypeId;
	private List<Long> specialStockIndicatorId;
	private List<Long> binClassId;
	private String description;
}