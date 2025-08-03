package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class OutboundReversalInput {

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private String preOutboundNo;

    private String refDocNumber;

}
