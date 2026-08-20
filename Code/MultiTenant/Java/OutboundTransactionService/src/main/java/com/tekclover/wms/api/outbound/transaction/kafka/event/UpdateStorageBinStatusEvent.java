package com.tekclover.wms.api.outbound.transaction.kafka.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStorageBinStatusEvent {

    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private Long statusId;
    private String storageBin;
    private String updatedBy;

}
