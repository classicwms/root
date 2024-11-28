package com.tekclover.wms.api.transaction.model.warehouse.outbound.v2;

import com.tekclover.wms.api.transaction.model.warehouse.outbound.OutboundOrder;
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

    private String branchCode;
    private String companyCode;
    private String languageId;
    private String returnOrderReference;

    private String pickListNumber;
    private String pickListStatus;
    private String companyName;
    private String branchName;
    private String warehouseName;
    private String salesOrderNumber;
    private Date salesInvoiceDate;
    private String salesInvoiceNumber;
    private String sourceCompanyCode;
    private String targetCompanyCode;
    private String targetBranchCode;
    private String tokenNumber;

    private String deliveryType;
    private String customerId;
    private String customerName;
    private String address;
    private String phoneNumber;
    private String alternateNo;
    private String status;
    private String invoice;

    private String fromBranchCode;
    private String isCompleted;
    private String isCancelled;
    private Date updatedOn;
    private Long middlewareId;
    private String middlewareTable;
    private String customerType;
    private String loginUserId;
    private Long numberOfAttempts;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "OUTBOUND_ORDER_HEADER_ID",referencedColumnName = "OUTBOUND_ORDER_HEADER_ID")
    private Set<OutboundOrderLineV2> line;
}