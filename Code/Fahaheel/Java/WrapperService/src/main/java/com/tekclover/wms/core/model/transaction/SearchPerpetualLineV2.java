package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class SearchPerpetualLineV2 extends SearchPerpetualLine {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
}