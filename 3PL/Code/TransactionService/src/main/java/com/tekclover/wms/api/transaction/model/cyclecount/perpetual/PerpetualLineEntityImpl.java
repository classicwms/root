package com.tekclover.wms.api.transaction.model.cyclecount.perpetual;

public interface PerpetualLineEntityImpl {
	String getLanguageId();
	String getCompanyCodeId();
	String getPlantId();
	String getWarehouseId();
	String getItemCode();
	String getStorageBin();
	Long getStockTypeId();
	Long getSpecialStockIndicator();
	String getPackBarcodes();
	Double getInventoryQuantity();
	String getInventoryUom();
	String getItemDesc();
	String getManufacturerPartNo();
	String getStorageSectionId();
	String getManufacturerName();
	String getDescription();
	String getCompanyDescription();
	String getPlantDescription();
	String getWarehouseDescription();
	String getCycleCountNo();
	String getBarcodeId();
}
