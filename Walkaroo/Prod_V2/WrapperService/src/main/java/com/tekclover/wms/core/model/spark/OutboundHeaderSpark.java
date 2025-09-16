package com.tekclover.wms.core.model.spark;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class OutboundHeaderSpark {

    private String companyCodeId;
    private String languageId;
    private String warehouseId;
    private String plantId;

    private String salesOrderNumber;
    private String customerId;
    private String customerName;
    private String shipToCode;
    private String shipToParty;

    private String deliveryOrderNo;
    private String refDocNumber;
    private Long outboundOrderTypeId;

    private Timestamp createdOn;
    private Timestamp requiredDeliveryDate;

    private Long statusId;

}
