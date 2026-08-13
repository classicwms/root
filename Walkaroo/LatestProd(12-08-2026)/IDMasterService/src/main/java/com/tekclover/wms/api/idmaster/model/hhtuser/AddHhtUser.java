package com.tekclover.wms.api.idmaster.model.hhtuser;

import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class AddHhtUser {
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String userId;
	private String password;
	private String userName;
	private Long statusId;
	private Long levelId;
	private Boolean caseReceipts;
	private Boolean itemReceipts;
	private Boolean putaway;
	private Boolean transfer;
	private Boolean picking;
	private Boolean quality;
	private Boolean inventory;
	private Boolean customerReturn;
	private Boolean supplierReturn;
	private String referenceField1;
	private String referenceField2;
	private String referenceField3;
	private String referenceField4;
	private String referenceField5;
	private String referenceField6;
	private String referenceField7;
	private String referenceField8;
	private String referenceField9;
	private String referenceField10;
	private Long deletionIndicator;
	private List<String> orderType;

	private Date startDate;
	private Date endDate;
	private String userPresent;
	private String noOfDaysLeave;

	private String teamMember1;
	private String teamMember2;
	private String teamMember3;
	private String teamMember4;
	private String teamMember5;


}
