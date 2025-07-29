package com.tekclover.wms.api.outbound.transaction.model.impl;

public interface OutBoundLineImpl {
    String getRefDocNo();
    Double getLinesOrdered();
    Double getLinesShipped();
    Double getOrderedQty();
    Double getShippedQty();
}
