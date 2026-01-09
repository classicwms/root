package com.tekclover.wms.api.inbound.transaction.model.report;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchTransactionDetailsDashBoard {

    private List<Long> transactionId;

    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> languageId;
    private List<String> warehouseId;
    private List<String> process;
    private List<String> itemCode;
    private List<String> barcodeId;
    private List<String> createdBy;
    private List<String> updatedBy;
    private Date startCreatedOn;
    private Date endCreatedOn;

    private boolean isRead;
}