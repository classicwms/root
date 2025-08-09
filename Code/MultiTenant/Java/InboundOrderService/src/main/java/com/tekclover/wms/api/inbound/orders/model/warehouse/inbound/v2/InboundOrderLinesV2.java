package com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2;

import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.InboundOrderLines;
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
    @Column(name = "ams_supplier_invoice_no")
    private String AMSSupplierInvoiceNo;

    //MiddleWare Fields
    @Column(name = "middleware_id")
    private Long middlewareId;
    @Column(name = "middleware_header_id")
    private Long middlewareHeaderId;
    @Column(name = "middleware_table")
    private String middlewareTable;

    @Column(name = "BAG_SIZE")
    private Double bagSize;

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
    private Long inboundOrderTypeId; 			// IB_ORD_TYP_ID
    /*----------------------Impex--------------------------------------------------*/
    @Column(name = "ALT_UOM", columnDefinition = "nvarchar(50)")
    private String alternateUom;

    @Column(name = "NO_BAGS")
    private Double noBags;

    @Column(name = "MRP")
    private Double mrp;
    @Column(name = "BARCODE_ID", columnDefinition = "nvarchar(50)")
    private String barcodeId;
    @Column(name = "NO_OF_PAIRS", columnDefinition = "nvarchar(50)")
    private String noPairs;
    @Column(name = "VEHICLE_NO", columnDefinition = "nvarchar(50)")
    private String vehicleNo;

    @Column(name = "VEHICLE_REPORTING_DATE")
    private Date vehicleReportingDate;

    @Column(name = "VEHICLE_UNLOADING_DATE")
    private Date vehicleUnloadingDate;
}
