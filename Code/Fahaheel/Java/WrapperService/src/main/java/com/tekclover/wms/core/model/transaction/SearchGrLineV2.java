package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString(callSuper = true)
public class SearchGrLineV2 extends SearchGrLine {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;

	private List<String> barcodeId;
	private List<String> manufacturerCode;
	private List<String> origin;
	private List<String> interimStorageBin;
	private List<String> manufacturerName;
	private List<String> brand;
	private List<String> rejectType;
	private List<String> rejectReason;
	private List<Long> inboundOrderTypeId;

	private Date startCreatedOn;
	private Date endCreatedOn;
}
