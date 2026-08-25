package com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.v2;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

@Data
public class SalesInvoice {

    @Column(nullable = false)
    @NotBlank(message = "Company Code is mandatory")
    private String companyCode;

    @Column(nullable = false)
    @NotBlank(message = "Sales Invoice Number is mandatory")
    private String salesInvoiceNumber;

    @Column(nullable = false)
    @NotBlank(message = "Branch Code is mandatory")
    private String branchCode;

    @Column(nullable = false)
    @NotBlank(message = "Sales Order Number is mandatory")
    private String salesOrderNumber;

    @Column(nullable = false)
    @NotBlank(message = "Pick List Number is mandatory")
    private String pickListNumber;

    @Column(nullable = false)
    @NotBlank(message = "Invoice Date is mandatory")
    private String invoiceDate;

    private String deliveryType;
    private String customerId;
    private String customerName;
    private String address;
    private String phoneNumber;
    private String alternateNo;
    private String status;

    private String warehouseID;

    private String languageId;
    private Long middlewareId;
    private String middlewareTable;
}
