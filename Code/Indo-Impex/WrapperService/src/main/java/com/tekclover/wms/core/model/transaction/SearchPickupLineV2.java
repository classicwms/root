package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SearchPickupLineV2 extends SearchPickupLine {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<Long> levelId;
	private List<Long> outboundOrderTypeId;
}