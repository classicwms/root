package com.tekclover.wms.core.model.warehouse.cyclecount.periodic;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PeriodicLineV1 {

    @NotBlank(message = "CycleCountNo is mandatory")
    private String cycleCountNo;

    @NotNull(message = "LineNoOfEachItemCode is mandatory")
    private Long lineNoOfEachItemCode;

    @NotBlank(message = "ItemCode is mandatory")
    private String itemCode;

    private String itemDescription;

    @NotBlank(message = "UOM is mandatory")
    private String Uom;

//    @NotBlank(message = "Manufacturer Code is mandatory")
    private String manufacturerCode;

//    @NotBlank(message = "Manufacturer Name is mandatory")
    private String manufacturerName;

    @NotNull(message = "FrozenQty is mandatory")
    private Double frozenQty;

    private Double countedQty;
    private String isCompleted;
    private String isCancelled;

}