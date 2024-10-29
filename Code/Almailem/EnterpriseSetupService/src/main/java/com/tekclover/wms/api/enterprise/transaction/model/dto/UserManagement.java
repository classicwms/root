package com.tekclover.wms.api.enterprise.transaction.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class UserManagement {

    private String userId;
	private String languageId;
	private String companyCode;
	private String plantID;
	private String warehouseId;
	private Long userRoleId;
	private Long userTypeId;
    private String password;
    private String userName;
    private String firstName;
    private String lastName;
	private Long statusId;
   	private Long dateFormatID;
   	private Long currencyDecimal;
    private String timeZone;
    private String emailID;
	private Long deletionIndicator = 0L;
	private String createdBy;
    private Date createdOn = new Date();
    private String updatedBy;
	private Date updatedOn = new Date();
}