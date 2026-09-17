package com.tekclover.wms.core.model.Connector;

import lombok.Data;

@Data
public class UpdateStockCountLine {

    private String itemCode;
    private String cycleCountNo;
    private String manufacturerName;
    private Double InventoryQty;

}
