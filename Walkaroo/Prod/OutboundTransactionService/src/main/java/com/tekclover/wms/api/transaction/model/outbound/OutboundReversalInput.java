package com.tekclover.wms.api.transaction.model.outbound;

import lombok.Data;

@Data
public class OutboundReversalInput {

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private String refDocNumber;

}
