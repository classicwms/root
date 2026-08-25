package com.tekclover.wms.core.model.warehouse.inbound.almailem;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class StockReceiptLine {

    private String companyCode;

    @NotBlank(message = "Branch Code is mandatory")
    private String branchCode;

    @NotBlank(message = "Receipt Number is mandatory")
    private String receiptNo;

    @NotNull(message = "Line Number for Each Item is mandatory")
    private Long lineNoForEachItem;

    @NotBlank(message = "Item Code is mandatory")
    private String itemCode;

    @NotBlank(message = "Item Description is mandatory")
    private String itemDescription;

    private String supplierCode;

    private String supplierPartNo;

    @NotBlank(message = "Manufacturer Short Name is mandatory")
    private String manufacturerShortName;

    @NotBlank(message = "Manufacturer Code is mandatory")
    private String manufacturerCode;

    @NotBlank(message = "Receipt Date is mandatory")
    private Date receiptDate;

    @NotNull(message = "Receipt Quantity is mandatory")
    private Double receiptQty;

    @NotBlank(message = "UOM is mandatory")
    private String unitOfMeasure;

    private String supplierName;

    private String manufacturerFullName;

}