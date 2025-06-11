package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

@Data
public class CBMBinReport {

    public String partnerId;
    public String partnerName;
    public Long numbersOfBin;
    public Long numbersOfCBM;

}
