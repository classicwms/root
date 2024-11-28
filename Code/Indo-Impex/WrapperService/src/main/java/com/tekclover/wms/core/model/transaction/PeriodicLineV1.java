package com.tekclover.wms.core.model.transaction;


import lombok.Data;

@Data
public class PeriodicLineV1 {

    private String cycleCountNo;

    private Long lineNoOfEachItemCode;

    private String itemCode;

    private String itemDescription;

    private String Uom;

    private String manufacturerCode;

    private String manufacturerName;

    private Double frozenQty;

    private Double countedQty;
    private String isCompleted;
    private String isCancelled;

    private String warehouseId;
    private String languageId;
    private String companyCode;
    private String branchCode;

}