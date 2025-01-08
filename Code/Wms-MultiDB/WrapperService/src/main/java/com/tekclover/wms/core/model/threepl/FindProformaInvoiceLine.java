package com.tekclover.wms.core.model.threepl;

import lombok.Data;

import java.util.List;

@Data
public class FindProformaInvoiceLine {
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> proformaBillNo;
    private List<String> partnerCode;
    private List<Long> lineNumber;
    private List<Long> proformaHeaderId;
    private List<Long> statusId;
}
