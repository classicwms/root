package com.tekclover.wms.core.model.dto;

import lombok.Data;

@Data
public class MultiDbInput {

    private String companyCode;
    private String languageId;
    private String plantId;
    private String warehouseId;
}
