package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindPickListHeader {

    private List<Long> pickListHeaderId;
    private List<String> companyCode;
    private List<String> branchCode;
    private List<String> salesOrderNo;
    private List<String> pickListNo;
    private List<String> tokenNumber;

    private Date fromOrderReceivedOn;

    private Date toOrderReceivedOn;

    private Date fromOrderProcessedOn;

    private Date toOrderProcessedOn;

    private List<Long> processedStatusId;

}
