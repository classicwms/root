package com.tekclover.wms.api.inbound.transaction.model.dto;

import lombok.Data;

@Data
public class MultiDbInput {

    private String companyCode;
    private String languageId;
    private String plantId;
    private String warehouseId;
}
