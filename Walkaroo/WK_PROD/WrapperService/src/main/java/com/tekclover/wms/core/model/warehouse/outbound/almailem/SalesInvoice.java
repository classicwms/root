package com.tekclover.wms.core.model.warehouse.outbound.almailem;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SalesInvoice {

    @NotBlank(message = "Company Code is mandatory")
    private String companyCode;

    @NotBlank(message = "Sales Invoice Number is mandatory")
    private String salesInvoiceNumber;

    @NotBlank(message = "Branch Code is mandatory")
    private String branchCode;

    @NotBlank(message = "Sales Order Number is mandatory")
    private String salesOrderNumber;

    @NotBlank(message = "Pick List Number is mandatory")
    private String pickListNumber;

    @NotBlank(message = "Invoice Date is mandatory")
    private String invoiceDate;
}
