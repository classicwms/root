package com.tekclover.wms.core.model.Connector;

import lombok.Data;

@Data
public class TransferInLine {

    private Long transferInLineId;

    private Long transferInHeaderId;

    private String transferOrderNo;

    private Long lineNoOfEachItem;

    private String itemCode;

    private String itemDescription;

    private Double transferQty;

    private String unitOfMeasure;

    private String manufacturerCode;

    private String manufacturerShortName;

    private String manufacturerFullName;

    private String isCompleted;
}