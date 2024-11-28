package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchPaste {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> productionOrderNo;
    private List<Long> productionOrderLineNo;
    private List<String> receipeId;
    private List<String> operationNumber;
    private List<String> itemCode;

    private Date startCreatedOn;
    private Date endCreatedOn;
    private Date startConfirmedOn;
    private Date endConfirmedOn;

}
