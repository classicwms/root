package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PerpetualLineV2 extends PerpetualLine {

	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;
	private String statusDescription;
	private String manufacturerName;
	private String middlewareId;
	private String middlewareHeaderId;
	private String middlewareTable;
	private String manufacturerFullName;
	private String referenceDocumentType;
	private Double frozenQty;
	private String barcodeId;
	private String manufacturerCode;

	private Double inboundQuantity;
	private Double outboundQuantity;
	private Double firstCountedQty;
	private Double secondCountedQty;
	private String levelId;
	private Double amsVarianceQty;
	private Long lineNo;
}