package com.tekclover.wms.api.outbound.transaction.kafka.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOutboundLineStatusEvent {

    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String languageId;
    private String preOutboundNo;
    private String refDocNumber;
    private String itemCode;
    private String manufacturerName;
    private String partnerCode;
    private String actualHeNo;
    private String assignedPickerId;
    private Long lineNumber;
    private Long statusId;
    private String statusDescription;
//    private Date createdOn;
    private Double bagSize;
    private Double noBags;

}
