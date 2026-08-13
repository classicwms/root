package com.tekclover.wms.api.inbound.transaction.model.dashboard;

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