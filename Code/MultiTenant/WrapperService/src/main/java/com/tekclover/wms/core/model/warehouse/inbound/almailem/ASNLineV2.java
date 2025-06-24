package com.tekclover.wms.core.model.warehouse.inbound.almailem;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class ASNLineV2 {

    @NotNull(message = "Line Reference is mandatory")
    private Long lineReference;

    @NotBlank(message = "sku is mandatory")
    private String sku;

//    @NotBlank(message = "sku description is mandatory")
    private String skuDescription;

    private String containerNumber;
    private String supplierCode;
    private String supplierPartNumber;

//    @NotBlank(message = "Manufacturer Name is mandatory")
    private String manufacturerName;

//    @NotBlank(message = "Manufacturer Code is mandatory")
    private String manufacturerCode;

    @NotBlank(message = "Expected Date is mandatory")
    private String expectedDate;

//    @NotNull(message = "expectedQty is mandatory")
    private Double expectedQty;

//    @NotBlank(message = "Uom is mandatory")
    private String uom;

    private Double packQty;
    private String origin;
    private String supplierName;
    private String brand;

    //almailem fields
    private Date receivedDate;
    private Double receivedQty;
    private String receivedBy;
    private String isCompleted;
    private String isCancelled;

    private String companyCode;
    private String branchCode;
    private String noPairs;

    private String manufacturerFullName;
    private String purchaseOrderNumber;
    private String supplierInvoiceNo;
    private Long inboundOrderTypeId;
    private String batchSerialNumber;

    //MiddleWare Fields
    private Long middlewareId;
    private Long middlewareHeaderId;
    private String middlewareTable;

    /*----------------Walkaroo changes------------------------------------------------------*/
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String barcodeId;
    /*----------------------Impex--------------------------------------------------*/
    private String alternateUom;
    private Double noBags;
    private Double bagSize;
    private Double mrp;

    /*------------------------Namratha Fields -------------------------------*/
    private String inwardDate;
    private Double expectedQtyInPieces;
    private Double expectedQtyInCases;
    private String unloadingIncharge;
    private Long totalUnLoaders;
    /*------------------------REEFERON FIELDS -------------------------------*/

    private String vehicleNo;
    private Date vehicleReportingDate;
    private Date vehicleUnloadingDate;
    private String customerId;
    private String customerName;

}
