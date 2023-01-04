package com.ustorage.api.trans.model.itemservice;

import lombok.Data;

import java.util.Date;
@Data
public class UpdateItemService {

    private String itemServiceName;

    private String itemName;
    private Double itemServiceQuantity;
    private Double itemServiceUnitPrice;
    private Double itemServiceTotal;
}