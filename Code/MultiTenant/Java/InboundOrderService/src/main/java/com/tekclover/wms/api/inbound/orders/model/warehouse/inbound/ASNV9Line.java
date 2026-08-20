package com.tekclover.wms.api.inbound.orders.model.warehouse.inbound;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class ASNV9Line {

    @NotBlank(message = "Line Reference is mandatory")
    private String lineReference;                            // IB_LINE_NO

    @NotBlank(message = "SKU is mandatory")
    private String sku;                                // ITM_CODE

    @NotBlank(message = "SKU Description is mandatory")
    private String skuText;                            // ITEM_TEXT

    private String invoiceNumber;                            // INV_NO
    private String containerNumber;                        // CONT_NO
    private String supplierCode;                            // PARTNER_CODE
    private String supplierPartNumber;                        // PARTNER_ITM_CODE
    private String manufacturerName;                        // BRND_NM
    private String manufacturerPartNo;                        // MFR_PART

    //    @NotBlank(message = "Expected Date is mandatory")
    private String expectedDate;                            // EA_DATE

    //    @NotNull(message = "Expected Quantity is mandatory")
    private Double expectedQty;                                // ORD_QTY

    @NotBlank(message = "UOM is mandatory")
    private String uom;                                        // ORD_UOM

    //    @NotNull(message = "Pack Quantity is mandatory")
    private Double packQty;

    private String barcodeId;
    private String companyCode;
    private String branchCode;
    private String languageId;
    private String warehouseId;
    //MiddleWare Fields
    private Long middlewareId;
    private Long middlewareHeaderId;
    private String middlewareTable;

    //almailem fields
    private Date receivedDate;
    private Double receivedQty;
    private String receivedBy;
    private String isCompleted;
    private String isCancelled;
    //BF
    private String inbound;
    private String qty;
    private String customerId;
    private String customerName;
    private String vehicleNo;
    private Date vehicleReportingDate;
    private Date vehicleUnloadingDate;


    private String manufacturerCode;
    private String manufacturerFullName;

    private String netWeight;
    private String grossWeight;
    private String totalWeight;
    private Double mrp;
}
