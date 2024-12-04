package com.tekclover.wms.api.transaction.model.dto;

import lombok.Data;

@Data
public class UpdateConsignmentSlip {
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