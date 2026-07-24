package com.tekclover.wms.api.inbound.transaction.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class IBHeaderReceivedLinesEvent {

    private Long receivedLines;
    private String companyCode;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String refDocNumber;
    private String preInboundNo;
}
