package com.tekclover.wms.api.transaction.model.threepl.invoiceheader;

import lombok.Data;

import java.util.List;

@Data
public class FindInvoiceHeader {
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> invoiceNumber;
    private List<String> partnerCode;
    private List<Long> statusId;
}
