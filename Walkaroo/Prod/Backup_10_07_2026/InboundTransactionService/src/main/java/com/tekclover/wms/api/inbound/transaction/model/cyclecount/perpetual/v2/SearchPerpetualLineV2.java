package com.tekclover.wms.api.inbound.transaction.model.cyclecount.perpetual.v2;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString(callSuper = true)
public class SearchPerpetualLineV2 {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;

	private List<String> cycleCountNo;
	private List<Long> lineStatusId;
	private String cycleCounterId;
	private String warehouseId;
	private Date startCreatedOn;
	private Date endCreatedOn;

	private List<String> itemCode;
	private List<String> storageBin;
	private List<String> packBarcodes;
	private List<Long> stockTypeId;
	private List<String> manufacturerPartNo;
	private List<String> storageSectionId;
}