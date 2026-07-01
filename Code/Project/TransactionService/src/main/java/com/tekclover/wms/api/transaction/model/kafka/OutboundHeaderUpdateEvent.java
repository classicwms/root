package com.tekclover.wms.api.transaction.model.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutboundHeaderUpdateEvent {
    private String warehouseId;
    private String refDocNumber;
    private Long statusId;
    private Date deliveryConfirmedOn;
}
