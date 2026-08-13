package com.tekclover.wms.api.transaction.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@EqualsAndHashCode(callSuper = true)
@Data
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