package com.tekclover.wms.api.transaction.model.kafka;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QualityHeaderUpdateEvent {

    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String qualityInspectionNo;
    private String statusDescription;
    private Long statusId;
    private String qualityCreatedBy;
}
