package com.tekclover.wms.api.masters.model.businesspartner.v2;

import com.tekclover.wms.api.masters.model.businesspartner.BusinessPartner;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Data
@ToString(callSuper = true)
public class BusinessPartnerV2 extends BusinessPartner {

    @Column(name = "ALTERNATE_PHONE_NUMBER")
    private String alternatePhoneNumber;
    @Column(name = "CIVIL_ID")
    private String civilId;

    @Column(name = "IS_NEW")
    private String isNew;

    @Column(name = "IS_UPDATE")
    private String isUpdate;

    @Column(name = "IS_COMPLETED")
    private String isCompleted;

    @Column(name = "REMARKS")
    private String remarks;
    //MiddleWare Fields
    @Column(name = "MIDDLEWARE_ID")
    private Long middlewareId;

    @Column(name = "MIDDLEWARE_TABLE")
    private String middlewareTable;
}