package com.tekclover.wms.core.model.masters;

import lombok.Data;

@Data
public class BusinessPartnerV2 extends BusinessPartner {

    private String alternatePhoneNumber;
    private String civilId;

    private String isNew;
    private String isUpdate;
    private String isCompleted;
    private String remarks;
    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
    private String gst;
}