package com.almailem.ams.api.connector.model.wms;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
public class StockReceiptLine {

    private String companyCode;

    @Column(nullable = false)
    private String branchCode;

    @Column(nullable = false)
    private String receiptNo;

    @Column(nullable = false)
    private Long lineNoForEachItem;

    @Column(nullable = false)
    private String itemCode;

    @Column(nullable = false)
    private String itemDescription;

    private String supplierCode;
    private String supplierPartNo;

    @Column(nullable = false)
    private String manufacturerShortName;

    @Column(nullable = false)
    private String manufacturerCode;

    @Column(nullable = false)
    private Date receiptDate;

    @Column(nullable = false)
    private Double receiptQty;

    @Column(nullable = false)
    private String unitOfMeasure;

    private String supplierName;
    private String manufacturerFullName;
    private String isCompleted;

    //MiddleWare Fields
    private Long middlewareId;
    private Long middlewareHeaderId;
    private String middlewareTable;
}