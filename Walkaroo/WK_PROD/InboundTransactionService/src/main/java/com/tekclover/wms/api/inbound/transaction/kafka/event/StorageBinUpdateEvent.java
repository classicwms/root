package com.tekclover.wms.api.inbound.transaction.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class StorageBinUpdateEvent {

    private String companyCode;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String storageBin;
    private Long binClassId;
}
