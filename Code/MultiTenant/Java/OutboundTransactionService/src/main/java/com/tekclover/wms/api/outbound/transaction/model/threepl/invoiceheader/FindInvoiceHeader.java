package com.tekclover.wms.api.outbound.transaction.model.threepl.invoiceheader;

import lombok.Data;

import java.util.List;

@Data
public class FindInvoiceHeader {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> invoiceNumber;
    private List<String> partnerCode;
    private List<Long> statusId;
}
