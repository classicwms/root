package com.tekclover.wms.core.model.warehouse.outbound.almailem;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SOLineV2 {

    @NotNull(message = "Line Reference is mandatory")
    private Long lineReference;                               // IB_LINE_NO

    @NotBlank(message = "SKU is mandatory")
    private String sku;                                       // ITM_CODE

    private String skuDescription;                            // ITEM_TEXT

    @NotNull(message = "Ordered Quantity is mandatory")
    private Double orderedQty;                                // ORD_QTY

    @NotBlank(message = "Unit Of Measure is mandatory")
    private String uom;                                       // ORD_UOM

    @NotBlank(message = "Order type is mandatory")
    private String orderType;                                 // REF_FIELD_1

//    @NotBlank(message = "Manufacturer Code is mandatory")
    private String manufacturerCode;

//    @NotBlank(message = "Manufacturer Name is mandatory")
    private String manufacturerName;

    private String brand;

    private String origin;
    private String supplierName;
    private Double packQty;
    private String fromCompanyCode;
    private Double expectedQty;
    protected String storeID;
    private String sourceBranchCode;
    private String countryOfOrigin;
    private String transferOrderNumber;

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
