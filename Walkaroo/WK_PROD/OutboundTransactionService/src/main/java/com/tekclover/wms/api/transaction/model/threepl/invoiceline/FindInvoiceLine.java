package com.tekclover.wms.api.transaction.model.threepl.invoiceline;

import lombok.Data;
import java.util.List;

@Data
public class FindInvoiceLine {
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<Long> invoiceNumber;
    private List<String> partnerCode;
    private List<Long> lineNumber;
    private List<Long> invoiceHeaderId;
    private List<Long> statusId;
}
