package com.tekclover.wms.api.transaction.model.tng;

import lombok.Data;

@Data
public class CancelShipmentOrder {

    private String orderReference;
    private String warehouseKey;
    private String orderKey;
}
