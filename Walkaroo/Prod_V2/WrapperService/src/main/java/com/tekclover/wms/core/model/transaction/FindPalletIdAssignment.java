package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class FindPalletIdAssignment {
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<Long> paId;
    private List<String> palletId;
    private List<Long> statusId;
}