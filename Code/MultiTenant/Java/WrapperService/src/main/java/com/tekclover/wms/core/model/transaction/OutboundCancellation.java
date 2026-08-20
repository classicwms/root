package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class OutboundCancellation {

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private String itemCode;

    private String preOutboundNo;

    private String refDocNumber;

    private List<Long> lineNumber;
}
