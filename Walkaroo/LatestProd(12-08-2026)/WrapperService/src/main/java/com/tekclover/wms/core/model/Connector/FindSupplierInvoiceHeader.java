package com.tekclover.wms.core.model.Connector;


import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindSupplierInvoiceHeader {

    private List<Long> supplierInvoiceHeaderId;

    private List<String> companyCode;

    private List<String> branchCode;


    private List<String> supplierInvoiceNo;


    private Date fromOrderReceivedOn;

    private Date toOrderReceivedOn;

    private Date fromOrderProcessedOn;

    private Date toOrderProcessedOn;

    private List<Long> processedStatusId;





}
