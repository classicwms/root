package com.almailem.ams.api.connector.model.supplierinvoice;


import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchSupplierInvoiceHeader {

    private List<Long> supplierInvoiceHeaderId;

    private List<String> companyCode;

    private List<String> branchCode;


    private List<String> supplierInvoiceNo;

    private List<Long> processedStatusId;

    private Date fromOrderProcessedOn;

    private Date toOrderProcessedOn;

    private Date fromOrderReceivedOn;

    private Date toOrderReceivedOn;

}
