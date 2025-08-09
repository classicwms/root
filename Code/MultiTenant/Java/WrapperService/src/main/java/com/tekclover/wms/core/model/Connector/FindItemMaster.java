package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindItemMaster {

    private List<Long> itemMasterId;
    private List<String> companyCode;
    private List<String> branchCode;
    private List<String> itemCode;
    private List<String> manufacturerCode;

    private Date fromOrderReceivedOn;

    private Date toOrderReceivedOn;

    private Date fromOrderProcessedOn;

    private Date toOrderProcessedOn;

    private List<Long> processedStatusId;


}
