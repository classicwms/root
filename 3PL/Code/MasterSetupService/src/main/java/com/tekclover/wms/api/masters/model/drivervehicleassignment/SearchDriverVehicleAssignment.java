package com.tekclover.wms.api.masters.model.drivervehicleassignment;

import lombok.Data;

import java.util.List;

@Data
public class SearchDriverVehicleAssignment {

    private List<String> languageId;

    private List<String> companyCodeId;

    private List<String> plantId;

    private List<String> warehouseId;

    private List<Long> driverId;

    private List<String> vehicleNumber;

    private List<Long> routeId;
}
