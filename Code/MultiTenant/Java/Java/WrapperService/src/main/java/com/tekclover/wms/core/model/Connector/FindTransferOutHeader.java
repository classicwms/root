package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindTransferOutHeader {

    private List<Long> transferOutHeaderId;
    private List<String> sourceCompanyCode;
    private List<String> targetCompanyCode;
    private List<String> transferOrderNumber;
    private List<String> sourceBranchCode;
    private List<String> targetBranchCode;

    private Date fromOrderReceivedOn;

    private Date toOrderReceivedOn;

    private Date fromOrderProcessedOn;

    private Date toOrderProcessedOn;

    private List<Long> processedStatusId;

}
