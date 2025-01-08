package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchPickupLineV3 {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> preOutboundNo;
    private List<String> refDocNumber;
    private List<String> partnerCode;
    private List<Long> lineNumber;
    private List<String> pickupNumber;
    private List<String> itemCode;
    private List<String> actualHeNo;
    private List<String> pickedStorageBin;
    private List<String> pickedPackCode;
    private List<String> assignedPickerId;
    private List<Long> outboundOrderTypeId;
    private List<String> manufacturerName;
    private List<String> salesOrderNumber;
    private List<Long> inboundOrderTypeId;

    private List<String> levelId;
    private List<Long> statusId;
    private Date fromPickConfirmedOn;
    private Date toPickConfirmedOn;


}