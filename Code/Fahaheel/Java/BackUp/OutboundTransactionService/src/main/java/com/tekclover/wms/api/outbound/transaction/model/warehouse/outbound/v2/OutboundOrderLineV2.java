package com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.v2;

import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.OutboundOrderLine;
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

    @Column(name = "manufacturer_code")
    private String manufacturerCode;
    @Column(name = "origin")
    private String origin;
    @Column(name = "supplier_name")
    private String supplierName;
    @Column(name = "brand")
    private String brand;
    @Column(name = "pack_qty")
    private Double packQty;
    @Column(name = "from_company_code")
    private String fromCompanyCode;
    @Column(name = "expected_qty")
    private Double expectedQty;
    @Column(name = "store_id")
    protected String storeID;

    @Column(name = "source_branch_code")
    private String sourceBranchCode;
    @Column(name = "country_of_origin")
    private String countryOfOrigin;
    @Column(name = "manufacturer_name")
    private String manufacturerName;
    @Column(name = "manufacturer_full_name")
    private String manufacturerFullName;
    @Column(name = "fulfilment_method")
    private String fulfilmentMethod;

    @Column(name = "sales_order_no")
    private String salesOrderNo;
    @Column(name = "pick_list_no")
    private String pickListNo;
    @Column(name = "middleware_id")
    private Long middlewareId;
    @Column(name = "middleware_header_id")
    private Long middlewareHeaderId;
    @Column(name = "middleware_table")
    private String middlewareTable;
    @Column(name = "transfer_order_number")
    private String transferOrderNumber;
    @Column(name = "sales_invoice_no")
    private String salesInvoiceNo;
    @Column(name = "supplier_invoice_no")
    private String supplierInvoiceNo;
    @Column(name = "return_order_no")
    private String returnOrderNo;
    @Column(name = "is_completed")
    private String isCompleted;
    @Column(name = "is_cancelled")
    private String isCancelled;
    @Column(name = "customer_type")
    private String customerType;
    @Column(name = "outbound_order_typeid")
    private Long outboundOrderTypeID;
    @Column(name = "ims_sales_type_code")
    private Integer imsSaleTypeCode;
    @Column(name = "barcode_id")
    private String barcodeId;

    @Column(name = "storage_section_id")
    private String storageSectionId;

    /*----------------Walkaroo changes------------------------------------------------------*/
    @Column(name = "material_no")
    private String materialNo;
    @Column(name = "price_segment")
    private String priceSegment;
    @Column(name = "article_no")
    private String articleNo;
    @Column(name = "gender")
    private String gender;
    @Column(name = "color")
    private String color;
    @Column(name = "size")
    private String size;
    @Column(name = "no_pairs")
    private String noPairs;

    /*----------------------Impex--------------------------------------------------*/
    @Column(name = "ALT_UOM", columnDefinition = "nvarchar(50)")
    private String alternateUom;

    @Column(name = "NO_BAGS")
    private Double noBags;

    @Column(name = "BAG_SIZE")
    private Double bagSize;

    @Column(name = "MRP")
    private Double mrp;


}