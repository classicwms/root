package com.tekclover.wms.api.inbound.transaction.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PutAwayHeaderUpdateEvent {

    private String companyCode;
    private String plantId;
    private String languageId;
    private String warehouseId;

    private List<String> barcodeIds;
}
