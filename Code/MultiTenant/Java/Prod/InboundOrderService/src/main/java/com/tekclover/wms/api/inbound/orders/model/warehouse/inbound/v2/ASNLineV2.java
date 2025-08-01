package com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class ASNLineV2 {

    @Column(nullable = false)
    @NotNull(message = "Line Reference is mandatory")
    private Long lineReference;

    @Column(nullable = false)
    @NotBlank(message = "sku is mandatory")
    private String sku;

    @Column(nullable = false)
//    @NotBlank(message = "sku description is mandatory")
    private String skuDescription;

    private String containerNumber;
    private String supplierCode;
    private String supplierPartNumber;
    //
//    @Column(nullable = false)
//    @NotBlank(message = "Manufacturer Name is mandatory")
    private String manufacturerName;

    //    @Column(nullable = false)
//    @NotBlank(message = "Manufacturer Code is mandatory")
    private String manufacturerCode;

    @Column(nullable = false)
    @NotBlank(message = "Expected Date is mandatory")
    private String expectedDate;

    //    @Column(nullable = false)
//    @NotNull(message = "expectedQty is mandatory")
    private Double expectedQty;

    //    @Column(nullable = false)
//    @NotBlank(message = "Uom is mandatory")
    private String uom;

    private Double packQty;
    private String origin;
    private String supplierName;
    private String brand;
    private String AMSSupplierInvoiceNo;

    //almailem fields
    private Date receivedDate;
    private Double receivedQty;
    private String receivedBy;
    private String isCompleted;
    private String isCancelled;

    private String companyCode;
    private String branchCode;

    private String manufacturerFullName;
    private String purchaseOrderNumber;
    private String supplierInvoiceNo;
    private String noPairs;

    //MiddleWare Fields
    private Long middlewareId;
    private Long middlewareHeaderId;
    private String middlewareTable;

    private Double noBags;
    private Double bagSize;

    /*------------------------Namratha Fields -------------------------------*/
    private String inwardDate;
    private Double expectedQtyInPieces;
    private Double expectedQtyInCases;
    private String unloadingIncharge;
    private Long totalUnLoaders;
    private String barcodeId;

    /*----------------Walkaroo changes------------------------------------------------------*/
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    /*----------------------Impex--------------------------------------------------*/
    private String alternateUom;
    private Double mrp;
    private String vehicleNo;
    private Date vehicleReportingDate;
    private Date vehicleUnloadingDate;
}
