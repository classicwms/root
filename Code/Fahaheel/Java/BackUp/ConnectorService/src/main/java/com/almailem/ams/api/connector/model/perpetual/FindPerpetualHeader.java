package com.almailem.ams.api.connector.model.perpetual;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindPerpetualHeader {

    private List<Long> perpetualHeaderId;
    private List<String> companyCode;
    private List<String> branchCode;
    private List<String> cycleCountNo;

    private Date fromOrderReceivedOn;

    private Date toOrderReceivedOn;

    private Date fromOrderProcessedOn;

    private Date toOrderProcessedOn;

    private List<Long> processedStatusId;

}
