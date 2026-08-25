package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class InboundOrderMobileApp {
    private String companyCode;
    private String plantId;
    private String warehouseId;
    private String languageId;
    private String oldBarcode;
    private String newBarcode;
    private Double orderQuantity;
}
