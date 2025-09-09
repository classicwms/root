package com.tekclover.wms.api.transaction.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PutAwayPalletGroupResponse {

    private String refDocNumber;
    private String putAwayNumber;
    private String palletId;
    private Long groupCount;
    private String proposedStorageBin;
    private String assignedUserId;
    private Date createdOn;
    private String createdBy;
}
