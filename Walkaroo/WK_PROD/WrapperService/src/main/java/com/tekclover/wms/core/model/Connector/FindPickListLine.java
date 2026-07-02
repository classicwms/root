package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.List;

@Data
public class FindPickListLine {
    private List<Long> pickListLineId;
    private List<String> salesOrderNo;
    private List<String> pickListNo;
    private List<String> itemCode;
    private List<String> itemDescription;
    private List<Double> pickListQty;
    private List<String> unitOfMeasure;
    private List<String> manufacturerCode;
    private List<String> manufacturerShortName;

    private List<Long> lineNumberOfEachItem;


}
