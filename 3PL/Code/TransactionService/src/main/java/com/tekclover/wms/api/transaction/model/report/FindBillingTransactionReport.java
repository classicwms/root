package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindBillingTransactionReport {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String partnerCode;
    private List<String> serviceTypeId;
    private Date startCreatedOn;
    private Date endCreatedOn;
}
