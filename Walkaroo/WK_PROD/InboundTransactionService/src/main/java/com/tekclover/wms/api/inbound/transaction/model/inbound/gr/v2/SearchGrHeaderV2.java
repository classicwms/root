package com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2;

import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.SearchGrHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchGrHeaderV2 extends SearchGrHeader {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
}
