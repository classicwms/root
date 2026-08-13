package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.List;

@Data
public class SearchTransferInLine {

    private List<Long> transferInLineId;
    private List<String> transferOrderNo;
    private List<Long>  lineNoOfEachItem;
    private List<String> itemCode;
    private List<String> itemDescription;
    private List<Double> transferQty;
    private List<String> unitOfMeasure;
    private List<String> manufacturerCode;
    private List<String> manufacturerShortName;







}
