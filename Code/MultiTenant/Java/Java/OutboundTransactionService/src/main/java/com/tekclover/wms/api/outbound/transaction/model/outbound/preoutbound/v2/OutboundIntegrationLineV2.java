package com.tekclover.wms.api.outbound.transaction.model.outbound.preoutbound.v2;

import com.tekclover.wms.api.outbound.transaction.model.outbound.preoutbound.OutboundIntegrationLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class OutboundIntegrationLineV2 extends OutboundIntegrationLine {

    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;

    //---------------------ALM Changes-----------------------------------------
    private String manufacturerCode;                        // MFR_CODE
    private String manufacturerName;                        // MFR_CODE
    private String origin;                                    // ORIGIN
    private String brand;                                    // BRAND
    private String companyCode;
    private String branchCode;

    private String salesOrderNumber;
    private String pickListNo;
    private String manufacturerFullName;
    private Long middlewareId;
    private Long middlewareHeaderId;
    private String middlewareTable;
    private String salesInvoiceNo;
    private String supplierInvoiceNo;
    private String referenceDocumentType;
    private Integer imsSaleTypeCode;

    private String transferOrderNo;
    private String returnOrderNo;
    private String isCompleted;
    private String isCancelled;
}