package com.tekclover.wms.api.inbound.transaction.model.preoutbound.v2;

import com.tekclover.wms.api.inbound.transaction.model.preoutbound.SearchPreOutboundHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchPreOutboundHeaderV2 extends SearchPreOutboundHeader {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;

}