package com.tekclover.wms.api.transaction.model.dto;

public interface OperationConsumptionImpl {

    String getCompanyDescription();
    String getPlantDescription();
    String getWarehouseDescription();

    String getItemCode();
    String getItemDescription();
    String getRequiredQuantity();
    String getIssuedQuantity();
    String getConsumedQuantity();
    String getLoss();

    Double getReceipeQuantity();
    Long getProductionOrderLineNo();
    String getUom();
    String getProductionOrderNo();
    String getBatchNumber();
    String getProductionOrderType();

    String getCompanyCodeId();
    String getPlantId();
    Double getBomQuantity();
    String getOperationNumber();
    String getParentProductionOrderNo();
}