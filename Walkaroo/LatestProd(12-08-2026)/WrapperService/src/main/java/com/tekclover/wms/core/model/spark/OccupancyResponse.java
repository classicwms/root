package com.tekclover.wms.core.model.spark;

import lombok.Data;

@Data
public class OccupancyResponse {

    private String company;
    private String plant;
    private String warehouse;
    private String storageBin;
    private String binCapacity;
    private Double occupiedCapacity;

}
