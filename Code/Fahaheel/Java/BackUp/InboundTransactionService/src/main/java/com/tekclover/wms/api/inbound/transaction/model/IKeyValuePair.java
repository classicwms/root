package com.tekclover.wms.api.inbound.transaction.model;


public interface IKeyValuePair {

    String getCompanyDesc();

    String getPlantDesc();

    String getWarehouseDesc();
    Double getInventoryQty();
    Double getOrdQty();
    Double getRxdQty();

    String getItemCode();
    Long getItemType();
    String getItemTypeDescription();

    String getManufacturerName();

    String getReferenceCycleCountNo();

    String getRefDocNumber();
    String getAssignPicker();

    String getWarehouseId();

    String getRefDocType();

    Long getPickerCount();
    Long getLineNumber();
    String getUserRole();

}
