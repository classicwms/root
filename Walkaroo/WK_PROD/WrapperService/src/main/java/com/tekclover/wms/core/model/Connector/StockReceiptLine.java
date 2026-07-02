package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;

@Data
public class StockReceiptLine {

    private Long stockReceiptLineId;

    private Long stockReceiptHeaderId;

    private String companyCode;

    private String branchCode;

    private String receiptNo;

    private Long lineNoForEachItem;

    private String itemCode;

    private String itemDescription;

    private String supplierCode;

    private String supplierPartNo;

    private String manufacturerShortName;

    private String manufacturerCode;

    private Date receiptDate;

    private Double receiptQty;

    private String unitOfMeasure;

    private String supplierName;

    private String manufacturerFullName;

    private String isCompleted;

}