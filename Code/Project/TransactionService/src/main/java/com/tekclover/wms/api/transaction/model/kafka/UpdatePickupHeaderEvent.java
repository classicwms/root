package com.tekclover.wms.api.transaction.model.kafka;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePickupHeaderEvent {

    private String warehouseId;
    private String preOutboundNo;
    private String refDocNo;
    private String partnerCode;
    private String pickupNumber;
    private Long statusId;


}
