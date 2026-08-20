package com.tekclover.wms.api.inbound.orders.model;


public interface IKeyValuePair {

    String getCompanyDesc();

    String getPlantDesc();

    String getWarehouseDesc();

    Double getInventoryQty();

    String getItemCode();
    Long getItemType();
    String getItemTypeDescription();

    String getManufacturerName();
    String getManufacturerCode();
    String getReferenceCycleCountNo();

    String getRefDocNumber();
    String getAssignPicker();

    String getWarehouseId();

    String getRefDocType();

    Long getPickerCount();
    Long getLineNumber();
    Double getAlterUomQty();
    Double getUomQty();
    String getReelNo();
    String getPreOutboundNo();
    Double getOrderQty();
    Double getDeliveryQty();

    String getStorageSectionId();
    String getUnitType();

    String getItemDescription();

    String getUom();
}
