package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.util.List;

@Data
public class FindPickupHeaderV2 {

    private List<String> warehouseId;
    private List<String> refDocNumber;
    private List<String> partnerCode;
    private List<String> pickupNumber;
    private List<String> itemCode;
    private List<String> proposedStorageBin;
    private List<String> proposedPackCode;
    private List<Long> outboundOrderTypeId;
    private List<Long> statusId;
    private List<String> soType; // referenceField1;
    private List<String> assignedPickerId;
    private List<String> levelId;

    // V2 fields
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
}
