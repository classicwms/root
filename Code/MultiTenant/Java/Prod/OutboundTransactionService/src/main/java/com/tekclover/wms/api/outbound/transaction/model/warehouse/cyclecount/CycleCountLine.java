package com.tekclover.wms.api.outbound.transaction.model.warehouse.cyclecount;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "tblcyclecountline")
public class CycleCountLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cycleCountNo;
    private Long lineOfEachItemCode;
    private String itemCode;
    private String itemDescription;
    private String Uom;
    private String manufacturerCode;
    private String manufacturerName;
    private String orderId;                 //header reference Id(parent Id)
    private Double countedQty;
    private Double frozenQty;
    private String stockCountType;

    //MiddleWare Fields
    private Long middlewareId;
    private Long middlewareHeaderId;
    private String middlewareTable;

    private String isCompleted;
    private String isCancelled;
}