package com.tekclover.wms.core.model.mfg;

import lombok.Data;

@Data
public class OperationConsumptionImpl {


    String companyDescription;
    String plantDescription;
    String warehouseDescription;

    String itemCode;
    String itemDescription;
    String receipeQuantity;
    String issuedQuantity;
    String consumedQuantity;
    Double loss;
    Double yield;
}