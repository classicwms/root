package com.tekclover.wms.api.inbound.orders.model.outbound.quality.v2;

import com.tekclover.wms.api.inbound.orders.model.outbound.quality.SearchQualityLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchQualityLineV2 extends SearchQualityLine {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;

}