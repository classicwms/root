package com.tekclover.wms.api.outbound.transaction.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOutboundHeaderStatus {

    private String companyId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String refDocNo;
    private String preOutboundNo;
    private Long statusId;
    private String statusDescription;
    private String loginUserID;

}
