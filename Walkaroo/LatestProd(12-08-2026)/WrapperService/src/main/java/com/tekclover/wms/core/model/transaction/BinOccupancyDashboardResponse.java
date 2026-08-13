package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import java.util.List;

@Data
public class BinOccupancyDashboardResponse {

    private Integer totalWarehouses;
    private Double totalCapacity;
    private Double totalUsed;
    private Double totalAvailable;

    private List<WarehouseOccupancyResponse> warehouses;
}