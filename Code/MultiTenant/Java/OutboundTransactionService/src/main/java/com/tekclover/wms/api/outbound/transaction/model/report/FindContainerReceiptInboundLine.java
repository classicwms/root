package com.tekclover.wms.api.outbound.transaction.model.report;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindContainerReceiptInboundLine {
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> refDocNumber;
    private List<String> itemCode;
    private List<String> barcodeId;
    private List<String> inventoryOwner;

    private Date fromDate;
    private Date toDate;
}
