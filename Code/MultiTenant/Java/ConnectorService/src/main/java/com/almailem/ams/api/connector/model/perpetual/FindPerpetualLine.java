package com.almailem.ams.api.connector.model.perpetual;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindPerpetualLine {

    private List<String> cycleCountNo;
    private List<Long> lineNoOfEachItemCode;
    private List<String> itemCode;

    private List<String> unitOfMeasure;
    private List<String> manufacturerCode;
    private List<String> manufacturerName;
    private List<Double> frozenQty;


    private List<Long> perpetualLineId;


}
