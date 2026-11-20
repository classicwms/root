package com.tekclover.wms.api.transaction.model.inbound.putaway.v2;

import com.tekclover.wms.api.transaction.model.inbound.putaway.SearchPutAwayHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchPutAwayHeaderV2 extends SearchPutAwayHeader {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;

	private List<String> barcodeId;
	private List<String> manufacturerCode;
	private List<String> manufacturerName;
	private List<String> origin;
	private List<String> brand;
	private List<String> approvalStatus;
	private List<String> itemCode;				//Ref_field_5
	private List<String> preInboundNo;
	private List<Long> inboundOrderTypeId;

	/*----------------Walkaroo changes------------------------------------------------------*/
	private List<String> materialNo;
	private List<String> priceSegment;
	private List<String> articleNo;
	private List<String> gender;
	private List<String> color;
	private List<String> size;
	private List<String> noPairs;
	private List<String> palletId;
}
