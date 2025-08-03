package com.tekclover.wms.api.inbound.orders.model;


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

    Double getNoOfCase();
    Double getPlusTolerance();

    Double getAlterUomQty();
    Double getUomQty();

}
