package com.tekclover.wms.api.idmaster.model.hhtuser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class HhtUserOutput {

	private String userId;
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long levelId;
	private String companyIdAndDescription;
	private String plantIdAndDescription;
	private String warehouseIdAndDescription;
	private String levelIdAndDescription;

	private List<String> orderType;

	private String password;
	private String userName;
	private Long statusId;
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
	private String createdBy;
	private Date createdOn;
	private String updatedBy;
	private Date updatedOn;

	private Date startDate;
	private Date endDate;
	private String userPresent;
	private String noOfDaysLeave;
}
