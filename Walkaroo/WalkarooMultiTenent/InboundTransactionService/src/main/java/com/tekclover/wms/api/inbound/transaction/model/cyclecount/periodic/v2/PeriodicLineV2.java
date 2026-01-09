package com.tekclover.wms.api.inbound.transaction.model.cyclecount.periodic.v2;

import com.tekclover.wms.api.inbound.transaction.model.cyclecount.periodic.PeriodicLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(callSuper = true)
public class PeriodicLineV2 extends PeriodicLine {

	@Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
	private String companyDescription;

	@Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
	private String plantDescription;

	@Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
	private String warehouseDescription;

	@Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
	private String statusDescription;

	@Column(name = "PARTNER_ITEM_BARCODE", columnDefinition = "nvarchar(255)")
	private String barcodeId;

	@Column(name = "ITM_TEXT", columnDefinition = "nvarchar(255)")
	private String itemDesc;

	@Column(name = "ST_SEC_ID")
	private String storageSectionId;

	@Column(name = "MFR_PART", columnDefinition = "nvarchar(255)")
	private String manufacturerPartNo;

//	@Column(name = "SP_ST_IND_ID")
//	private Long specialStockIndicator;

	@Column(name = "MIDDLEWARE_ID", columnDefinition = "nvarchar(50)")
	private String middlewareId;

	@Column(name = "MIDDLEWARE_HEADER_ID", columnDefinition = "nvarchar(50)")
	private String middlewareHeaderId;

	@Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
	private String middlewareTable;

	@Column(name = "MANUFACTURER_FULL_NAME", columnDefinition = "nvarchar(150)")
	private String manufacturerFullName;

	@Column(name = "MFR_CODE", columnDefinition = "nvarchar(255)")
	private String manufacturerCode;

	@Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
	private String referenceDocumentType;

	@Column(name = "FrozenQty")
	private Double frozenQty;

	@Column(name = "IB_QTY")
	private Double inboundQuantity;

	@Column(name = "OB_QTY")
	private Double outboundQuantity;

	@Column(name = "FIRST_CTD_QTY")
	private Double firstCountedQty;

	@Column(name = "SECOND_CTD_QTY")
	private Double secondCountedQty;

	@Column(name = "LEVEL_ID", columnDefinition = "nvarchar(255)")
	private String levelId;

	@Column(name = "AMS_VAR_QTY")
	private Double amsVarianceQty;
}