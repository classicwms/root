package com.tekclover.wms.core.model.transaction;

import com.tekclover.wms.core.model.transaction.SearchInboundLine;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString(callSuper = true)
public class SearchInboundLineV2 extends SearchInboundLine {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<Long> inboundOrderTypeId;
	private List<String> sourceBranchCode;
	private List<String> sourceCompanyCode;

	private Date startCreatedOn;
	private Date endCreatedOn;
	private List<String> customerId;

	private List<String> erpStatus;

	private Date startConfirmedOn;
	private Date endConfirmedOn;

	private List<String> vehicleNo;
	private List<String> materialNo;

	private List<String> referenceDocumentType;
}
