package com.tekclover.wms.api.outbound.transaction.model.outbound;

import lombok.Data;


@Data
public class PickupNewUpdate {

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private String itemCode;

    private String preOutboundNo;

    private String refDocNumber;

    private Double orderQty;

    private Long lineNumber;

}
