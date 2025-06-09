package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class CBMBinReport {

    public String partnerId;

    public String partnerName;

    public Long numbersOfBin;
    public Long numbersOfCBM;

}
