package com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.v2;

import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.OutboundOrder;
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
public class OutboundOrderV2 extends OutboundOrder {

    @Column(name = "branch_code")
    private String branchCode;
    @Column(name = "company_code")
    private String companyCode;
    @Column(name = "language_id")
    private String languageId;
    @Column(name = "return_order_reference")
    private String returnOrderReference;

    @Column(name = "pick_list_number")
    private String pickListNumber;
    @Column(name = "pick_list_status")
    private String pickListStatus;
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "branch_name")
    private String branchName;
    @Column(name = "warehouse_name")
    private String warehouseName;
    @Column(name = "sales_order_number")
    private String salesOrderNumber;
    @Column(name = "sales_invoice_date")
    private Date salesInvoiceDate;
    @Column(name = "sales_invoice_number")
    private String salesInvoiceNumber;
    @Column(name = "source_company_code")
    private String sourceCompanyCode;
    @Column(name = "target_company_code")
    private String targetCompanyCode;
    @Column(name = "target_branch_code")
    private String targetBranchCode;
    @Column(name = "token_number")
    private String tokenNumber;
//    @Column(name = "transfer_order_number")
//    private String transferOrderNumber;
//    @Column(name = "transfer_order_date")
//    private Date transferOrderDate;
//    @Column(name = "source_branch_code")
//    private String sourceBranchCode;


    @Column(name = "delivery_type")
    private String deliveryType;
    @Column(name = "customer_id")
    private String customerId;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "address")
    private String address;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "alternate_no")
    private String alternateNo;
    @Column(name = "status")
    private String status;

    @Column(name = "from_branch_code")
    private String fromBranchCode;
    @Column(name = "is_completed")
    private String isCompleted;
    @Column(name = "is_cancelled")
    private String isCancelled;
    @Column(name = "updated_on")
    private Date updatedOn;
    @Column(name = "middleware_id")
    private Long middlewareId;
    @Column(name = "middleware_table")
    private String middlewareTable;
    @Column(name = "customer_type")
    private String customerType;
    @Column(name = "number_of_attempts")
    private Long numberOfAttempts;
    @Column(name = "ims_sales_type_code")
    private Integer imsSaleTypeCode;
    @Column(name = "transfer_request_type")
    private String TransferRequestType;
    @Column(name = "customer_code")
    private String customerCode;
    @Column(name = "invoice")
    private String invoice;
    private Long executed;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "OUTBOUND_ORDER_HEADER_ID",referencedColumnName = "OUTBOUND_ORDER_HEADER_ID")
    private Set<OutboundOrderLineV2> line;


}