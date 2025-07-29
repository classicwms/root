package com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2;

import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.SearchPickupHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchPickupHeaderV2 extends SearchPickupHeader {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> levelId;
	private List<String> preOutboundNo;

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