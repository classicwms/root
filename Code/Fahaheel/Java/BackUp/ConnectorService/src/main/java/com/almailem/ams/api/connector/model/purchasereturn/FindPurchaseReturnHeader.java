package com.almailem.ams.api.connector.model.purchasereturn;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindPurchaseReturnHeader {

    private List<Long> purchaseReturnHeaderId;
    private List<String> companyCode;
    private List<String> branchCode;
    private List<String> returnOrderNo;

    private Date fromOrderReceivedOn;

    private Date toOrderReceivedOn;

    private Date fromOrderProcessedOn;

    private Date toOrderProcessedOn;

    private List<Long> processedStatusId;

}
