package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindTransferOutLine {

    private List<Long> transferOutLineId;

    private List<String> transferOrderNumber;

    private List<Long> lineNumberOfEachItem;

    private List<String> itemCode;

    private List<String> itemDescription;

    private List<Double> transferOrderQty;

    private List<String> unitOfMeasure;

    private List<String> manufacturerCode;

    private List<String> manufacturerShortName;




}
