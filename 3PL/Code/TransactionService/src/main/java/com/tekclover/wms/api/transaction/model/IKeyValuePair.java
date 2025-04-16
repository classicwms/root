package com.tekclover.wms.api.transaction.model;


import java.util.Date;

public interface IKeyValuePair {

    String getCompanyDesc();

    String getPlantDesc();

    String getWarehouseDesc();

    Double getInventoryQty();

    String getItemCode();

    String getManufacturerName();

    String getReferenceCycleCountNo();

    String getRefDocNumber();
    String getAssignPicker();

    String getWarehouseId();

    String getRefDocType();

    Long getPickerCount();
    Long getLineNumber();
    Double getTotalRate();
    Double getTotalCbm();
    String getCurrency();
    //Dash Board
    Double getSumOfTotalVolumes();
    Double getSumOfOccupiedVolumes();
    Double getSumOfRemainingVolumes();

    Long getInventoryId();
    String getThreePLPartnerId();
    Long getBinClassId();
    String getStorageBin();
    Double getTotalVolumes();
    Double getOccupiedVolumes();
    Double getRemainingVolumes();

    Double getOrderQty();
    Date getFromDate();
    Date getToDate();
    public String getSku();
    String getSkuDescription();


}
