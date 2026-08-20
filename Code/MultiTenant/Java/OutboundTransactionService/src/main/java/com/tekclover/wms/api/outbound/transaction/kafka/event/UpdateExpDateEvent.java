package com.tekclover.wms.api.outbound.transaction.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExpDateEvent {

    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String referenceDocumentNo;
    private String itemCode;
    private String barcodeId;
    private Date expiryDate;

}
