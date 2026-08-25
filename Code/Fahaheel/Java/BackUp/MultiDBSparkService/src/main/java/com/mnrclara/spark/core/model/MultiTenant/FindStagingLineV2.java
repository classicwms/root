package com.mnrclara.spark.core.model.MultiTenant;

import lombok.Data;

import java.util.List;

@Data
public class FindStagingLineV2 {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;

    private List<String> manufacturerCode;
    private List<String> manufacturerName;
    private List<String> origin;
    private List<String> brand;

    private List<String> palletId;
    private List<String> preInboundNo;
    private List<String> refDocNumber;
    private List<String> stagingNo;
    private List<String> palletCode;
    private List<String> caseCode;
    private List<Long> lineNo;
    private List<String> itemCode;
    private List<Long> statusId;
}
