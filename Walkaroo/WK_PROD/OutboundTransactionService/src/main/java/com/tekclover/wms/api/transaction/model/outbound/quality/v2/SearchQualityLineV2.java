package com.tekclover.wms.api.transaction.model.outbound.quality.v2;

import com.tekclover.wms.api.transaction.model.outbound.quality.SearchQualityLine;
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

	/*----------------Walkaroo changes------------------------------------------------------*/
	private List<String> materialNo;
	private List<String> priceSegment;
	private List<String> articleNo;
	private List<String> gender;
	private List<String> color;
	private List<String> size;
	private List<String> noPairs;
	private List<String> barcodeId;

}