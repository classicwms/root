package com.tekclover.wms.api.transaction.model.impl;

public interface OutBoundLineImpl {
    String getRefDocNo();
    Double getLinesOrdered();
    Double getLinesShipped();
    Double getOrderedQty();
    Double getShippedQty();
}
