package com.tekclover.wms.core.model.warehouse.cyclecount;

import lombok.Data;

import javax.persistence.*;

@Data
public class CycleCountLine {

    private Long id;

    private String cycleCountNo;
    private Long lineOfEachItemCode;
    private String itemCode;
    private String itemDescription;
    private String Uom;
    private String manufacturerCode;
    private String manufacturerName;
    private String orderId;

    private Double countedQty;
    private Double frozenQty;
    private String stockCountType;

    //MiddleWare Fields

    private String isCompleted;
    private String isCancelled;
}