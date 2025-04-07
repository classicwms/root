package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class PickerDenialSummary {

	private String partnerCode;
	private String partnerName;
	private String orderType;
	private Long totalSKU;
	private Long outOfStock;
	private Long shortQty;
	private Long damage;
	private Long aisleBlock;
	private Long nonPackQty;
//	List<PickerDenialRemarkSummary> remarks;

}