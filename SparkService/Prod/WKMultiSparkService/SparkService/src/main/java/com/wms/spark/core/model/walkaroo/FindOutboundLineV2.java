package com.wms.spark.core.model.walkaroo;


import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindOutboundLineV2 {

    private List<String> warehouseId;
    private List<String> preOutboundNo;
    private List<String> refDocNumber;
    private List<String> partnerCode;
    private List<String> itemCode;

    // For Reports
    private List<String> orderType; // REF_FIELD_1
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> manufacturerName;
    private List<String> targetBranchCode;
    private List<String> salesOrderNumber;
    private List<Long> statusId;	// STATUS_ID
    private List<Long> lineNumber;
    private Date fromDeliveryDate;	// DLV_CNF_ON
    private Date toDeliveryDate;	// DLV_CNF_ON
}

