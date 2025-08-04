package com.tekclover.wms.api.transaction.model.dto;

import lombok.Data;

public interface PutAwayStrategyDetails {

    String getMaxCapacity();
    String getSuperMaxCapacity();
    String getStorageBin();
}
