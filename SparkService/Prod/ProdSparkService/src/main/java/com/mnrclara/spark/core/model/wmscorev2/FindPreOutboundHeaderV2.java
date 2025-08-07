package com.mnrclara.spark.core.model.wmscorev2;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindPreOutboundHeaderV2 {

    private List<String> warehouseId;
    private List<String> refDocNumber;
    private List<String> preOutboundNo;
    private List<Long> outboundOrderTypeId;
    private List<String> soType;
    private List<String> soNumber;
    private List<String> partnerCode;
    private List<Long> statusId;
    private List<String> createdBy;

    private Date startRequiredDeliveryDate;
    private Date endRequiredDeliveryDate;
    private Date startOrderDate;
    private Date endOrderDate;
    private Date startCreatedOn;
    private Date endCreatedOn;

    //v2 fields
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;

}
