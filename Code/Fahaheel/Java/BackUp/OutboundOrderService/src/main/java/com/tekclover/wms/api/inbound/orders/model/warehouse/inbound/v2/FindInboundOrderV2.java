package com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindInboundOrderV2 {

    private List<String> branchCode;
    private List<String> companyCode;
    private List<String> transferOrderNumber;
    private List<Long> middlewareId;
    private List<String> middlewareTable;
    private List<String> orderId;
    private List<String> refDocumentNo;
    private List<String> refDocumentType;
    private List<String> warehouseID;
    private Date fromOrderReceivedOn;
    private Date toOrderReceivedOn;
    private Date fromOrderProcessedOn;
    private Date toOrderProcessedOn;
    private List<Long> processedStatusId;
}
