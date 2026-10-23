package com.tekclover.wms.api.inbound.transaction.model.dashboard;

public interface BinOccupancyProjection {

    String getStorageBin();

    Double getOpeningQty();

    Double getOccupaidQty();

    String getCompanyCodeId();

    String getPlantId();
    String getWarehouseId();

    Double getBalanceQty();

    Double getOccupaidPercentage();

    String getPlantText();
    String getWarehouseText();
}