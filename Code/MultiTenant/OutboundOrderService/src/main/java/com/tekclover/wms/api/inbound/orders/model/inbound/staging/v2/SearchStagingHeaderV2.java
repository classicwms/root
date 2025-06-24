package com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2;

import com.tekclover.wms.api.inbound.orders.model.inbound.staging.SearchStagingHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchStagingHeaderV2 extends SearchStagingHeader {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
}
