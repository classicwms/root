package com.mnrclara.spark.core.model.MultiTenant;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindInboundLine {

    private List<String> warehouseId;
    private List<String> referenceField1;
    private List<Long> statusId;
    private List<String> itemCode;
    private List<String> manufacturerPartNo;
    private List<String> refDocNumber;
    private List<Long> inboundOrderTypeId;
    private Date startConfirmedOn;
    private Date endConfirmedOn;

    // V2 fields
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> manufactureName;
    private List<String> sourceBranchCode;
    private List<String> sourceCompanyCode;
    private Date startCreatedOn;
    private Date endCreatedOn;
}
