package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class SearchDeliveryHeader {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> deliveryNo;
    private List<Long> statusId;
    private List<String> remarks;
    private List<String> refDocNumber;
}
