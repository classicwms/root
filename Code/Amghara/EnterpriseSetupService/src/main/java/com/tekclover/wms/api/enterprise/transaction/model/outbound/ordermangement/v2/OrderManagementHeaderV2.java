package com.tekclover.wms.api.enterprise.transaction.model.outbound.ordermangement.v2;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.tekclover.wms.api.enterprise.transaction.model.outbound.ordermangement.OrderManagementHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(callSuper = true)
public class OrderManagementHeaderV2 extends OrderManagementHeader {

    @Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
    private String companyDescription;

    @Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
    private String plantDescription;

    @Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
    private String warehouseDescription;

    @Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
    private String statusDescription;

    @Column(name = "MIDDLEWARE_ID")
    private Long middlewareId;

    @Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
    private String middlewareTable;

    @Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
    private String referenceDocumentType;

    @Column(name = "SALES_ORDER_NUMBER", columnDefinition = "nvarchar(150)")
    private String salesOrderNumber;

    @Column(name = "PICK_LIST_NUMBER", columnDefinition = "nvarchar(150)")
    private String pickListNumber;

    @Column(name = "TOKEN_NUMBER", columnDefinition = "nvarchar(150)")
    private String tokenNumber;

    /*-------------------------------------------------------------------------------------------------*/

    @Column(name = "FROM_BRANCH_CODE", columnDefinition = "nvarchar(50)")
    private String fromBranchCode;

    @Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(20)")
    private String isCompleted;

    @Column(name = "IS_CANCELLED", columnDefinition = "nvarchar(20)")
    private String isCancelled;

    @Column(name = "M_UPDATED_ON")
    private Date mUpdatedOn;

    @Column(name = "TARGET_BRANCH_CODE", columnDefinition = "nvarchar(50)")
    private String targetBranchCode;

    @Column(name = "IMS_SALE_TYP_CODE")
    private Integer imsSaleTypeCode;

    @Column(name = "CSTR_COD", columnDefinition = "nvarchar(50)")
    private String customerCode;

    @Column(name = "TFR_REQ_TYP", columnDefinition = "nvarchar(50)")
    private String transferRequestType;

}