package com.almailem.ams.api.connector.model.salesreturn;

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
@Table(name = "SALESRETURNLINE")
public class SalesReturnLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Salesreturnlineid")
    private Long salesReturnLineId;

    @Column(name = "Salesreturnheaderid")
    private Long salesReturnHeaderId;

    @NotNull(message = "Line No of Each Item is mandatory")
    @Column(name = "Linenoofeachitem")
    private Long lineNoOfEachItem;

    @NotBlank(message = "Item Code is mandatory")
    @Column(name = "Itemcode", columnDefinition = "nvarchar(50)", nullable = false)
    private String itemCode;

    @NotBlank(message = "Item Description is mandatory")
    @Column(name = "Itemdescription", columnDefinition = "nvarchar(500)", nullable = false)
    private String itemDescription;

    @NotBlank(message = "Reference Invoice No is mandatory")
    @Column(name = "Referenceinvoiceno", columnDefinition = "nvarchar(50)", nullable = false)
    private String referenceInvoiceNo;

    @Column(name = "Sourcebranchcode", columnDefinition = "nvarchar(50)")
    private String sourceBranchCode;

    @Column(name = "Supplierpartno", columnDefinition = "nvarchar(50)")
    private String supplierPartNo;

    @NotBlank(message = "Manufacturer Short Name is mandatory")
    @Column(name = "Manufacturershortname", columnDefinition = "nvarchar(200)", nullable = false)
    private String manufacturerShortName;

    @NotNull(message = "Return Order Date is mandatory")
    @Column(name = "Returnorderdate")
    private Date returnOrderDate;

    @NotNull(message = "Return Qty is mandatory")
    @Column(name = "Returnqty")
    private Double returnQty;

    @NotBlank(message = "UOM is mandatory")
    @Column(name = "Unitofmeasure", columnDefinition = "nvarchar(50)", nullable = false)
    private String unitOfMeasure;

    @Column(name = "Noofpacks")
    private Long noOfPacks;

    @Column(name = "Countryoforigin", columnDefinition = "nvarchar(250)")
    private String countryOfOrigin;

    @NotBlank(message = "Manufacturer Code is mandatory")
    @Column(name = "Manufacturercode", columnDefinition = "nvarchar(200)", nullable = false)
    private String manufacturerCode;

    @Column(name = "Manufacturerfullname", columnDefinition = "nvarchar(250)")
    private String manufacturerFullName;

    @Column(name = "Is_completed", columnDefinition = "nvarchar(10)")
    private String isCompleted;

    @Column(name = "Is_cancelled", columnDefinition = "nvarchar(10)")
    private String isCancelled;
}