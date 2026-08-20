package com.tekclover.wms.api.outbound.transaction.model.dto;


import lombok.Data;

@Data
public class UpdateOutboundOrder {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String sourceRefDocNumber;
    private String targetRefDocNumber;
}


