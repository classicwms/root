package com.tekclover.wms.api.transaction.model.cyclecount.perpetual;

import java.util.Date;

import lombok.Data;

@Data
public class UpdatePerpetualHeader {

	private String companyCodeId;
	private String palntId;
	private String warehouseId;
	private Long cycleCountTypeId;
	private String cycleCountNo;
	private Long movementTypeId;
	private Long subMovementTypeId;
	private String statusId;
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
	private Date createdOn = new Date();
	private String countedBy;
	private Date countedOn = new Date();
	private String confirmedBy;
	private Date confirmedOn = new Date();
}
