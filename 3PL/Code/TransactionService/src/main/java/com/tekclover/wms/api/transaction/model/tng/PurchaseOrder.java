package com.tekclover.wms.api.transaction.model.tng;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseOrder {

    private String poKey;
    private String storerKey;
    private String warehouseKey;
    private List<Sku> skus;

}
