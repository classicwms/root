package com.tekclover.wms.api.transaction.model.kafka;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QualityHeaderReverseEvent {
    private String refDocNumber;
    private String warehouseId;
    private String pickupNumber;
    private String loginUserID;
}
