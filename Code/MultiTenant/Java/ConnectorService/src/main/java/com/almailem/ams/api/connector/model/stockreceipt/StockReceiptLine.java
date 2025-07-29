package com.almailem.ams.api.connector.model.stockreceipt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "STOCKRECEIPTLINE")
public class StockReceiptLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Stockreceiptlineid")
    private Long stockReceiptLineId;

    @Column(name = "Stockreceiptheaderid")
    private Long stockReceiptHeaderId;

    @NotBlank(message = "Company Code is mandatory")
    @Column(name = "Companycode", columnDefinition = "nvarchar(25)", nullable = false)
    private String companyCode;

    @NotBlank(message = "Branch Code is mandatory")
    @Column(name = "Branchcode", columnDefinition = "nvarchar(25)", nullable = false)
    private String branchCode;

    @NotBlank(message = "Receipt Number is mandatory")
    @Column(name = "Receiptno", columnDefinition = "nvarchar(50)", nullable = false)
    private String receiptNo;

    @NotNull(message = "Line No for Each Item is mandatory")
    @Column(name = "Linenoforeachitem")
    private Long lineNoForEachItem;

    @NotBlank(message = "Item Code is mandatory")
    @Column(name = "Itemcode", columnDefinition = "nvarchar(50)", nullable = false)
    private String itemCode;

    @NotBlank(message = "Item Description is mandatory")
    @Column(name = "Itemdescription", columnDefinition = "nvarchar(500)", nullable = false)
    private String itemDescription;

    @Column(name = "Suppliercode", columnDefinition = "nvarchar(50)")
    private String supplierCode;

    @Column(name = "Supplierpartno", columnDefinition = "nvarchar(50)")
    private String supplierPartNo;

    @NotBlank(message = "Manufacturer Short Name is mandatory")
    @Column(name = "Manufacturershortname", columnDefinition = "nvarchar(200)", nullable = false)
    private String manufacturerShortName;

    @NotBlank(message = "Manufacturer Code is mandatory")
    @Column(name = "Manufacturercode", columnDefinition = "nvarchar(200)", nullable = false)
    private String manufacturerCode;

    @NotNull(message = "Receipt Date is mandatory")
    @Column(name = "Receiptdate")
    private Date receiptDate;

    @NotNull(message = "Receipt Quantity is mandatory")
    @Column(name = "Receiptqty")
    private Double receiptQty;

    @NotBlank(message = "UOM is mandatory")
    @Column(name = "Unitofmeasure", columnDefinition = "nvarchar(50)", nullable = false)
    private String unitOfMeasure;

    @Column(name = "Suppliername", columnDefinition = "nvarchar(250)")
    private String supplierName;

    @Column(name = "Manufacturerfullname", columnDefinition = "nvarchar(250)")
    private String manufacturerFullName;

    @Column(name = "Is_completed", columnDefinition = "nvarchar(10)")
    private String isCompleted;

}