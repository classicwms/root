package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchOperationLineReportProcess {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> productionOrderNo;
    private List<String> batchNumber;
    private List<String> itemCode;
    private List<Long> statusId;

    private List<String> receipeId;
    private List<String> operationNumber;
    private List<String> phaseNumber;
    private List<String> bomItem;
    private List<String> supervisorName;

    private Date startCreatedOn;
    private Date endCreatedOn;

    private List<String> process;        //to filter process - work for process report only

}