package com.tekclover.wms.api.outbound.transaction.model.inbound.v2;

import com.tekclover.wms.api.outbound.transaction.model.inbound.InboundLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(callSuper = true)
public class InboundLineV2 extends InboundLine {

    @Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
    private String companyDescription;

    @Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
    private String plantDescription;

    @Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
    private String warehouseDescription;

    @Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
    private String statusDescription;

    @Column(name = "MFR_CODE", columnDefinition = "nvarchar(255)")
    private String manufacturerCode;

    @Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
    private String manufacturerName;

    @Column(name = "MIDDLEWARE_ID", columnDefinition = "nvarchar(50)")
    private String middlewareId;

    @Column(name = "MIDDLEWARE_HEADER_ID", columnDefinition = "nvarchar(50)")
    private String middlewareHeaderId;

    @Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
    private String middlewareTable;

    @Column(name = "MANUFACTURER_FULL_NAME", columnDefinition = "nvarchar(150)")
    private String manufacturerFullName;

    @Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
    private String referenceDocumentType;

    @Column(name = "PURCHASE_ORDER_NUMBER", columnDefinition = "nvarchar(150)")
    private String purchaseOrderNumber;

    @Column(name = "SUPPLIER_NAME", columnDefinition = "nvarchar(255)")
    private String supplierName;

    @Column(name = "STR_NO")
    private String batchSerialNumber;

    @Column(name = "MFR_DATE")
    private Date manufacturerDate;

    @Column(name = "EXP_DATE")
    private Date expiryDate;
    /*--------------------------------------------------------------------------------------------------------*/
    @Column(name = "BRANCH_CODE", columnDefinition = "nvarchar(50)")
    private String branchCode;

    @Column(name = "TRANSFER_ORDER_NO", columnDefinition = "nvarchar(50)")
    private String transferOrderNo;

    @Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(20)")
    private String isCompleted;

    @Column(name = "SOURCE_BRANCH_CODE", columnDefinition = "nvarchar(50)")
    private String sourceBranchCode;

    @Column(name = "SOURCE_COMPANY_CODE", columnDefinition = "nvarchar(50)")
    private String sourceCompanyCode;

    @Column(name = "AMS_SUP_INV", columnDefinition = "nvarchar(50)")
    private String AMSSupplierInvoiceNo;

    /*----------------------Impex--------------------------------------------------*/
    @Column(name = "ALT_UOM", columnDefinition = "nvarchar(50)")
    private String alternateUom;

    @Column(name = "NO_BAGS")
    private Double noBags;

    @Column(name = "BAG_SIZE")
    private Double bagSize;

    @Column(name = "MRP")
    private Double mrp;

    @Column(name = "ITM_TYP", columnDefinition = "nvarchar(100)")
    private String itemType;

    @Column(name = "ITM_GRP", columnDefinition = "nvarchar(100)")
    private String itemGroup;

    @Column(name = "BRAND", columnDefinition = "nvarchar(100)")
    private String brand;

    @Column(name = "ACT_ACCEPT_QTY")
    private Double actualAcceptedQty;

    @Column(name = "ACT_DAMAGE_QTY")
    private Double actualDamageQty;
}
