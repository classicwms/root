package com.tekclover.wms.api.transaction.model.kafka;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePickupHeaderEvent {

    private String refDocNumber;
    private String pickupNumber;
    private Long statusId;
    private String statusDescription;
    private String loginUserID;
}
