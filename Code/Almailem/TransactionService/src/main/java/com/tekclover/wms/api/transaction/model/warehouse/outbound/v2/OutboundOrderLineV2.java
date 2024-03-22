package com.tekclover.wms.api.transaction.model.warehouse.outbound.v2;

import com.tekclover.wms.api.transaction.model.warehouse.outbound.OutboundOrderLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(callSuper = true)
public class OutboundOrderLineV2 extends OutboundOrderLine {

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

    private String manufacturerName;
    private String manufacturerFullName;
    private String fulfilmentMethod;

    private String salesOrderNo;
    private String pickListNo;

    private Long middlewareId;
    private Long middlewareHeaderId;
    private String middlewareTable;
    private String transferOrderNumber;
    private String salesInvoiceNo;
    private String supplierInvoiceNo;
    private String returnOrderNo;
    private String isCompleted;
    private String isCancelled;
    private String customerType;
    private Long outboundOrderTypeID;
}
