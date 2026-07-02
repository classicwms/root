package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class DCReversalRequest {
	private String plantId;
	private String warehouseId;
    private String refDocNo;
    private String itemCode;
    private String barCodeId;
}