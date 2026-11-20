package com.tekclover.wms.api.inbound.transaction.model.impl;

import java.util.Date;

public interface InventoryImpl {

    String getLanguageId();
    String getCompanyCodeId();
    String getPlantId();
    String getWarehouseId();
    String getPalletCode();
    String getCaseCode();
    String getPackBarcodes();
    String getItemCode();
    Long getVariantCode();
    String getVariantSubCode();
    String getBatchSerialNumber();
    String getStorageBin();
    Long getStockTypeId();
    Long getSpecialStockIndicatorId();
    String getReferenceOrderNo();
    String getStorageMethod();
    Long getBinClassId();
    String getDescription();
    Double getInventoryQuantity();
    Double getAllocatedQuantity();
    String getInventoryUom();
    Date getManufacturerDate();
    Date getExpiryDate();
    Long getDeletionIndicator();
    String getReferenceField1();
    String getReferenceField2();
    String getReferenceField3();
    Double getReferenceField4();
    String getReferenceField5();
    String getReferenceField6();
    String getReferenceField7();
    String getReferenceField8();
    String getReferenceField9();
    String getReferenceField10();
    String getAsnNumber();
    String getCreatedBy();
    Date getCreatedOn();
    String getUpdatedBy();
    Date getUpdatedOn();
}
