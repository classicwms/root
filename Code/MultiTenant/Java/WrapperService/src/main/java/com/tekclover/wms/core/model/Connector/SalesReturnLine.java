package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;

@Data
public class SalesReturnLine {

    private Long salesReturnLineId;

    private Long salesReturnHeaderId;

    private Long lineNoOfEachItem;

    private String itemCode;

    private String itemDescription;

    private String referenceInvoiceNo;

    private String sourceBranchCode;

    private String supplierPartNo;

    private String manufacturerShortName;

    private Date returnOrderDate;

    private Double returnQty;

    private String unitOfMeasure;

    private Long noOfPacks;

    private String countryOfOrigin;

    private String manufacturerCode;

    private String manufacturerFullName;

    private String isCompleted;

    private String isCancelled;
}