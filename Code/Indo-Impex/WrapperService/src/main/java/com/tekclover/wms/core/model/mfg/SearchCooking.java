package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchCooking {

    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> languageId;
    private List<String> warehouseId;
    private List<String> operationNumber;
    private List<String> receipeId;
    private List<String> productionOrderNo;
    private List<Long> productionOrderLineNo;
    private List<String> itemCode;
    private List<String> statusId;

    private Date startCreatedOn;
    private Date endCreatedOn;
}
