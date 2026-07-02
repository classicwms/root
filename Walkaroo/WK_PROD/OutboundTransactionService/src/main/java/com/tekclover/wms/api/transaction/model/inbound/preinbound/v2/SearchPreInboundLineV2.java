package com.tekclover.wms.api.transaction.model.inbound.preinbound.v2;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchPreInboundLineV2{

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> warehouseId;
	private List<String> preInboundNo;
	private List<Long> inboundOrderTypeId;
	private List<Long> statusId;
	private List<Long> lineNo;
	private List<String> refDocNumber;
	private List<String> purchaseOrderNumber;
	private List<String> invoiceNo;
	private List<String> itemCode;
	private List<String> manufacturerName;

	private Date startRefDocDate;
	private Date endRefDocDate;

	private Date startCreatedOn;
	private Date endCreatedOn;

	/*----------------Walkaroo changes------------------------------------------------------*/
	private List<String> barcodeId;
	private List<String> materialNo;
	private List<String> priceSegment;
	private List<String> articleNo;
	private List<String> gender;
	private List<String> color;
	private List<String> size;
	private List<String> noPairs;
}
