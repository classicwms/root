package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QualityHeaderV2 extends QualityHeader {

	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;
	private String statusDescription;

	private String middlewareId;
	private String middlewareTable;
	private String referenceDocumentType;
	private String salesOrderNumber;
	private String manufacturerName;
	private String supplierInvoiceNo;
	private String salesInvoiceNumber;
	private String pickListNumber;
	private String tokenNumber;
	private String targetBranchCode;
	private String batchSerialNumber;

	private String customerId;
	private String customerName;

	/*----------------Walkaroo changes------------------------------------------------------*/
	private String materialNo;
	private String priceSegment;
	private String articleNo;
	private String gender;
	private String color;
	private String size;
	private String noPairs;

	private String shipToCode;
	private String shipToParty;
	private String specialStock;
	private String mtoNumber;
}