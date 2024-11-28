package com.tekclover.wms.core.model.warehouse.mastersorder;

import lombok.Data;

@Data
public class Customer {

    private String companyCode;

    private String branchCode;

    private String partnerCode;

    private String partnerName;

    private String address1;

    private String address2;

    private String phoneNumber;

    private String civilId;

    private String country;

    private String alternatePhoneNumber;

    private String createdBy;

    private String createdOn;
}
