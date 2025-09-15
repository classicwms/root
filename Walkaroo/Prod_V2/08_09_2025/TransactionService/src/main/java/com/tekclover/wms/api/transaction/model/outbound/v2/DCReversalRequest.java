package com.tekclover.wms.api.transaction.model.outbound.v2;

import lombok.Data;

@Data
public class DCReversalRequest {
	private String plantId;
	private String warehouseId;
    private String refDocNo;
    private String itemCode;
    private String barCodeId;
}