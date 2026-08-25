package com.tekclover.wms.api.enterprise.transaction.model.threepl.proformainvoiceline;

import lombok.Data;

import java.util.List;

@Data
public class FindProformaInvoiceLine {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> proformaBillNo;
    private List<String> partnerCode;
    private List<Long> lineNumber;
    private List<Long> statusId;
}