package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindSalesReturnLine {

    private List<Long> salesReturnLineId;

    private List<Long> lineNoOfEachItem;
    private List<String> itemCode;
    private List<String> itemDescription;
    private List<String> referenceInvoiceNo;

    private List<String> manufacturerCode;

    private List<String> manufacturerShortName;
    private Date fromReturnOrderDate;
    private Date toReturnOrderDate;
    private List<Double> returnQty;
    private List<String> unitOfMeasure;





}
