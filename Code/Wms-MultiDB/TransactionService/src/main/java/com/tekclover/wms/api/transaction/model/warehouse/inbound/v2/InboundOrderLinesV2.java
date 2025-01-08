package com.tekclover.wms.api.transaction.model.warehouse.inbound.v2;

import com.tekclover.wms.api.transaction.model.warehouse.inbound.InboundOrderLines;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(callSuper = true)
public class InboundOrderLinesV2 extends InboundOrderLines {


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
    @Column(name = "manufacturer_full_name")
    private String manufacturerFullName;
    @Column(name = "purchase_order_number")
    private String purchaseOrderNumber;

    //MiddleWare Fields
    @Column(name = "middleware_id")
    private Long middlewareId;
    @Column(name = "middleware_header_id")
    private Long middlewareHeaderId;
    @Column(name = "middleware_table")
    private String middlewareTable;
    @Column(name = "storage_section_id")
    private String storageSectionId;
    @Column(name = "batch_serial_number")
    private String batchSerialNumber;
    @Column(name = "transfer_order_number")
    private String transferOrderNumber;
    @Column(name = "received_date")
    private Date receivedDate;
    @Column(name = "received_qty")
    private Double receivedQty;
    @Column(name = "received_by")
    private String receivedBy;
    @Column(name = "is_completed")
    private String isCompleted;
    @Column(name = "is_cancelled")
    private String isCancelled;
    @Column(name = "supplier_invoice_no")
    private String supplierInvoiceNo;
    @Column(name = "company_code")
    private String companyCode;
    @Column(name = "branch_code")
    private String branchCode;
    @Column(name = "inbound_order_type_id")
    private Long inboundOrderTypeId; 		// IB_ORD_TYP_ID

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
    @Column(name = "barcode_id")
	private String barcodeId;

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
