package com.tekclover.wms.api.inbound.orders.model.inbound.v2;

import com.tekclover.wms.api.inbound.orders.model.inbound.InboundHeader;
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
public class InboundHeaderV2 extends InboundHeader {

    @Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
    private String companyDescription;

    @Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
    private String plantDescription;

    @Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
    private String warehouseDescription;

    @Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
    private String statusDescription;

    @Column(name = "PURCHASE_ORDER_NUMBER", columnDefinition = "nvarchar(150)")
    private String purchaseOrderNumber;

    @Column(name = "MIDDLEWARE_ID", columnDefinition = "nvarchar(50)")
    private String middlewareId;

    @Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
    private String middlewareTable;

    @Column(name = "MANUFACTURER_FULL_NAME", columnDefinition = "nvarchar(150)")
    private String manufacturerFullName;

    @Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
    private String referenceDocumentType;

    @Column(name = "COUNT_OF_ORD_LINES")
    private Long countOfOrderLines;

    @Column(name = "RECEIVED_LINES")
    private Long receivedLines;

    /*-------------------------------------------------------------------------------------------------------*/
    @Column(name = "TRANSFER_ORDER_DATE")
    private Date transferOrderDate;

    @Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(20)")
    private String isCompleted;

    @Column(name = "IS_CANCELLED", columnDefinition = "nvarchar(20)")
    private String isCancelled;

    @Column(name = "M_UPDATED_ON")
    private Date mUpdatedOn;

    @Column(name = "SOURCE_BRANCH_CODE", columnDefinition = "nvarchar(50)")
    private String sourceBranchCode;

    @Column(name = "SOURCE_COMPANY_CODE", columnDefinition = "nvarchar(50)")
    private String sourceCompanyCode;

    @Column(name = "PARENT_PRODUCTION_ORDER_NO", columnDefinition = "nvarchar(50)")
    private String parentProductionOrderNo;

    @Column(name = "CSTR_COD", columnDefinition = "nvarchar(50)")
    private String customerCode;

    @Column(name = "TFR_REQ_TYP", columnDefinition = "nvarchar(50)")
    private String TransferRequestType;

    @Column(name = "AMS_SUP_INV", columnDefinition = "nvarchar(50)")
    private String AMSSupplierInvoiceNo;

    /*-------------------------------------------Reeferon-----------------------------------*/

    @Column(name = "QTY_IN_CASE")
    private Double qtyInCase;

    @Column(name = "QTY_IN_PIECE")
    private Double qtyInPiece;

    @Column(name = "QTY_IN_CREATE")
    private Double qtyInCreate;

    @Column(name = "CUSTOMER_ID", columnDefinition = "nvarchar(50)")
    private String customerId;

    @Column(name = "CUSTOMER_NAME", columnDefinition = "nvarchar(50)")
    private String customerName;

}