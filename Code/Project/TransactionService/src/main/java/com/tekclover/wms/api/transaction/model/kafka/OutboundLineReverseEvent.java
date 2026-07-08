package com.tekclover.wms.api.transaction.model.kafka;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboundLineReverseEvent {
    private String warehouseId;
    private String refDocNumber;
    private String itemCode;
    private Long statusId;
    private String loginUserID;
}
