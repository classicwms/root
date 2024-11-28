package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchMasterOperation {

	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> languageId;
	private List<String> warehouseId;
	private List<String> operationNumber;
	private List<String> phaseNumber;
	private List<String> workCenterId;
	private List<Long> statusId;
	
	private Date startCreatedOn;
	private Date endCreatedOn;
}