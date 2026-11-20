package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchStockReceiptLine {

    private List<Long> stockReceiptLineId;

    private List<String> companyCode;
    private List<String> branchCode;
    private List<String> receiptNo;
    private List<Long> lineNoForEachItem;
    private List<String> itemCode;
    private List<String> itemDescription;
    private List<String> manufacturerShortName;
    private List<String> manufacturerCode;
    private Date receiptDate;

    private List<String> unitOfMeasure;


}
