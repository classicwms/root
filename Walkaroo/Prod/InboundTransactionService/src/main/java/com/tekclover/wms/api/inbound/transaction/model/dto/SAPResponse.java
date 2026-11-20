package com.tekclover.wms.api.inbound.transaction.model.dto;

import lombok.Data;

@Data
public class SAPResponse {

    String refDocNumber;
    String response;
    String warehouseId;

}
