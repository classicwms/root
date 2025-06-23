package com.almailem.ams.api.connector.model.purchasereturn;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindPurchaseReturnLine {

    private List<Long> purchaseReturnLineId;
    private List<String> returnOrderNo;
    private List<Long> lineNoOfEachItemCode;
    private List<String> itemCode;

    private List<Double> returnOrderQty;
    private List<String> unitOfMeasure;
    private List<String> manufacturerCode;
    private List<String> manufacturerShortName;



}
