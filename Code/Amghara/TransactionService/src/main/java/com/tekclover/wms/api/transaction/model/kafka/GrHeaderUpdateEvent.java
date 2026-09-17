package com.tekclover.wms.api.transaction.model.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrHeaderUpdateEvent {

    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String refDocNo;
    private String preInboundNo;
    private Long statusId;
    private String statusText;

}
