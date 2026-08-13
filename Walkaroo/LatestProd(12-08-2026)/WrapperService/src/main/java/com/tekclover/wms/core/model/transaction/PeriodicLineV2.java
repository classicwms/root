package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PeriodicLineV2 extends PeriodicLine {

	private String companyDescription;

	private String plantDescription;

	private String warehouseDescription;

	private String statusDescription;

	private String manufacturerName;

	private String itemDesc;

	private String storageSectionId;

	private String manufacturerPartNo;

//	@Column(name = "SP_ST_IND_ID")
//	private Long specialStockIndicator;

	private String middlewareId;

	private String middlewareHeaderId;

	private String middlewareTable;

	private String manufacturerFullName;

	private String referenceDocumentType;
	private String manufacturerCode;

	private Double frozenQty;
	private String barcodeId;

	private Double inboundQuantity;

	private Double outboundQuantity;

	private Double firstCountedQty;

	private Double secondCountedQty;
	private String levelId;
	private Double amsVarianceQty;
	private Long lineNo;
}