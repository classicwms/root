package com.tekclover.wms.api.inbound.transaction.model.dto;

public interface InventoryBinItmGroupByDto {

    public String getItemText();
    public String getLanguageId();
    public String getCompanyCodeId();
    public String getPlantId();
    public String getWarehouseId();
    public Double getInvQty();
    public Double getAllQty();
    public Double getTotQty();
    public Double getNoBags();
    public Double getBagSizes();
    public String getManufacturerName();
    public String getStorageBin();
    public String getItemCode();

}
