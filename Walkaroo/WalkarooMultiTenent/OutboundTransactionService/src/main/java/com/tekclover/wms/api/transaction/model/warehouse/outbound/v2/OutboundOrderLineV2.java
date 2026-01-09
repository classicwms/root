package com.tekclover.wms.api.transaction.model.warehouse.outbound.v2;

import com.tekclover.wms.api.transaction.model.warehouse.outbound.OutboundOrderLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(callSuper = true)
public class OutboundOrderLineV2 extends OutboundOrderLine {

    @Column(name = "MANUFACTURER_CODE")
    private String manufacturerCode;

    @Column(name = "ORIGIN")
    private String origin;

    @Column(name = "SUPPLIER_NAME")
    private String supplierName;

    @Column(name = "BRAND")
    private String brand;

    @Column(name = "PACK_QTY")
    private Double packQty;

    @Column(name = "FROM_COMPANY_CODE")
    private String fromCompanyCode;

    @Column(name = "EXPECTED_QTY")
    private Double expectedQty;

    @Column(name = "STOREID")
    protected String storeID;

    @Column(name = "SOURCE_BRANCH_CODE")
    private String sourceBranchCode;

    @Column(name = "COUNTRY_OF_ORIGIN")
    private String countryOfOrigin;

    @Column(name = "MANUFACTURER_NAME")
    private String manufacturerName;

    @Column(name = "MANUFACTURER_FULL_NAME")
    private String manufacturerFullName;

    @Column(name = "FULFILMENT_METHOD")
    private String fulfilmentMethod;

    @Column(name = "STORAGE_SECTION_ID")
    private String storageSectionId;

    @Column(name = "SALES_ORDER_NO")
    private String salesOrderNo;

    @Column(name = "PICK_LIST_NO")
    private String pickListNo;

    @Column(name = "MIDDLEWARE_ID")
    private Long middlewareId;

    @Column(name = "MIDDLEWARE_HEADER_ID")
    private Long middlewareHeaderId;

    @Column(name = "MIDDLEWARE_TABLE")
    private String middlewareTable;

    @Column(name = "TRANSFER_ORDER_NUMBER")
    private String transferOrderNumber;

    @Column(name = "SALES_INVOICE_NO")
    private String salesInvoiceNo;

    @Column(name = "SUPPLIER_INVOICE_NO")
    private String supplierInvoiceNo;

    @Column(name = "RETURN_ORDER_NO")
    private String returnOrderNo;

    @Column(name = "IS_COMPLETED")
    private String isCompleted;

    @Column(name = "IS_CANCELLED")
    private String isCancelled;

    @Column(name = "CUSTOMER_TYPE")
    private String customerType;

    @Column(name = "OUTBOUND_ORDER_TYPEID")
    private Long outboundOrderTypeID;

    /*----------------Walkaroo changes------------------------------------------------------*/

    @Column(name = "MATERIAL_NO")
    private String materialNo;

    @Column(name = "PRICE_SEGMENT")
    private String priceSegment;

    @Column(name = "ARTICLE_NO")
    private String articleNo;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "COLOR")
    private String color;

    @Column(name = "SIZE")
    private String size;

    @Column(name = "NO_PAIRS")
    private String noPairs;

    @Column(name = "BARCODE_ID")
    private String barcodeId;

    @Column(name = "SHIP_TO_CODE")
    private String shipToCode;

    @Column(name = "SHIP_TO_PARTY")
    private String shipToParty;

    @Column(name = "SPECIAL_STOCK")
    private String specialStock;

    @Column(name = "MTO_NUMBER")
    private String mtoNumber;


}
