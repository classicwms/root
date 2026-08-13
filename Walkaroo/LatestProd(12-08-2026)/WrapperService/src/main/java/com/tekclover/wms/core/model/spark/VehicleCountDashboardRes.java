package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class VehicleCountDashboardRes {

    private String warehouseId;
    private String companyCodeId;
    private String plantId;
    private String date;
    private Long truckCount;
}
