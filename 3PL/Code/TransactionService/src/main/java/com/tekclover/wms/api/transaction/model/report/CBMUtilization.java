package com.tekclover.wms.api.transaction.model.report;

public interface CBMUtilization {

     String getPartnerCode();
     String getPartnerName();
     Double getTotalCbm();
     Double getTotalConfirmedQty();

     Double getInboundConfirmedQty();
     Double getInboundTotalThreePLCbm();
     Double getOutboundConfirmedQty();
     Double getOutboundTotalThreePLCbm();


}
