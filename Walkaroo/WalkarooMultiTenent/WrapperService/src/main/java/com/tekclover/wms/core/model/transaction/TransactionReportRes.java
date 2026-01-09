package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class TransactionReportRes {

    private String itemCode;
    private String huNumber;
    private Double invQty;
    private String inboundDocNo;
    private String outboundDocNo;
    private Double inboundQty;
    private Double outboundQty;
    private Double expectedInvQty;
    private Double variance;


}
