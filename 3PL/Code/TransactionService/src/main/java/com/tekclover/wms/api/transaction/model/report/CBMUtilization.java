package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

@Data
public class CBMUtilization {

//     String getPartnerCode();
//     String getPartnerName();
//     Double getTotalCbm();
//     Double getTotalConfirmedQty();
//
//     Double getInboundConfirmedQty();
//     Double getInboundTotalThreePLCbm();
//     Double getOutboundConfirmedQty();
//     Double getOutboundTotalThreePLCbm();

     public String partnerCode;
     public String partnerName;
     public Double inboundConfirmedQty;
     public Double inboundTotalThreePLCbm;
     public Double outboundConfirmedQty;
     public Double outboundTotalThreePLCbm;
     public Double totalConfirmedQty;
     public Double totalCbm;
}
