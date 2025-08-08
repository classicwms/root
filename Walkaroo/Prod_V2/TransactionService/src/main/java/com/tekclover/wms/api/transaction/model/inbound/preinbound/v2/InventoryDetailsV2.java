package com.tekclover.wms.api.transaction.model.inbound.preinbound.v2;

import lombok.Data;

@Data
public class InventoryDetailsV2 {

    private String storageBin;
    private Double inventoryQty;
    private Double accommodationQty;
}
