package com.tekclover.wms.api.outbound.transaction.model.dto;


import lombok.Data;

import java.util.Date;

@Data
public class PickListLoosePack {

    private String refDocNumber;
    private String itemCode;
    private String referenceField5;
    private String bagSize;
    private Double pickToQty;
    private String proposedStorageBin;
    private String partnerCode;
    private Date pickupCreatedOn;
    private Double totalPickToQty;
    private Long totalBags;
    private Double mrp;
}
