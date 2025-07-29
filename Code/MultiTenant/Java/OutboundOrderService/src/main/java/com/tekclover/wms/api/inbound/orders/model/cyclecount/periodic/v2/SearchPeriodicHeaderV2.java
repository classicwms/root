package com.tekclover.wms.api.inbound.orders.model.cyclecount.periodic.v2;

import com.tekclover.wms.api.inbound.orders.model.cyclecount.periodic.SearchPeriodicHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchPeriodicHeaderV2 extends SearchPeriodicHeader {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
}