package com.tekclover.wms.api.inbound.transaction.model;

import lombok.Data;

@Data
public class DocumentNumber {

    String refDocNumber;
    String preOutboundNo;
    String companyCodeId;
    String plantId;
    String warehouseId;
    String languageId;
}