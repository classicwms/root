package com.tekclover.wms.core.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PutAwayPalletGroupResponse {

    private String refDocNumber;
    private String putAwayNumber;
    private String palletId;
    private Long groupCount;
    private String proposedStorageBin;
    private Date createdOn;
    private String createdBy;
}
