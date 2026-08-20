package com.tekclover.wms.core.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class OutwardReportResponse {

    public Date shippedDate;
    public String orderNo;
    public Date receiptDate;
    public String customerName;
    public String sku;
    public String description;
    public String batchNo;
    public Date mfg;
    public String vehicleNo;
    public String uom;
    public Double shippedQty;
    public String inventoryOwner;
    public String preOutboundNo;
    public Date createdOn;
    public String createdBy;
    public String tokenNumber;
    public String referenceField25;
    public String containerReceiptNo;
}
