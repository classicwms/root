package com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.v2;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class StockReceiptLine {

    private String companyCode;

    @Column(nullable = false)
    @NotBlank(message = "Branch Code is mandatory")
    private String branchCode;

    @Column(nullable = false)
    @NotBlank(message = "Receipt Number is mandatory")
    private String receiptNo;

    @Column(nullable = false)
    @NotNull(message = "Line Number for Each Item is mandatory")
    private Long lineNoForEachItem;

    @Column(nullable = false)
    @NotBlank(message = "Item Code is mandatory")
    private String itemCode;

    @Column(nullable = false)
    @NotBlank(message = "Item Description is mandatory")
    private String itemDescription;

    private String supplierCode;

    private String supplierPartNo;

    @Column(nullable = false)
    @NotBlank(message = "Manufacturer Short Name is mandatory")
    private String manufacturerShortName;

    @Column(nullable = false)
    @NotBlank(message = "Manufacturer Code is mandatory")
    private String manufacturerCode;

    @Column(nullable = false)
    @NotBlank(message = "Receipt Date is mandatory")
    private Date receiptDate;

    @Column(nullable = false)
    @NotNull(message = "Receipt Quantity is mandatory")
    private Double receiptQty;

    @Column(nullable = false)
    @NotBlank(message = "UOM is mandatory")
    private String unitOfMeasure;

    private String supplierName;

    private String manufacturerFullName;
    private String isCompleted;
    //MiddleWare Fields
    private Long middlewareId;
    private Long middlewareHeaderId;
    private String middlewareTable;

}