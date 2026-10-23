package com.tekclover.wms.api.inbound.transaction.model.dto;

import lombok.Data;

@Data
public class OutboundOrderReversal {

    private String companyCodeId;

    private String languageId;

    private String plantId;

    private String warehouseId;

    private String refDocNumber;

}
