package com.wms.spark.core.model.walkaroo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchGrHeaderV2 {

    private List<Long> inboundOrderTypeId;
    private List<String> goodsReceiptNo;
    private List<String> preInboundNo;
    private List<String> refDocNumber;
    private Date startCreatedOn;
    private Date endCreatedOn;
    private List<Long> statusId;
    private List<String> warehouseId;
    private List<String> caseCode;
    private List<String> createdBy;
    private Long deletionIndicator;
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
}

