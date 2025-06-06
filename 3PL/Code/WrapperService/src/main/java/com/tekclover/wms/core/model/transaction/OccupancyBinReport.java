package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class OccupancyBinReport {

    public Long totalBin;

    public Long occupiedBin;

    public Long emptyBin;
}
