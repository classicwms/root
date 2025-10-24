package com.almailem.ams.api.connector.model.periodic;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindPeriodicHeader {

    private List<Long> periodicHeaderId;
    private List<String> companyCode;
    private List<String> branchCode;
    private List<String> cycleCountNo;

    private Date fromOrderReceivedOn;

    private Date toOrderReceivedOn;

    private Date fromOrderProcessedOn;

    private Date toOrderProcessedOn;

    private List<Long> processedStatusId;

}
