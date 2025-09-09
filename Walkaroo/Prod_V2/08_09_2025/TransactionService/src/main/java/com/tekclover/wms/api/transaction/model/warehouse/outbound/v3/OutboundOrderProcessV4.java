package com.tekclover.wms.api.transaction.model.warehouse.outbound.v3;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tblouboundprocess")
@Data
public class OutboundOrderProcessV4 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OUTBOUND_ORDER_ID")
    private Long outboundOrderId;

    private String salesOrderNumber;                        // REF_DOC_NO;
    private String pickListNumber;
    private String requiredDeliveryDate;                    //REQ_DEL_DATE
    private String status;
    private String tokenNumber;
    private String orderType;                                // REF_FIELD_1

    private String companyCode;
    private String branchCode;
    private String languageId;
    private String warehouseId;

    private String customerId;
    private String customerName;
    private String loginUserId;

    private String fromCompanyCode;
    private String fromBranchCode;
    private String toCompanyCode;
    private String toBranchCode;
    private String transferOrderNumber;
    private String poNumber;                            // REF_DOC_NO;
    private String supplierInvoiceNo;

    //Lines
    private Long lineReference;                                // IB_LINE_NO
    private String barcodeId;
    private String sku;                                    // ITM_CODE
    private String skuDescription;                            // ITEM_TEXT
    private Double orderedQty;                                // ORD_QTY
    private Double returnQty;                                // ORD_QTY
    private String uom;                                        // ORD_UOM
    private String manufacturerCode;
    private String manufacturerName;

    private String storageSectionId;
    private String gender;
    private String color;
    private String size;
    private String brand;

    private String origin;
    private String supplierName;
    private Double packQty;
    private Double expectedQty;

    private String itemType;
    private String itemGroup;
    private String batchSerialNumber;
    private String address;
    /*----------------------Impex--------------------------------------------------*/
    private String alternateUom;
    private Double noBags;
    private Double bagSize;
    private Double mrp;

    /*--------------------------Walkaroo--------------------------------------------*/
    private String outbound;
    private Long itm;
    private String customerCode;
    private String customer;
    private String shipToCode;
    private String shipToParty;
    private String materialNo;
    private String priceSegment;
    private Double qty;
    private String specialStock;
    private String mtoNumber;
    private String skuCode;
}
