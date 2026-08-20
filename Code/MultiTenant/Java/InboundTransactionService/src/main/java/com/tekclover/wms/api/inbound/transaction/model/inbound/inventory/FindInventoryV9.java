package com.tekclover.wms.api.inbound.transaction.model.inbound.inventory;

import lombok.Data;

import java.util.List;

@Data
public class FindInventoryV9 {
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> itemCode;
    private List<String> warehouseId;
    private List<String> storageSecId;
    private List<Long> binClassId;


}
