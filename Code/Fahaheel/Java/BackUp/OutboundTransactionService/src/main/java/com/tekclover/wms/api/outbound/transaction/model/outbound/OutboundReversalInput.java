package com.tekclover.wms.api.outbound.transaction.model.outbound;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
public class OutboundReversalInput {

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private String preOutboundNo;

    private String refDocNumber;

}
