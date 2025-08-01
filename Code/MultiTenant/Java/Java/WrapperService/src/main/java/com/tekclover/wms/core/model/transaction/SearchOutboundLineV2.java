package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SearchOutboundLineV2 extends SearchOutboundLine {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> manufacturerName;
	private List<String> targetBranchCode;
	private List<String> salesOrderNumber;
	//-----------------Reeferon------------------------------------
	private List<String> materialNo;
	private List<String> priceSegment;
	private List<String> articleNo;
	private List<String> gender;
	private List<String> color;
	private List<String> size;
	private List<String> noPairs;
	private List<String> barcodeId;
}