package com.tekclover.wms.api.transaction.model.warehouse.outbound.v2;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ReturnPOLineV2 {

    @Column(nullable = false)
    @NotNull(message = "Line Reference is mandatory")
    private Long lineReference;                                // IB_LINE_NO

    @Column(nullable = false)
    @NotBlank(message = "SKU is mandatory")
    private String sku;                                    // ITM_CODE

    private String skuDescription;                            // ITEM_TEXT

    @Column(nullable = false)
    @NotNull(message = "ReturnOrder Quantity is mandatory")
    private Double returnQty;                                // ORD_QTY

    @Column(nullable = false)
    @NotBlank(message = "UOM is mandatory")
    private String uom;                                        // ORD_UOM

    private String orderType;                                // REF_FIELD_1

//    @Column(nullable = false)
//    @NotBlank(message = "Manufacturer Code is mandatory")
    private String manufacturerCode;

//    @Column(nullable = false)
//    @NotBlank(message = "Manufacturer Name is mandatory")
    private String manufacturerName;

    private String storageSectionId;

    private String brand;

    private String origin;
    private String supplierName;
    private Double packQty;
    private String fromCompanyCode;
    private Double expectedQty;
    protected String storeID;
    private String sourceBranchCode;
    private String countryOfOrigin;
    private String isCompleted;
    private String isCancelled;
    private String returnOrderNo;

    //MiddleWare Fields
    private Long middlewareId;
    private Long middlewareHeaderId;
    private String middlewareTable;
    private String supplierInvoiceNo;

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