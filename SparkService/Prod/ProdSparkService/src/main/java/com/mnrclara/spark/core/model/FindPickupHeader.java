package com.mnrclara.spark.core.model;

import lombok.Data;

import java.util.List;

@Data
public class FindPickupHeader {

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
}
