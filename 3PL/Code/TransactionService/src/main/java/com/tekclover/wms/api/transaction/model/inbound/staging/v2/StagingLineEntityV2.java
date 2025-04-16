package com.tekclover.wms.api.transaction.model.inbound.staging.v2;

import com.tekclover.wms.api.transaction.model.inbound.staging.StagingLineEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(callSuper = true)
public class StagingLineEntityV2 extends StagingLineEntity {

    @Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
    private String companyDescription;

    @Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
    private String plantDescription;

    @Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
    private String warehouseDescription;

    @Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
    private String statusDescription;

    @Column(name = "INV_QTY")
    private Double inventoryQuantity;

    @Column(name = "MFR_CODE", columnDefinition = "nvarchar(255)")
    private String manufacturerCode;

    @Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
    private String manufacturerName;

    @Column(name = "ORIGIN", columnDefinition = "nvarchar(255)")
    private String origin;

    @Column(name = "BRAND", columnDefinition = "nvarchar(255)")
    private String brand;

    @Column(name = "PARTNER_ITEM_BARCODE", columnDefinition = "nvarchar(500)")
    private String partner_item_barcode;

    @Column(name = "REC_ACCEPT_QTY")
    private Double rec_accept_qty;

    @Column(name = "REC_DAMAGE_QTY")
    private Double rec_damage_qty;

    @Column(name = "MIDDLEWARE_ID", columnDefinition = "nvarchar(50)")
    private String middlewareId;

    @Column(name = "MIDDLEWARE_HEADER_ID", columnDefinition = "nvarchar(50)")
    private String middlewareHeaderId;

    @Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
    private String middlewareTable;

    @Column(name = "PURCHASE_ORDER_NUMBER", columnDefinition = "nvarchar(150)")
    private String purchaseOrderNumber;

    @Column(name = "MANUFACTURER_FULL_NAME", columnDefinition = "nvarchar(150)")
    private String manufacturerFullName;

    @Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
    private String referenceDocumentType;

    /*--------------------------------------------------------------------------------------------------------*/
    @Column(name = "BRANCH_CODE", columnDefinition = "nvarchar(50)")
    private String branchCode;

    @Column(name = "TRANSFER_ORDER_NO", columnDefinition = "nvarchar(50)")
    private String transferOrderNo;

    @Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(20)")
    private String isCompleted;

    @Column(name = "CSTR_COD", columnDefinition = "nvarchar(50)")
    private String customerCode;

    @Column(name = "TFR_REQ_TYP", columnDefinition = "nvarchar(50)")
    private String TransferRequestType;

    @Column(name = "AMS_SUP_INV", columnDefinition = "nvarchar(50)")
    private String AMSSupplierInvoiceNo;

    @Column(name = "SOURCE_BRANCH_CODE", columnDefinition = "nvarchar(50)")
    private String sourceBranchCode;
}