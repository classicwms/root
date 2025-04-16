package com.tekclover.wms.api.transaction.model.threepl.invoiceheader;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindInvoice {

    private String companyId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String partnerCode;
    private List<String> serviceTypeId;
    private Date fromDate;
    private Date toDate;
}
