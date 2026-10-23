package com.tekclover.wms.api.inbound.transaction.model.impl;

public interface OutBoundLineImpl {
    String getRefDocNo();
    Double getLinesOrdered();
    Double getLinesShipped();
    Double getOrderedQty();
    Double getShippedQty();
}
