package com.tekclover.wms.api.inbound.transaction.model.dto;

import lombok.Data;

public interface PutAwayStrategyDetails {

    String getMaxCapacity();
    String getSuperMaxCapacity();
    String getStorageBin();
}
