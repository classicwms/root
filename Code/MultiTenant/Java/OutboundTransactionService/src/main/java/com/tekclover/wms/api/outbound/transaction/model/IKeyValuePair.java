package com.tekclover.wms.api.outbound.transaction.model;


import java.util.Date;

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

    Double getAlterUomQty();
    Double getUomQty();

    Double getOrderQty();

    String getDriverName();

    String getRemarks();

    String getVehicleNo();

    String getItemText();

    String getBarcodeId();
    String getCompanyCodeId();
    String getLanguageId();
    String getPlantId();

    Double getDeliveryQty();

    String getOrigin();
    Date getExpiryDate();
    Date getMfrDate();
    String getMaterialNo();
    Double getPickConfirmQty();
}