package com.tekclover.wms.api.transaction.model.outboundreversal;

import lombok.Data;

@Data
public class OutboundReversalInput {


    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private String preOutboundNo;

    private String refDocNumber;

}
