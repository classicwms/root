package com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class PerpetualHeaderEntity { 
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long cycleCountTypeId;
	private String cycleCountNo;
	private Long movementTypeId;
	private Long subMovementTypeId;
	private Long statusId;
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
	private String countedBy;
	private Date countedOn;
	private String confirmedBy;
	private Date confirmedOn;
	
	private List<PerpetualLineEntity> perpetualLine;
}
