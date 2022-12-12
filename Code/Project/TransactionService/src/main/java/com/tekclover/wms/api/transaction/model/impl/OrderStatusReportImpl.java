package com.tekclover.wms.api.transaction.model.impl;


import java.util.Date;

public interface OrderStatusReportImpl {
    String getSoNumber();
    String getDoNumber();
    String getPartnerCode();
    String getPartnerName();
    String getWarehouseId();
    String getItemCode();
    String getItemDescription();
    Date getDeliveryConfirmedOn();
    Double getOrderedQty();
    Double getDeliveryQty();
    String getStatusIdName();
    Long getStatusId();
    String getOrderType();
    Date getRefDocDate();
    Date getRequiredDeliveryDate();
}
