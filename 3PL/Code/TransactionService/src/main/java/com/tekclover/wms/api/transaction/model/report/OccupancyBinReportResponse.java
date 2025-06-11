package com.tekclover.wms.api.transaction.model.report;

public interface OccupancyBinReportResponse {

     String getPartnerId();

     String getPartnerName();
    
     Long getOccupiedBin();

    String getPartnerCode();
  //  String getPartnerName();
//    Double getTotalCbm();
//    Double getTotalConfirmedQty();

    Double getInboundConfirmedQty();
    Double getInboundTotalThreePLCbm();
    Double getOutboundConfirmedQty();
    Double getOutboundTotalThreePLCbm();


}
