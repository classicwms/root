package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class WarehouseOccupancyResponse {

    private String warehouseId;
    private String companyCodeId;
    private String plantId;

    private Double capacity;
    private Double used;
    private Double available;

    private Double percent;

    private String status;

    private String plantText;
    private String warehouseText;
}