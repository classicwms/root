package com.tekclover.wms.core.model.warehouse.inbound.walkaroo;

import lombok.Data;

@Data
public class ReversalLineV3 {
    private String orderNumber;
    private String huSerialNo;
    private String material;
    private String priceSegement;
    private String plant;
    private String storageLocation;
    private String skuCode;
}