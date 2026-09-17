package com.tekclover.wms.api.transaction.model.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrLineUpdateEvent {

    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String preInboundNo;
    private String itemCode;
    private Long lineNo;
    private Date createdOn;
    private Long status;

}
