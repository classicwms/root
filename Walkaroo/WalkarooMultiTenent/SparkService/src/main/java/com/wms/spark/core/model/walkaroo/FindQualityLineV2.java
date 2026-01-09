package com.wms.spark.core.model.walkaroo;

import lombok.Data;

import java.util.List;

@Data
public class FindQualityLineV2 {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> refDocNumber;
    private List<String> partnerCode;
    private List<String> qualityInspectionNo;
    private List<Long> statusId;
    private List<String> preOutboundNo;
    private List<Long> lineNumber;
    private List<String> itemCode;

}
