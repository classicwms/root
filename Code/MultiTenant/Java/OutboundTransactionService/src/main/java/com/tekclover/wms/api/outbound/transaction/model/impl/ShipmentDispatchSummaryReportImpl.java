package com.tekclover.wms.api.outbound.transaction.model.impl;

import java.util.Date;

public interface ShipmentDispatchSummaryReportImpl {
    String getSoNumber();
    String getPartnerCode();
    Date getOrderReceiptTime();
    Double getLinesOrdered();
    Double getLinesShipped();
    Double getOrderedQty();
    Double getShippedQty();
    Double getPercentageShipped();
}
