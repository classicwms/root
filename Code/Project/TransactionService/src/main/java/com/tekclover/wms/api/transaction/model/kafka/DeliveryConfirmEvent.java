package com.tekclover.wms.api.transaction.model.kafka;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryConfirmEvent {
    private String warehouseId;
    private String preOutboundNo;
    private String refDocNumber;
    private String partnerCode;
}
