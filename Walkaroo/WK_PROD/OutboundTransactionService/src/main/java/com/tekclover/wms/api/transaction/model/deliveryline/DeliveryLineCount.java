package com.tekclover.wms.api.transaction.model.deliveryline;


import lombok.Data;

@Data
public class DeliveryLineCount {

    private Long newCount;
    private Long inTransitCount;
    private Long completedCount;
    private Long redeliveryCount;


}
