package com.tekclover.wms.api.inbound.transaction.model.inbound.v2;

import com.tekclover.wms.api.inbound.transaction.model.inbound.SearchInboundHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchInboundHeaderV2 extends SearchInboundHeader {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> customerId;

}
