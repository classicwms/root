package com.tekclover.wms.api.enterprise.model.dto;

import lombok.Data;

@Data
public class UpdateOutboundHeader {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String languageId;
    private String preOutboundNo;
    private String invoiceNumber;
    private String partyName;
    private String partyLocation;
    private String transportName;
    private String loginUserId;
}