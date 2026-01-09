package com.wms.spark.core.model.walkaroo;


import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
public class OutboundHeaderSpark {

    private String companyCodeId;
    private String languageId;
    private String warehouseId;
    private String plantId;

    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;

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
    private Timestamp deliveryConfirmedOn;




}
