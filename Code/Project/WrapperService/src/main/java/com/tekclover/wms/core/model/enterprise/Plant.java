package com.tekclover.wms.core.model.enterprise;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Plant {

    private String plantId;
    private String companyId;
    private String languageId;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private Long zipCode;
    private String contactName;
    private String designation;
    private String phoneNumber;
    private String eMail;
	private Long deletionIndicator = 0L;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
