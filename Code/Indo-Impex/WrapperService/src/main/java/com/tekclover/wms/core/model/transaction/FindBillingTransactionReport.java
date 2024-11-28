package com.tekclover.wms.core.model.transaction;


import lombok.Data;

import java.util.Date;

@Data
public class FindBillingTransactionReport {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String partnerCode;

    private Date startCreatedOn;
    private Date endCreatedOn;
}
