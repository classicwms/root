package com.almailem.ams.api.connector.model.supplierinvoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "SUPPLIERINVOICELINE")
public class SupplierInvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Supplierinvoicelineid")
    private Long supplierInvoiceLineId;

    @Column(name = "Supplierinvoiceheaderid")
    private Long supplierInvoiceHeaderId;

    @NotBlank(message = "Company Code is mandatory")
    @Column(name = "Companycode", columnDefinition = "nvarchar(25)", nullable = false)
    private String companyCode;

    @NotBlank(message = "Branch Code is mandatory")
    @Column(name = "Branchcode", columnDefinition = "nvarchar(25)", nullable = false)
    private String branchCode;

    @NotBlank(message = "Supplier Invoice No is mandatory")
    @Column(name = "Supplierinvoiceno", columnDefinition = "nvarchar(50)", nullable = false)
    private String supplierInvoiceNo;

    @NotNull(message = "Line No for each item is mandatory")
    @Column(name = "Linenoforeachitem")
    private Long lineNoForEachItem;

    @NotBlank(message = "Item Code is mandatory")
    @Column(name = "Itemcode", columnDefinition = "nvarchar(50)", nullable = false)
    private String itemCode;

    @NotBlank(message = "Item Description is mandatory")
    @Column(name = "Itemdescription", columnDefinition = "nvarchar(500)", nullable = false)
    private String itemDescription;

    @Column(name = "Containerno", columnDefinition = "nvarchar(50)")
    private String containerNo;

    @NotBlank(message = "Supplier Code is mandatory")
    @Column(name = "Suppliercode", columnDefinition = "nvarchar(50)", nullable = false)
    private String supplierCode;

    @Column(name = "Supplierpartno", columnDefinition = "nvarchar(50)")
    private String supplierPartNo;

    @NotBlank(message = "Manufacturer Short Name is mandatory")
    @Column(name = "Manufacturershortname", columnDefinition = "nvarchar(200)", nullable = false)
    private String manufacturerShortName;

    @NotBlank(message = "Manufacturer Code is mandatory")
    @Column(name = "Manufacturercode", columnDefinition = "nvarchar(200)", nullable = false)
    private String manufacturerCode;

    @NotBlank(message = "Purchase Order No is mandatory")
    @Column(name = "Purchaseorderno", columnDefinition = "nvarchar(50)", nullable = false)
    private String purchaseOrderNo;

    @NotNull(message = "Invoice Date is mandatory")
    @Column(name = "Invoicedate")
    private Date invoiceDate;

    @NotNull(message = "Invoice Qty is mandatory")
    @Column(name = "Invoiceqty")
    private Double invoiceQty;

    @NotBlank(message = "Unit Of Measure is mandatory")
    @Column(name = "Unitofmeasure", columnDefinition = "nvarchar(50)", nullable = false)
    private String unitOfMeasure;

    @Column(name = "Suppliername", columnDefinition = "nvarchar(250)")
    private String supplierName;

    @Column(name = "Manufacturerfullname", columnDefinition = "nvarchar(250)")
    private String manufacturerFullName;

    @Column(name = "Receiveddate")
    private Date receivedDate;

    @Column(name = "Receivedqty")
    private Double receivedQty;

    @Column(name = "Receivedby", columnDefinition = "nvarchar(50)")
    private String receivedBy;

    @Column(name = "Is_completed", columnDefinition = "nvarchar(10)")
    private String isCompleted;

    @Column(name = "Is_cancelled", columnDefinition = "nvarchar(10)")
    private String isCancelled;
}