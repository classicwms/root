package com.mnrclara.spark.core.model.MultiTenant;

import lombok.Data;

import java.util.List;

@Data
public class FindPreOutBoundLineV2 {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> refDocNumber;
    private List<String> preOutboundNo;
    private List<String> partnerCode;
    private List<Long> lineNumber;
    private List<Long> statusId;
    private List<String> itemCode;
}
