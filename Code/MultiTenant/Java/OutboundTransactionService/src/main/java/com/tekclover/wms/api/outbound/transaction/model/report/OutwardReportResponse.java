package com.tekclover.wms.api.outbound.transaction.model.report;

import java.util.Date;

public interface OutwardReportResponse {
    public Date   getShippedDate();
    public String   getOrderNo();
    public Date   getReceiptDate();
    public String   getCustomerName();
    public String   getSku();
    public String   getDescription();
    public String   getBatchNo();
    public Date getMfg();
    public String   getVehicleNo();
    public String   getUom();
    public Double   getShippedQty();
    public String getPreOutboundNo();

    public Date getCreatedOn();

    public String getCreatedBy();

    public String getTokenNumber();

    public String getReferenceField25();
    String getContainerReceiptNo();
}
