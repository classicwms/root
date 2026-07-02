package com.tekclover.wms.api.transaction.model.warehouse.inbound.v2;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindInboundOrderLineV2 {

    private List<Long> middlewareId;
    private List<Long> middlewareHeaderId;
    private List<Double> receivedQty;
    private List<String> transferOrderNumber;
    private List<String> supplierCode;
    private List<String> orderId;
    private List<String> itemCode;
    private List<String> invoiceNumber;
    private Date fromReceivedDate;
    private Date toReceivedDate;
    private Date fromExpectedDate;
    private Date toExpectedDate;
}
