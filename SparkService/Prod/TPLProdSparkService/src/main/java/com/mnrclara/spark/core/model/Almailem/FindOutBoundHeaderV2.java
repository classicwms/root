package com.mnrclara.spark.core.model.Almailem;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindOutBoundHeaderV2 {

    private List<String> warehouseId;
    private List<String> refDocNumber;
    private List<String> partnerCode;
    private List<Long> outboundOrderTypeId;
    private List<Long> statusId;
    private List<String> soType; //referenceField1;

    private Date startRequiredDeliveryDate;
    private Date endRequiredDeliveryDate;

    private Date startDeliveryConfirmedOn;
    private Date endDeliveryConfirmedOn;

    private Date startOrderDate;
    private Date endOrderDate;

    // V2 fields
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> targetBranchCode;
    private List<String> preOutboundNo;
}
