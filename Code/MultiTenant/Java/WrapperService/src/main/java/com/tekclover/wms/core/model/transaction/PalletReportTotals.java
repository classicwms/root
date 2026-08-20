package com.tekclover.wms.core.model.transaction;


import lombok.Data;

@Data
public class PalletReportTotals {

    private Double totalQty;
    private Double bestFit;
    private Double difference;
    private Double inventoryQty;
    private Double value1;
    private Double value2;
    private Double value3;
    private Double value4;

}
