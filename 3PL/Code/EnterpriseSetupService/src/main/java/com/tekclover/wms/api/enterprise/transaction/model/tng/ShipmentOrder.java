package com.tekclover.wms.api.enterprise.transaction.model.tng;

import lombok.Data;

import java.util.List;

@Data
public class ShipmentOrder {

    private String orderReference;
    private String storerKey;
    private String warehouseKey;
    private String consigneeName;
    private List<Skus> skus;

}
