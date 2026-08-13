package com.tekclover.wms.api.transaction.model.warehouse.inbound.v2;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class B2bTransferInLine {

    @Column(nullable = false)
    @NotNull(message = "Line Reference is mandatory")
    private Long lineReference;

    @Column(nullable = false)
    @NotBlank(message = "sku is mandatory")
    private String sku;

    @Column(nullable = false)
    @NotBlank(message = "sku Description is mandatory")
    private String skuDescription;

    @Column(nullable = false)
    @NotBlank(message = "Store Id is mandatory")
    private String storeID;

    private String supplierPartNumber;
    private String manufacturerName;
    private String manufacturerFullName;

    private String storageSectionId;

    @Column(nullable = false)
    @NotBlank(message = "Excepted Date is mandatory")
    private String expectedDate;

    @Column(nullable = false)
    @NotNull(message = "Expected Qty is mandatory")
    private Double expectedQty;

    @Column(nullable = false)
    @NotBlank(message = "uom is mandatory")
    private String uom;

    private String origin;

    @Column(nullable = false)
    @NotBlank(message = "Manufacturer Code is mandatory")
    private String manufacturerCode;

    private Long packQty;
    private String brand;
    private String supplierName;
    private String orderType;
    private String transferOrderNo;
    private String isCompleted;

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
    private String noPairs;
    private String barcodeId;
}
