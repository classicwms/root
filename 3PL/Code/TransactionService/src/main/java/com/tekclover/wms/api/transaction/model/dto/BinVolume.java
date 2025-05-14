package com.tekclover.wms.api.transaction.model.dto;



public interface BinVolume {

    String getStorageBin();
    Double getOccupiedVolume();   // SUM(total_tpl_cbm)
    Double getOccupancyVolume();
    String getCompanyDescription();
    String getPlantDescription();
    String getWarehouseDescription();
}
