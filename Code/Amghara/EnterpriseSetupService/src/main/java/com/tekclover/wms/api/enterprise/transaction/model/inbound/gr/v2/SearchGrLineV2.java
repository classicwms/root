package com.tekclover.wms.api.enterprise.transaction.model.inbound.gr.v2;

import com.tekclover.wms.api.enterprise.transaction.model.inbound.gr.SearchGrLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchGrLineV2 extends SearchGrLine {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> warehouseId;

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