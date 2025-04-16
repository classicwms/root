package com.tekclover.wms.api.masters.model.businesspartner.v2;

import com.tekclover.wms.api.masters.model.businesspartner.BusinessPartner;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@Data
@ToString(callSuper = true)
public class BusinessPartnerV2 extends BusinessPartner {

    private String alternatePhoneNumber;
    private String civilId;
    private String gst;
    private String isNew;
    private String isUpdate;
    private String isCompleted;
    private String remarks;
    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
}