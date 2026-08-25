package com.tekclover.wms.core.model.warehouse.cyclecount.perpetual;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PerpetualLineV1 {

    @NotBlank(message = "CycleCountNo is mandatory")
    private String cycleCountNo;

    @NotNull(message = "lineNoOfEach ItemCOde is mandatory")
    private Long lineNoOfEachItemCode;

    @NotBlank(message = "itemCode is mandatory")
    private String itemCode;

    private String itemDescription;

    @NotBlank(message = "UOM is mandatory")
    private String uom;

    @NotBlank(message = "Manufacturer Code is mandatory")
    private String manufacturerCode;

    @NotBlank(message = "Manufacturer Name is mandatory")
    private String manufacturerName;

    private Double frozenQty;
    private Double countedQty;

    private String isCompleted;
    private String isCancelled;
}