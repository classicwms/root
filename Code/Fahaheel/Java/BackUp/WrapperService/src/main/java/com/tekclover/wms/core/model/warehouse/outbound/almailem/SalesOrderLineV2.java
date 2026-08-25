package com.tekclover.wms.core.model.warehouse.outbound.almailem;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SalesOrderLineV2 {

    //    @Column(nullable = false)
//    @NotNull(message = "Line Reference is mandatory")
    private Long lineReference;                                // IB_LINE_NO

    //    @Column(nullable = false)
//    @NotBlank(message = "SKU is mandatory")
    private String sku;                                    // ITM_CODE

    private String skuDescription;                            // ITEM_TEXT

    //    @Column(nullable = false)
//    @NotNull(message = "Ordered Quantity is mandatory")
    private Double orderedQty;                                // ORD_QTY

    //    @Column(nullable = false)
//    @NotBlank(message = "Unit of Measure is mandatory")
    private String uom;                                        // ORD_UOM

    //    @Column(nullable = false)
//    @NotBlank(message = "Manufacturer Code is mandatory")
    private String manufacturerCode;

    //    @Column(nullable = false)
//    @NotBlank(message = "Manufacturer Name is mandatory")
    private String manufacturerName;

    private String brand;
    private String storageSectionId;
    private String salesOrderNo;
    private String pickListNo;

    private String orderType;                                // REF_FIELD_1
    private String origin;
    private String supplierName;
    private Double packQty;
    private String fromCompanyCode;
    private Double expectedQty;
    protected String storeID;
    private String sourceBranchCode;
    private String countryOfOrigin;
    private String manufacturerFullName;
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

    /*----------------Namratha------------------------*/
    private Double expectedQtyInCases;
    private Double expectedQtyInPieces;

    /*------------REEFERON--------------------*/
    private String shipToCode;
    private String shipToParty;
    private String specialStock;
    private String mtoNumber;
    private Double qtyInCase;
    private Double qtyInPiece;
}
