package com.tekclover.wms.core.model.spark;

import lombok.Data;

@Data
public class AgingByCategoryResponse {

    private String company;
    private String plant;
    private String warehouse;
    private String sku;
    private String itemName;
    private String receivingDate;
    private Long daysInStock;
    private Double qty;
    private String bucket;
    private String binId;
    private String category;

}
