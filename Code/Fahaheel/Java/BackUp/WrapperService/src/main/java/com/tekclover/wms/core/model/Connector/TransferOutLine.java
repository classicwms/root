package com.tekclover.wms.core.model.Connector;

import lombok.Data;

@Data
public class TransferOutLine {

    private Long transferOutLineId;

    private Long transferOutHeaderId;

    private String transferOrderNumber;

    private Long lineNumberOfEachItem;

    private String itemCode;

    private String itemDescription;

    private Double transferOrderQty;

    private String unitOfMeasure;

    private String manufacturerCode;

    private String manufacturerShortName;

    private String manufacturerFullName;

    private String isCompleted;
}
