package com.tekclover.wms.api.inbound.orders.model.inbound.strategies;

import lombok.Data;

@Data
public class FindOutboundStrategies {

    private Long inboundStrategiesId;

    private Long inventoryId;

    private String languageId;

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private String palletCode;

    private String itemCode;

    private String refDocNumber;
    private Long sequenceNo;
}
