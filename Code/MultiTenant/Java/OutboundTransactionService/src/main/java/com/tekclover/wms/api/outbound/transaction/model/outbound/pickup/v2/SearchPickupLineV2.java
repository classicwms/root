package com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2;

import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.SearchPickupLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchPickupLineV2 extends SearchPickupLine {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<Long> levelId;
	private List<Long> outboundOrderTypeId;
	private List<String> assignedPickerId;

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