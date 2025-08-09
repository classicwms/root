package com.tekclover.wms.core.model.Connector;

import lombok.Data;

@Data
public class PerpetualLine {

    private Long perpetualLineId;

    private Long perpetualHeaderId;

    private String cycleCountNo;

    private Long lineNoOfEachItemCode;

    private String itemCode;

    private String itemDescription;

    private String unitOfMeasure;

    private String manufacturerCode;

    private String manufacturerName;

    private Double frozenQty;

    private String isCompleted;

    private String isCancelled;

    private Double countedQty;
}
