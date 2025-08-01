package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

@Data
public class OccupancyBinReport {

    public String partnerId;

    public String partnerName;

    public Long totalBin;

    public Long occupiedBin;

    public Long emptyBin;
}
