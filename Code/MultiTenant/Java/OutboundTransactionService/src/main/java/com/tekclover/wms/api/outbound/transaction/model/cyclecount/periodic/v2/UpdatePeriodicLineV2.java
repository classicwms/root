package com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.v2;


import lombok.Data;

@Data
public class UpdatePeriodicLineV2 {

    private String itemCode;
    private String cycleCountNo;
    private String manufacturerName;
    private Double InventoryQty;
    private Long lineNo;
}
