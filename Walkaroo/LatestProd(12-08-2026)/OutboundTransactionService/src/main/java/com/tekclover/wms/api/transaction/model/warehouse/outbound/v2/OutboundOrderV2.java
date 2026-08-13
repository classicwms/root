package com.tekclover.wms.api.transaction.model.warehouse.outbound.v2;

import java.util.Date;
import java.util.Set;

import javax.persistence.*;

import com.tekclover.wms.api.transaction.model.warehouse.outbound.OutboundOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(callSuper = true)
public class OutboundOrderV2 extends OutboundOrder {

    @Column(name = "BRANCH_CODE")
    private String branchCode;

    @Column(name = "COMPANY_CODE")
    private String companyCode;

    @Column(name = "LANGUAGE_ID")
    private String languageId;

    @Column(name = "RETURN_ORDER_REFERENCE")
    private String returnOrderReference;

    @Column(name = "PICK_LIST_NUMBER")
    private String pickListNumber;

    @Column(name = "PICK_LIST_STATUS")
    private String pickListStatus;

    @Column(name = "COMPANY_NAME")
    private String companyName;

    @Column(name = "BRANCH_NAME")
    private String branchName;

    @Column(name = "WAREHOUSE_NAME")
    private String warehouseName;

    @Column(name = "SALES_ORDER_NUMBER")
    private String salesOrderNumber;

    @Column(name = "SALES_INVOICE_DATE")
    private Date salesInvoiceDate;

    @Column(name = "SALES_INVOICE_NUMBER")
    private String salesInvoiceNumber;

    @Column(name = "SOURCE_COMPANY_CODE")
    private String sourceCompanyCode;

    @Column(name = "TARGET_COMPANY_CODE")
    private String targetCompanyCode;

    @Column(name = "TARGET_BRANCH_CODE")
    private String targetBranchCode;

    @Column(name = "TOKEN_NUMBER")
    private String tokenNumber;

    @Column(name = "DELIVERY_TYPE")
    private String deliveryType;

    @Column(name = "CUSTOMER_ID")
    private String customerId;

    @Column(name = "CUSTOMER_NAME")
    private String customerName;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "ALTERNATE_NO")
    private String alternateNo;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "FROM_BRANCH_CODE")
    private String fromBranchCode;

    @Column(name = "IS_COMPLETED")
    private String isCompleted;

    @Column(name = "IS_CANCELLED")
    private String isCancelled;

    @Column(name = "UPDATED_ON")
    private Date updatedOn;

    @Column(name = "MIDDLEWARE_ID")
    private Long middlewareId;

    @Column(name = "MIDDLEWARE_TABLE")
    private String middlewareTable;

    @Column(name = "CUSTOMER_TYPE")
    private String customerType;

    @Column(name = "LOGIN_USER_ID")
    private String loginUserId;

    @Column(name = "NUMBER_OF_ATTEMPTS")
    private Long numberOfAttempts;

    @Column(name = "EXECUTED")
    private Long executed;

    // Order_Processing
    @Column(name = "PRE_OUTBOUND_HEADER")
    private Long preOutboundHeader = 0L;

    @Column(name = "OUTBOUND_HEADER")
    private Long outboundHeader = 0L;

    @Column(name = "ORDER_MANAGEMENT_HEADER")
    private Long orderManagementHeader = 0L;

    @Column(name = "PICKUP_HEADER")
    private Long pickupHeader = 0L;

    @Column(name = "ORDER_TEXT")
    private String orderText;


    /*
     * Outbound Order from SAP
     */
    @Column(name = "ISSAPORDER")
    private Boolean isSAPOrder;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "OUTBOUND_ORDER_HEADER_ID",referencedColumnName = "OUTBOUND_ORDER_HEADER_ID")
    private Set<OutboundOrderLineV2> line;
}