package com.tekclover.wms.core.model.Connector;

import lombok.Data;

@Data
public class PickListLine {

    private Long pickListLineId;

    private Long pickListHeaderId;

    private String salesOrderNo;

    private String pickListNo;

    private String itemCode;

    private String itemDescription;

    private Double pickListQty;

    private String unitOfMeasure;

    private String manufacturerCode;

    private String manufacturerShortName;

    private String manufacturerFullName;

    private Double pickedQty;

    private String isCompleted;

    private String isCancelled;

    private String isAllPicked;

    private Long lineNumberOfEachItem;
}
