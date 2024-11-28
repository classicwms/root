package com.tekclover.wms.api.enterprise.model;

public interface IkeyValuePair {
    String getCompanyCodeId();
    String getDescription();
    String getVerticalId();
    String getPlantId();
    String getWarehouseId();

    Long getSubItemGroupId();
    String getStrategyNoText();
    String getFloorId();
    String getStorageClassId();
    String getStorageTypeId();
    String getLevelId();
    Long getItemTypeId();
    String getVariantId();

    Long getItemGroupId();
    String getStorageBinTypeId();
    String getWarehouseTypeId();

    String getCompanyDescription();
    String getPlantDescription();
    String getWarehouseDescription();
    String getLevelDescription();
    String getVariantDescription();
}
