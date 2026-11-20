package com.tekclover.wms.core.model.spark;

import lombok.Data;

@Data
public class CapacityUtilizationByPlantResponse {

    private String warehouseId;
    private String companyCodeId;
    private String plantId;
    private Double capacity;
    private Double used;
    private Double available;
    private Double percent;
    //    private String status;
    private String binCode;
}
