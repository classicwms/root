package com.tekclover.wms.api.outbound.transaction.model.warehouse.cyclecount.perpetual;


import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PerpetualLineV1 {

    @Column(nullable = false)
    @NotBlank(message = "CycleCountNo is mandatory")
    private String cycleCountNo;

    @Column(nullable = false)
    @NotNull(message = "lineNoOfEach ItemCode is mandatory")
    private Long lineNoOfEachItemCode;

    @Column(nullable = false)
    @NotBlank(message = "itemCode is mandatory")
    private String itemCode;

    private String itemDescription;

    @Column(nullable = false)
    @NotBlank(message = "UOM is mandatory")
    private String uom;

    @Column(nullable = false)
    @NotBlank(message = "Manufacturer Code is mandatory")
    private String manufacturerCode;

    @Column(nullable = false)
    @NotBlank(message = "Manufacturer Name is mandatory")
    private String manufacturerName;

    private Double frozenQty;
    private Double countedQty;

    private String isCompleted;
    private String isCancelled;

    //MiddleWare Fields
    private Long middlewareId;
    private Long middlewareHeaderId;
    private String middlewareTable;
}
