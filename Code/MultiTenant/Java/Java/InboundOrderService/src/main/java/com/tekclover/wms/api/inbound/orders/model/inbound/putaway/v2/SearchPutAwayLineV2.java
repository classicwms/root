package com.tekclover.wms.api.inbound.orders.model.inbound.putaway.v2;

import com.tekclover.wms.api.inbound.orders.model.inbound.putaway.SearchPutAwayLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchPutAwayLineV2 extends SearchPutAwayLine {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;

	private List<String> barcodeId;
	private List<String> manufacturerCode;
	private List<String> manufacturerName;
	private List<String> origin;
	private List<String> brand;
	private List<Long> statusId;
	private List<Long> inboundOrderTypeId;
	private List<String> confirmedBy;

	/*----------------Walkaroo changes------------------------------------------------------*/
	private List<String> materialNo;
	private List<String> priceSegment;
	private List<String> articleNo;
	private List<String> gender;
	private List<String> color;
	private List<String> size;
	private List<String> noPairs;
}
