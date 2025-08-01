package com.tekclover.wms.api.outbound.transaction.model.driverremark;


import lombok.Data;

import java.io.Serializable;

@Data
public class DriverRemarkCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /*
    *  'DRIVER_REMARK_NO' , 'PRE_OB_NO' , 'REF_DOC_NO'
     */

    private String driverRemarkNo;
    private String preOutboundNo;
    private String refDocNumber;
}
