package com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound.v2;

import lombok.Data;

import java.util.List;

@Data
public class FindOutboundOrderLineV2 {

    private List<String> itemCode;
    private List<String> orderId;
    private List<String> manufacturerCode;
    private List<Long> middlewareId;
    private List<String> middlewareTable;
    private List<String> transferOrderNumber;
    private List<String> supplierInvoiceNo;
    private List<Double> orderedQty;
}