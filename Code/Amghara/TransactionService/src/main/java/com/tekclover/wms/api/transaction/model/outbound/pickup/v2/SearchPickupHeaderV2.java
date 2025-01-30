package com.tekclover.wms.api.transaction.model.outbound.pickup.v2;

import com.tekclover.wms.api.transaction.model.outbound.pickup.SearchPickupHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchPickupHeaderV2 extends SearchPickupHeader {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> levelId;
	private List<String> preOutboundNo;
}