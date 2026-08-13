package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.util.List;

@Data
public class CapacityUtilizationByPlantRequest {

    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> storageBin;

}
