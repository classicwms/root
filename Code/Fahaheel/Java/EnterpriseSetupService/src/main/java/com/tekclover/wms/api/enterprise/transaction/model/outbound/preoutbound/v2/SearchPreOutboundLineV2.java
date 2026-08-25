package com.tekclover.wms.api.enterprise.transaction.model.outbound.preoutbound.v2;

import com.tekclover.wms.api.enterprise.transaction.model.outbound.preoutbound.SearchPreOutboundLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchPreOutboundLineV2 extends SearchPreOutboundLine {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;

}