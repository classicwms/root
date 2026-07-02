package com.tekclover.wms.api.transaction.model.warehouse.inbound.v2;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class InterWarehouseTransferInLineV2 {

    @Column(nullable = false)
    @NotBlank(message = "From Company Code is Mandatory")
    private String fromCompanyCode;

    //	@NotBlank(message = "Origin is mandatory")
    private String origin;

    private String supplierName;

//    @Column(nullable = false)
//    @NotBlank(message = "Manufacturer Code is mandatory")
    private String manufacturerCode;

    private String Brand;

    @Column(nullable = false)
    @NotBlank(message = "From Branch Code is mandatory")
    private String fromBranchCode;

    @Column(nullable = false)
    @NotNull(message = "Line Reference is mandatory")
    private Long lineReference;

    @Column(nullable = false)
    @NotBlank(message = "sku is mandatory")
    private String sku;

    @Column(nullable = false)
    @NotBlank(message = "sku Description is mandatory")
    private String skuDescription;

    private String supplierPartNumber;

//    @Column(nullable = false)
//    @NotBlank(message = "Manufacturer Name is mandatory")
    private String manufacturerName;

    private String storageSectionId;

    @Column(nullable = false)
    @NotBlank(message = "Excepted Date is mandatory")
    private String expectedDate;

    @Column(nullable = false)
    @NotNull(message = "Excepted Qty is mandatory")
    private Double expectedQty;

    @Column(nullable = false)
    @NotBlank(message = "Uom is mandatory")
    private String uom;

    private Double packQty;
    private String orderType;
    private String manufacturerFullName;
    private String isCompleted;
    private String transferOrderNo;

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