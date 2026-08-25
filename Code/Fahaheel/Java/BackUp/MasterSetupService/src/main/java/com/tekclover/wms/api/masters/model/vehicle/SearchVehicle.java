package com.tekclover.wms.api.masters.model.vehicle;

import lombok.Data;

import java.util.List;

@Data
public class SearchVehicle {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String>vehicleNumber;
}
