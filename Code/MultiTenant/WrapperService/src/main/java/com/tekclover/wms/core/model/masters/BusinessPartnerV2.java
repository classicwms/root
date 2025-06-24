package com.tekclover.wms.core.model.masters;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
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
}