package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindStagingHeaderV2 {

    private List<Long> inboundOrderTypeId;
    private List<String> stagingNo;
    private List<String> preInboundNo;
    private List<String> refDocNumber;
    private List<Long> statusId;
    private List<String> warehouseId;
    private List<String> createdBy;
    private Date startCreatedOn;
    private Date endCreatedOn;

    //v2 fields
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> customerId;
    private List<String> customerName;
}
