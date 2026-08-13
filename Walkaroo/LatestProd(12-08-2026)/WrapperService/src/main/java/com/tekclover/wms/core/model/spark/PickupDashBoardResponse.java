package com.tekclover.wms.core.model.spark;

import lombok.Data;

@Data
public class PickupDashBoardResponse {

    private String company;
    private String plant;
    private String warehouse;
    private String customerNo;
    private String customerName;
    private Long orderCount;
    private Double qty;
    private int leadTimeMinutes;
    private String orderNo;
}
