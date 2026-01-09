package com.tekclover.wms.api.transaction.model.inbound.staging.v2;

import com.tekclover.wms.api.transaction.model.inbound.staging.SearchStagingLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchStagingLineV2 extends SearchStagingLine {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;

	private List<String> manufacturerCode;
	private List<String> manufacturerName;
	private List<String> origin;
	private List<String> brand;

	/*----------------Walkaroo changes------------------------------------------------------*/
	private List<String> materialNo;
	private List<String> priceSegment;
	private List<String> articleNo;
	private List<String> gender;
	private List<String> color;
	private List<String> size;
	private List<String> noPairs;
	private List<String> barcodeId;
	private List<String> vehicleNumber;
	private Date startCreatedOn;
	private Date endCreatedOn;

}
