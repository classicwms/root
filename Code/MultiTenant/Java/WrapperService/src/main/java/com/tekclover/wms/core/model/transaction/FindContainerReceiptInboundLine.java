package com.tekclover.wms.core.model.transaction;

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

    public Date fromDate;
    public Date toDate;
}
