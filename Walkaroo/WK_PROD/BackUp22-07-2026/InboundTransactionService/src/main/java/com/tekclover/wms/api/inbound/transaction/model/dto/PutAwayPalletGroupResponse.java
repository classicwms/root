package com.tekclover.wms.api.inbound.transaction.model.dto;

import java.util.Date;

public interface PutAwayPalletGroupResponse {

    String getRefDocNumber();
     String getPutAwayNumber();
     String getPalletId();
     Long getGroupCount();
     String getProposedStorageBin();
     String getAssignedUserId();
     Date getCreatedOn();
     String getCreatedBy();
}
