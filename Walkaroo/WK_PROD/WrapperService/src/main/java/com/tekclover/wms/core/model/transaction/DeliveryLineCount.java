package com.tekclover.wms.core.model.transaction;


import lombok.Data;

@Data
public class DeliveryLineCount {

    private Long newCount;
    private Long inTransitCount;
    private Long completedCount;
    private Long redeliveryCount;


}
