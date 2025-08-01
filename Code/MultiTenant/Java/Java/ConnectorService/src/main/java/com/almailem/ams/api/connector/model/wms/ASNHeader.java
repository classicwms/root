package com.almailem.ams.api.connector.model.wms;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class ASNHeader {

    @Column(nullable = false)
    @NotBlank(message = "Branch Code is mandatory")
    private String branchCode;

    @Column(nullable = false)
    @NotBlank(message = "Company Code is mandatory")
    private String companyCode;

    @Column(nullable = false)
    @NotBlank(message = "ASN number is mandatory")
    private String asnNumber;

    private String AMSSupplierInvoiceNo;

    //almailem fields
//	private String purchaseOrderNumber;

    private String isCompleted;
    private Date updatedOn;
    private String isCancelled;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
}