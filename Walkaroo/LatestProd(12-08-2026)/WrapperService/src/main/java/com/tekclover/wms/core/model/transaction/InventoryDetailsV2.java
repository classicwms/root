package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class InventoryDetailsV2 {

    private String storageBin;
    private Double inventoryQty;
    private Double accommodationQty;
}
