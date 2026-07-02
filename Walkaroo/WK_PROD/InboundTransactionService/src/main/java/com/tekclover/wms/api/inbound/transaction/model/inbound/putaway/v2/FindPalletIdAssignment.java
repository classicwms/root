package com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2;

import lombok.Data;

import java.util.List;
@Data
public class FindPalletIdAssignment {
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> palletId;
    private List<String> assignedUserId;
    private List<Long> paId;
    private List<Long> statusId;
}