package com.almailem.ams.api.connector.model.stockadjustment;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindStockAdjustment {

    private List<Long> stockAdjustmentId;
    private List<String> companyCode;
    private List<String> branchCode;
    private List<String> itemCode;
    private List<String> unitOfMeasure;
    private List<String> manufacturerCode;

    private Date fromOrderReceivedOn;

    private Date toOrderReceivedOn;

    private Date fromOrderProcessedOn;

    private Date toOrderProcessedOn;

    private List<Long> processedStatusId;



}
