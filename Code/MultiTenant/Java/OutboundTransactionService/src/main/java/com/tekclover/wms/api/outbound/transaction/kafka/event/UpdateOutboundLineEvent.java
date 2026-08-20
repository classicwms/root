package com.tekclover.wms.api.outbound.transaction.kafka.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOutboundLineEvent {

    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String preOutboundNo;
    private String refDocNumber;
    private String itemCode;
    private Long lineNumber;
    private String referenceField6;

}
