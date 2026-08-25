package com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.v2;

import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.SearchPerpetualHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchPerpetualHeaderV2 extends SearchPerpetualHeader {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	
}