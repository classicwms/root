package com.tekclover.wms.api.inbound.transaction.model.cyclecount.stockadjustment;

import lombok.Data;

import java.util.List;

@Data
public class SearchStockAdjustment{

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;

	private List<String> barcodeId;
	private List<String> manufacturerCode;
	private List<String> manufacturerName;
	private List<String> referenceDocumentNo;
	private List<Long> stockAdjustmentId;
	private List<Long> stockAdjustmentKey;

	private List<String> warehouseId;
	private List<String> packBarcodes;
	private List<String> itemCode;
	private List<String> storageBin;
	private List<Long> stockTypeId;
	private List<Long> specialStockIndicatorId;
	private List<Long> binClassId;
}