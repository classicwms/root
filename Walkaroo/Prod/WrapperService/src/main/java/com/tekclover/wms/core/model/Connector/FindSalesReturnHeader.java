package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindSalesReturnHeader {

    private List<Long> salesReturnHeaderId;

    private List<String> companyCode;

    private List<String> branchCodeOfReceivingWarehouse;

    private List<String> branchCode;

    private List<String> returnOrderNo;

    private Date fromOrderReceivedOn;

    private Date toOrderReceivedOn;

    private Date fromOrderProcessedOn;

    private Date toOrderProcessedOn;

    private List<Long> processedStatusId;

}
