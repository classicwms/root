package com.tekclover.wms.core.model.warehouse.inbound.almailem;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class InterWarehouseTransferInLineV2 {

    @NotBlank(message = "From Company Code is Mandatory")
    private String fromCompanyCode;

    //	@NotBlank(message = "Origin is mandatory")
    private String origin;

    private String supplierName;

//    @NotBlank(message = "Manufacturer Code is mandatory")
    private String manufacturerCode;

    private String Brand;

    @NotBlank(message = "From Branch Code is mandatory")
    private String fromBranchCode;

    @NotNull(message = "Line Reference is mandatory")
    private Long lineReference;

    @NotBlank(message = "sku is mandatory")
    private String sku;

    @NotBlank(message = "sku Description is mandatory")
    private String skuDescription;

    private String supplierPartNumber;

//    @NotBlank(message = "Manufacturer Name is mandatory")
    private String manufacturerName;

    @NotBlank(message = "Excepted Date is mandatory")
    private String expectedDate;

    @NotNull(message = "Excepted Qty is mandatory")
    private Double expectedQty;

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
    /*----------------------Impex--------------------------------------------------*/
    private String alternateUom;
    private Double noBags;
    private Double bagSize;
    private Double mrp;

}