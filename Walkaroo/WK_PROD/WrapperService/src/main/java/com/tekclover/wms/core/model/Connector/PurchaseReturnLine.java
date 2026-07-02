package com.tekclover.wms.core.model.Connector;

import lombok.Data;

@Data
public class PurchaseReturnLine {

    private Long purchaseReturnLineId;

    private Long purchaseReturnHeaderId;

    private String returnOrderNo;

    private Long lineNoOfEachItemCode;

    private String itemCode;

    private String itemDescription;

    private Double returnOrderQty;

    private String unitOfMeasure;

    private String manufacturerCode;

    private String manufacturerShortName;

    private String manufacturerFullName;

    private String isCompleted;

    private String isCancelled;

    private String supplierInvoiceNo;

}
