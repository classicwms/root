package com.tekclover.wms.core.model.user;

import java.util.Date;

import lombok.Data;

import javax.persistence.Column;

@Data
public class UserManagement {

	private String userId;
	private String languageId;
	private String companyCode;
	private String plantId;
	private String warehouseId;
	private Long userRoleId;
	private Long userTypeId;
	private String userName;
	private String firstName;
	private String lastName;
	private String password;
	private Long statusId;
	private Long dateFormatId;
	private Long currencyDecimal;
	private String timeZone;
	private String emailId;
	private Long deletionIndicator = 0L;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();

	private Boolean portalLoggedIn;
	private Boolean hhtLoggedIn;
	private Boolean createHhtUser;
	private String companyIdAndDescription;

	private String plantIdAndDescription;

	private String warehouseIdAndDescription;

	private String userRoleIdAndDescription;
	private String userTypeIdAndDescription;
}
