package com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2;

import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.InboundOrderLines;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(callSuper = true)
public class InboundOrderLinesV2 extends InboundOrderLines {

    private String manufacturerCode;
    private String origin;
    private String supplierName;
    private String brand;
    private Double packQty;
    private String fromCompanyCode;
    private Double expectedQty;
    protected String storeID;

    private String sourceBranchCode;
    private String countryOfOrigin;
    private String manufacturerFullName;
    private String purchaseOrderNumber;
    private String AMSSupplierInvoiceNo;

    //MiddleWare Fields
    private Long middlewareId;
    private Long middlewareHeaderId;
    private String middlewareTable;

    private String transferOrderNumber;
    private Date receivedDate;
    private Double receivedQty;
    private String receivedBy;
    private String isCompleted;
    private String isCancelled;
    private String supplierInvoiceNo;
    private String companyCode;
    private String branchCode;
    private Long inboundOrderTypeId; 		// IB_ORD_TYP_ID
}