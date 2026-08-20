package com.tekclover.wms.api.outbound.transaction.kafka.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class updateObHeaderPreObHeaderEvent {

    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String preOutboundNo;
    private String refDocNumber;
    private String itemCode;
}
