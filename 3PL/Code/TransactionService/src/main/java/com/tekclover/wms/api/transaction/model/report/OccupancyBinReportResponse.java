package com.tekclover.wms.api.transaction.model.report;

public interface OccupancyBinReportResponse {

    public String getPartnerId();

    public String getPartnerName();
    
    public Long getOccupiedBin();
}
