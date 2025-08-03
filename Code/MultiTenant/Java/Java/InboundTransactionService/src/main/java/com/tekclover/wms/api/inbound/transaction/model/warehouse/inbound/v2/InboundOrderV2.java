package com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2;

import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.InboundOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(callSuper = true)
public class InboundOrderV2 extends InboundOrder {

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "company_code")
    private String companyCode;

    @Column(name = "return_order_reference")
    private String returnOrderReference;

    @Column(name = "transfer_order_number")
    private String transferOrderNumber;

    @Column(name = "source_company_code")
    private String sourceCompanyCode;
    @Column(name = "source_branch_code")
    private String sourceBranchCode;
    @Column(name = "transfer_order_date")
    private Date transferOrderDate;
    @Column(name = "is_completed")
    private String isCompleted;
    @Column(name = "updated_on")
    private Date updatedOn;
    @Column(name = "is_cancelled")
    private String isCancelled;
    @Column(name = "EXECUTED")
    private Long executed;
    //MiddleWare Fields
    @Column(name = "middleware_id")
    private Long middlewareId;
    @Column(name = "middleware_table")
    private String middlewareTable;
    @Column(name = "number_of_attempts")
    private Long numberOfAttempts;
    @Column(name = "customer_code")
    private String customerCode;
    @Column(name = "transfer_request_type")
    private String TransferRequestType;
    @Column(name = "ams_supplier_invoice_no")
    private String AMSSupplierInvoiceNo;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "INBOUND_ORDER_HEADER_ID",referencedColumnName = "INBOUND_ORDER_HEADER_ID")
    private Set<InboundOrderLinesV2> line;
}