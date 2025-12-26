package com.tekclover.wms.api.transaction.model.outbound.pickup.v2;

import com.tekclover.wms.api.transaction.model.outbound.pickup.SearchPickupLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchPickupLineV2 extends SearchPickupLine {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<Long> levelId;
	private List<Long> outboundOrderTypeId;
	private List<String> assignedPickerId;
}