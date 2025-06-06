package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;
@Data
public class CBMUtilizationReport {


    public String partnerCode;
    public String partnerName;
    public Double totalCbm;
    public Double totalConfirmedQty;

    public Double inboundConfirmedQty;
    public Double inboundTotalThreePLCbm;
    public Double outboundConfirmedQty;
    public Double outboundTotalThreePLCbm;





}
