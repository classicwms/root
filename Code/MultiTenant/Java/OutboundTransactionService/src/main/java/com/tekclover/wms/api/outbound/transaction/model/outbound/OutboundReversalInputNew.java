package com.tekclover.wms.api.outbound.transaction.model.outbound;

import lombok.Data;

@Data
public class OutboundReversalInputNew {

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private String preOutboundNo;

    private String refDocNumber;

    private String palletCode;

    private String pickupNumber;
}
