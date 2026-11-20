package com.tekclover.wms.core.model.spark;

import lombok.Data;

@Data
public class OccupancyDashboardCountRes {

    private String warehouseId;
    private String companyCodeId;
    private String plantId;
    private Long total;
    private Long occupied;
    private Long free;
    private Double percent;
}
