package com.tekclover.wms.api.inbound.transaction.model.dto;

import lombok.Data;

@Data
public class PostSAP {

    String companyCodeId;
    String plantId;
    String warehouseId;
    String refDocNumber;

}
