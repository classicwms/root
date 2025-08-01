package com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2;

import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.InboundOrder;
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

    private String branchCode;
    private String companyCode;
    private String returnOrderReference;

    private String transferOrderNumber;

    private String sourceCompanyCode;
    private String sourceBranchCode;
    private Date transferOrderDate;
    private String isCompleted;
    private Date updatedOn;
    private String isCancelled;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
    private Long numberOfAttempts;
    private String customerCode;
    private String TransferRequestType;
    private String AMSSupplierInvoiceNo;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "INBOUND_ORDER_HEADER_ID",referencedColumnName = "INBOUND_ORDER_HEADER_ID")
    private Set<InboundOrderLinesV2> line;

}