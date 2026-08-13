package com.tekclover.wms.api.idmaster.model.processsequenceid;

import java.util.Date;

import lombok.Data;

import javax.persistence.Column;

@Data
public class AddProcessSequenceId {
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String processId;
	private Long processSequenceId;
	private String languageId;
	private String processDescription;
	private String subProcessDescription;
	private Long deletionIndicator;
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

}
