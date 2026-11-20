package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindInboundLineV2 {

    private List<String> warehouseId;
    private List<String> referenceField1;
    private List<Long> statusId;
    private List<String> itemCode;
    private List<String> manufacturerPartNo;
    private List<String> refDocNumber;
    private Date startConfirmedOn;
    private Date endConfirmedOn;
    private List<Long> inboundOrderTypeId;
    // V2 fields
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> sourceBranchCode;
    private List<String> sourceCompanyCode;
    private List<String> manufactureName;
    private Date startCreatedOn;
    private Date endCreatedOn;
}
