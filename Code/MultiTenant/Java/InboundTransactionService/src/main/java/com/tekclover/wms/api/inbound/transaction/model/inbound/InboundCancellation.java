package com.tekclover.wms.api.inbound.transaction.model.inbound;

import lombok.Data;

import java.util.List;

@Data
public class InboundCancellation {

    private String companyCode;

    private String plantId;

    private String warehouseId;

    private String itemCode;

    private String preInboundNo;

    private String refDocNumber;

    private List<Long> lineNo;
}
