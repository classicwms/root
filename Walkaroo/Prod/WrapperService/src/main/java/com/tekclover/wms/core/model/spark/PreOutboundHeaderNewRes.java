package com.tekclover.wms.core.model.spark;

import lombok.Data;

@Data
public class PreOutboundHeaderNewRes {

    private Double customerTotalQty;
    private String shipToCode;
    private String shipToParty;

    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Double totalQty;
    private String refDocNumber;
}
