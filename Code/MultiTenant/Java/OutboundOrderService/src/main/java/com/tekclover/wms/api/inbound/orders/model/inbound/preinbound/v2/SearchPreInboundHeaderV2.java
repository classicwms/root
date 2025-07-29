package com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2;

import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.SearchPreInboundHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchPreInboundHeaderV2 extends SearchPreInboundHeader {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
}
