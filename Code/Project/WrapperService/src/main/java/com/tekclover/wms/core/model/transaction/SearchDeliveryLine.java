package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class SearchDeliveryLine  {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> deliveryNo;
    private List<String> itemCode;
    private List<Long> lineNo;
    private List<String> refDocNumber;
    private List<String> invoiceNumber;
}
