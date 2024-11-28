package com.tekclover.wms.core.model.threepl;

import lombok.Data;

import java.util.List;

@Data
public class FindInvoiceLine {
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> invoiceNumber;
    private List<String> partnerCode;
    private List<Long> lineNumber;
    private List<Long> statusId;
    private List<Long> invoiceHeaderId;
}
