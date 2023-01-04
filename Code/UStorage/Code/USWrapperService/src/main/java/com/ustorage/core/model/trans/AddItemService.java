package com.ustorage.core.model.trans;

import lombok.Data;

@Data
public class AddItemService {

    private String itemServiceName;
    private String itemName;
    private Double itemServiceQuantity;
    private Double itemServiceUnitPrice;
    private Double itemServiceTotal;
}
