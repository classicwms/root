package com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2;

import java.util.Date;
import java.util.Set;

import javax.persistence.*;

import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.InboundOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(callSuper = true)
public class InboundOrderV2 extends InboundOrder {

    @Column(name = "return_order_reference")
    private String returnOrderReference;
    @Column(name = "transfer_order_number")
    private String transferOrderNumber;
    @Column(name = "parent_production_order_no")
    private String parentProductionOrderNo;
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
    @Column(name = "language_id")
    private String languageId;
    @Column(name = "login_user_id")
    private String loginUserId;

    @Column(name = "middleware_id")
    //MiddleWare Fields
    private Long middlewareId;
    @Column(name = "middleware_table")
    private String middlewareTable;
    @Column(name = "number_of_attempts")
    private Long numberOfAttempts;
    @Column(name = "executed")
    private Long executed;

    // Order_Processing
    @Column(name = "pre_inbound_header")
    private Long preInboundHeader = 0L;
    @Column(name = "inbound_header")
    private Long inboundHeader = 0L;
    @Column(name = "staging_header")
    private Long stagingHeader = 0L;
    @Column(name = "gr_header")
    private Long grHeader = 0L;
    @Column(name = "putaway_header")
    private Long putawayHeader = 0L;
    @Column(name = "order_text")
    private String orderText;
    
    //-------InboundUpload Vs SAP API -------Differentiator
    @Column(name = "is_sap_order")
  	private Boolean isSapOrder;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "INBOUND_ORDER_HEADER_ID",referencedColumnName = "INBOUND_ORDER_HEADER_ID")
    private Set<InboundOrderLinesV2> line;

}