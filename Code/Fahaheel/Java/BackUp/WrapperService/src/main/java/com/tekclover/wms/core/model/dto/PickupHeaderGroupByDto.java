package com.tekclover.wms.core.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PickupHeaderGroupByDto {

    private String refDocNumber;
    private String itemCode;
    private String referenceField5;
    private Double bagSize;
    private Double pickToQty;
    private String proposedStorageBin;
    private String partnerCode;
    private Date pickupCreatedOn;
    private Double totalPickToQty;
    private Long totalBags;
    private Double mrp;
}
